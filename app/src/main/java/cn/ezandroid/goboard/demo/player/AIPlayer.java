package cn.ezandroid.goboard.demo.player;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;
import cn.ezandroid.goboard.demo.core.Chain;
import cn.ezandroid.goboard.demo.network.AQ211Value;
import cn.ezandroid.goboard.demo.network.FeatureBoard;
import cn.ezandroid.goboard.demo.network.IPolicyNetwork;
import cn.ezandroid.goboard.demo.network.IValueNetwork;
import cn.ezandroid.goboard.demo.util.Debug;

/**
 * AIPlayer
 *
 * @author like
 * @date 2018-01-26
 */
public class AIPlayer implements IPlayer {

    private float mMinSumChooseThreshold = 0.98f; // 所有待选位置概率之和

    private float mMinChooseThreshold = 0.02f; // 最低待选位置概率阈值，落子概率低于此值的位置不考虑

    private IPolicyNetwork mPolicyNetwork;

    private IValueNetwork mValueNetwork;

    private FeatureBoard mFeatureBoard;

    private ExecutorService mValueExecutor = Executors.newCachedThreadPool();

    public AIPlayer(IPolicyNetwork policyNetwork, IValueNetwork valueNetwork) {
        mPolicyNetwork = policyNetwork;
        mValueNetwork = valueNetwork;
    }

    public void setFeatureBoard(FeatureBoard featureBoard) {
        mFeatureBoard = featureBoard;
    }

    public void setMinSumChooseThreshold(float minSumChooseThreshold) {
        mMinSumChooseThreshold = minSumChooseThreshold;
    }

    public void setMinChooseThreshold(float minChooseThreshold) {
        mMinChooseThreshold = minChooseThreshold;
    }

    public float[][] getPolicies() {
        return mPolicyNetwork.getOutput(mFeatureBoard);
    }

    @Override
    public Stone genMove(boolean player1) {
        Log.e("AIPlayer", player1 + " 手数:" + (mFeatureBoard.getCurrentMoveNumber() + 1));
        float[][] policies = getPolicies();
        Debug.printRate(policies[0]);

        List<Pair<Integer, Float>> all = new ArrayList<>();
        for (int i = 0; i < policies[0].length; i++) {
            all.add(new Pair<>(i, policies[0][i]));
        }

        Collections.sort(all, (o1, o2) -> {
            if (o1.second > o2.second) {
                return -1;
            } else if (o1.second < o2.second) {
                return 1;
            }
            return 0;
        });

        List<Pair<Integer, Float>> alternative = new ArrayList<>();
        float sumRate = 0;
        for (Pair<Integer, Float> pair : all) {
            float rate = pair.second;
            if (rate > mMinChooseThreshold) {
                alternative.add(pair);
                sumRate += rate;
            }
            if (sumRate > mMinSumChooseThreshold) {
                break;
            }
        }

        Pair<Integer, Float> choosePair = null;

        long time = System.currentTimeMillis();
        List<Callable<String>> valueTasks = new ArrayList<>();
        final byte[][][] featuresArray49 = new byte[alternative.size()][][];
        for (int i = 0; i < alternative.size(); i++) {
            final Pair<Integer, Float> pair = alternative.get(i);
            final int index = i;
            valueTasks.add(() -> {
                Set<Chain> captured = new HashSet<>();
                FeatureBoard clone = mFeatureBoard.clone();
                clone.playMove(pair.first % 19, pair.first / 19,
                        player1 ? FeatureBoard.BLACK : FeatureBoard.WHITE, captured);

                featuresArray49[index] = clone.generateFeatures49();
                return null;
            });
        }
        try {
            mValueExecutor.invokeAll(valueTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("AIPlayer", player1 + " 获取价值网络特征 数量:" + alternative.size() + " 耗时:" + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        float[] values = mValueNetwork.getOutput(featuresArray49, player1 ? AQ211Value.WHITE : AQ211Value.BLACK);
        Log.e("AIPlayer", player1 + " 进行价值网络评估 耗时:" + (System.currentTimeMillis() - time));

        float maxScore = -1;
        for (int i = 0; i < alternative.size(); i++) {
            final Pair<Integer, Float> pair = alternative.get(i);
            float score = (1 - values[i]) / 2 + pair.second / 8;
            if (maxScore < score) {
                maxScore = score;
                choosePair = pair;
            }
            Log.e("AIPlayer", player1 + " " + Debug.pos2str(pair.first)
                    + " 胜率:" + (1 - values[i]) / 2 + " 概率:" + pair.second + " 分数:" + score);
        }

        Log.e("AIPlayer", player1 + " 最终的选择:" + Debug.pos2str(choosePair.first));

        Stone stone = new Stone();
        stone.color = player1 ? StoneColor.BLACK : StoneColor.WHITE;
        stone.intersection = new Intersection(choosePair.first % 19, choosePair.first / 19);
        return stone;
    }
}
