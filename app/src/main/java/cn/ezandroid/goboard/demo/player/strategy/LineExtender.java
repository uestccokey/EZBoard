package cn.ezandroid.goboard.demo.player.strategy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import cn.ezandroid.goboard.demo.core.Chain;
import cn.ezandroid.goboard.demo.network.FeatureBoard;
import cn.ezandroid.goboard.demo.network.IPolicyNetwork;

/**
 * 落子链扩展器
 * <p>
 * 通过策略网络不断扩展（我方-敌方-我方-敌方...）落子链，直到达到设定的最大深度
 */
public class LineExtender implements Callable<FeatureBoard> {

    private FeatureBoard mBoard;
    private Line mLine;
    private IPolicyNetwork mPolicyNetwork;
    private int mMaxDepth;

    public LineExtender(FeatureBoard board, Line line, IPolicyNetwork policyNetwork, int maxDepth) {
        this.mBoard = board;
        this.mLine = line;
        this.mPolicyNetwork = policyNetwork;
        this.mMaxDepth = maxDepth;
    }

    @Override
    public FeatureBoard call() throws Exception {
        if (mLine == null || mLine.isEmpty()) {
            return null;
        }
        extendNextNode(mLine.getNode(0));
        return mBoard;
    }

    private void extendNextNode(Node parentNode) throws CloneNotSupportedException {
        if (parentNode.depth > mMaxDepth) {
            return;
        }

        Set<Chain> captured = new HashSet<>();
        mBoard.playMove(parentNode.pos % 19, parentNode.pos / 19, parentNode.color, captured);

        // 获取敌方落子概率数组
        float[][] policies = mPolicyNetwork.getOutput(mBoard);

        // 获取敌方最大落子概率节点
        int maxPos = -1;
        float maxPolicy = -1;
        for (int i = 0; i < policies[0].length; i++) {
            if (maxPolicy < policies[0][i]) {
                maxPolicy = policies[0][i];
                maxPos = i;
            }
        }
        Node bestPolicyNode = new Node();
        bestPolicyNode.pos = maxPos;
        bestPolicyNode.policy = maxPolicy;
        bestPolicyNode.color = (parentNode.color == FeatureBoard.WHITE) ? FeatureBoard.BLACK : FeatureBoard.WHITE;
        bestPolicyNode.depth = parentNode.depth + 1;

        // 加入落子链
        mLine.addNode(bestPolicyNode);

        // 继续扩展落子链
        extendNextNode(bestPolicyNode);
    }
}
