package cn.ezandroid.goboard.demo.player;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;
import cn.ezandroid.goboard.demo.network.FeatureBoard;
import cn.ezandroid.goboard.demo.network.IPolicyNetwork;
import cn.ezandroid.goboard.demo.util.Debug;

/**
 * PolicyPlayer
 *
 * @author like
 * @date 2018-01-26
 */
public class PolicyPlayer implements IPlayer {

    private float mDirectChooseThreshold = 0.6f; // 当概率大于该阈值时，说明该位置只此一手，直接选择该位置

    private IPolicyNetwork mPolicyNetwork;

    private FeatureBoard mFeatureBoard;

    public PolicyPlayer(IPolicyNetwork policyNetwork) {
        mPolicyNetwork = policyNetwork;
    }

    public void setFeatureBoard(FeatureBoard featureBoard) {
        mFeatureBoard = featureBoard;
    }

    public void setDirectChooseThreshold(float directChooseThreshold) {
        mDirectChooseThreshold = directChooseThreshold;
    }

    public float[][] getPolicies() {
        return mPolicyNetwork.getOutput(mFeatureBoard);
    }

    @Override
    public Stone genMove(boolean player1) {
        float[][] policies = getPolicies();
        List<Pair<Integer, Float>> pairs = new ArrayList<>();
        float maxRate = -1;
        int maxPos = 0;
        for (int i = 0; i < policies[0].length; i++) {
            pairs.add(new Pair<>(i, policies[0][i]));
            if (policies[0][i] > maxRate) {
                maxRate = policies[0][i];
                maxPos = i;
            }
        }
        Debug.printRate(policies[0]);

        if (maxRate < mDirectChooseThreshold) {
            Collections.sort(pairs, (o1, o2) -> {
                if (o1.second > o2.second) {
                    return -1;
                } else if (o1.second < o2.second) {
                    return 1;
                }
                return 0;
            });

            Pair<Integer, Float> choosePair = null;
            Random random = new Random();
            float chooseValue = random.nextFloat();
            float sumValue = 0;
            for (Pair<Integer, Float> pair : pairs) {
                sumValue += pair.second;
                if (sumValue > chooseValue) {
                    choosePair = pair;
                    break;
                }
            }

            maxPos = choosePair.first;
        }

        Stone stone = new Stone();
        stone.color = player1 ? StoneColor.BLACK : StoneColor.WHITE;
        stone.intersection = new Intersection(maxPos % 19, maxPos / 19);
        return stone;
    }
}
