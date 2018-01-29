package cn.ezandroid.goboard.demo.player.strategy;

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 落子链
 */
public class Line {

    private List<Node> mNodes = new ArrayList<>();

    // 主要在打印日志时使用
    private TreeNode<Node> mTreeNode;

    private float mScore;

    public Line() {
    }

    @Override
    public String toString() {
        return root().toString() + "价值:" + mScore;
    }

    public void setScore(float score) {
        mScore = score;
    }

    public float getScore() {
        return mScore;
    }

    public void addNode(Node node) {
        TreeNode<Node> newTreeNode = new LinkedMultiTreeNode<>(node);
        if (mTreeNode == null) {
            mTreeNode = newTreeNode;
        } else {
            mTreeNode.add(newTreeNode);
            mTreeNode = newTreeNode;
        }
        mNodes.add(node);
    }

    public Node getNode(int index) {
        return mNodes.get(index);
    }

    public boolean isEmpty() {
        return mNodes.isEmpty();
    }

    public TreeNode<Node> root() {
        return mTreeNode.root();
    }
}
