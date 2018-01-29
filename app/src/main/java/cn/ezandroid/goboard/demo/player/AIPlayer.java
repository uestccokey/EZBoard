package cn.ezandroid.goboard.demo.player;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;
import cn.ezandroid.goboard.demo.network.AQ211Value;
import cn.ezandroid.goboard.demo.network.FeatureBoard;
import cn.ezandroid.goboard.demo.network.IPolicyNetwork;
import cn.ezandroid.goboard.demo.network.IValueNetwork;
import cn.ezandroid.goboard.demo.player.strategy.Line;
import cn.ezandroid.goboard.demo.player.strategy.LineExtender;
import cn.ezandroid.goboard.demo.player.strategy.Node;
import cn.ezandroid.goboard.demo.util.Debug;

/**
 * AIPlayer
 *
 * @author like
 * @date 2018-01-28
 */
public class AIPlayer implements IPlayer {

    private IPolicyNetwork mPolicyNetwork;

    private IValueNetwork mValueNetwork;

    private int mMaxDepth = 4;

    private int mMaxWidth = 3;

    private FeatureBoard mFeatureBoard;

    private ExecutorService mValueExecutor = Executors.newCachedThreadPool();

    public AIPlayer(IPolicyNetwork policyNetwork, IValueNetwork valueNetwork) {
        mPolicyNetwork = policyNetwork;
        mValueNetwork = valueNetwork;
    }

    public void setFeatureBoard(FeatureBoard featureBoard) {
        mFeatureBoard = featureBoard;
    }

    public float[][] getPolicies() {
        return mPolicyNetwork.getOutput(mFeatureBoard);
    }

    public void setMaxDepth(int maxDepth) {
        mMaxDepth = maxDepth;
    }

    public void setMaxWidth(int maxWidth) {
        mMaxWidth = maxWidth;
    }

    @Override
    public Stone genMove(boolean player1) {
        long time = System.currentTimeMillis();
        float[][] policies = getPolicies();
        Debug.printRate(policies[0]);

        // 将策略网络获取到的落子概率数组转换为节点列表
        List<Node> all = new ArrayList<>();
        for (int i = 0; i < policies[0].length; i++) {
            Node node = new Node();
            node.pos = i;
            node.policy = policies[0][i];
            node.color = player1 ? FeatureBoard.BLACK : FeatureBoard.WHITE;
            all.add(node);
        }

        // 从大到小排序节点列表
        Collections.sort(all, (o1, o2) -> {
            if (o1.policy > o2.policy) {
                return -1;
            } else if (o1.policy < o2.policy) {
                return 1;
            }
            return 0;
        });

        // 当最佳策略节点落子概率大于0.95时，通常表示只此一手，直接落子，节约时间
        Node bestPolicyNode = all.get(0);
        if (bestPolicyNode.policy > 0.95f) {
            Stone stone = new Stone();
            stone.color = player1 ? StoneColor.BLACK : StoneColor.WHITE;
            stone.intersection = new Intersection(bestPolicyNode.pos % 19, bestPolicyNode.pos / 19);
            return stone;
        }

        // 查找所有符合条件的备选节点
        List<Node> alternative = new ArrayList<>();
        for (Node node : all) {
            float policy = node.policy;
            if (policy > 0.05f && alternative.size() < mMaxWidth) {
                alternative.add(node);
            }
        }

        // 遍历备选节点生成落子链
        List<Line> lines = new ArrayList<>();
        for (Node node : alternative) {
            Line line = new Line();
            line.addNode(node);
            lines.add(line);
        }

        // 每个落子链进行并行扩展，直到全部结束
        List<LineExtender> lineExtenders = new ArrayList<>();
        for (Line line : lines) {
            try {
                lineExtenders.add(new LineExtender(mFeatureBoard.clone(), line, mPolicyNetwork, mMaxDepth));
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        byte[][][] featuresArray49 = new byte[lineExtenders.size()][][];
        try {
            List<Future<FeatureBoard>> results = mValueExecutor.invokeAll(lineExtenders);
            for (int i = 0; i < results.size(); i++) {
                FeatureBoard board = results.get(i).get();
                featuresArray49[i] = board.generateFeatures49();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        float[] values = mValueNetwork.getOutput(featuresArray49, player1 ? AQ211Value.WHITE : AQ211Value.BLACK);

        Line bestLine = null;
        float bestValue = -1;
        for (int i = 0; i < values.length; i++) {
            float score = (1 - values[i]) / 2;
            Line line = lines.get(i);
            line.setScore(score);
            if (bestValue < score) {
                bestValue = score;
                bestLine = line;
            }
        }

        for (Line line : lines) {
            Log.e("AIPlayer", line.toString());
        }

        Node bestNode = bestLine.getNode(0);
        Log.e("AIPlayer", "最终选择:" + Debug.pos2str(bestNode.pos)
                + " 价值:" + bestLine.getScore()
                + " 颜色:" + bestNode.getPlayer()
                + " 手数:" + (mFeatureBoard.getCurrentMoveNumber() + 1)
                + " 用时:" + (System.currentTimeMillis() - time));

        Stone stone = new Stone();
        stone.color = player1 ? StoneColor.BLACK : StoneColor.WHITE;
        stone.intersection = new Intersection(bestNode.pos % 19, bestNode.pos / 19);
        return stone;
    }
}
