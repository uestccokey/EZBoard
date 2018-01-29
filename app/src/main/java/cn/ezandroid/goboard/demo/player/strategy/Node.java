package cn.ezandroid.goboard.demo.player.strategy;

import cn.ezandroid.goboard.demo.network.FeatureBoard;
import cn.ezandroid.goboard.demo.util.Debug;

/**
 * 节点
 */
public class Node {

    public int pos = -1;
    public float policy;
    public byte color;
    public int depth;

    @Override
    public String toString() {
        if (pos == -1) {
            return "Root [颜色:" + getPlayer() + "]";
        }
        return Debug.pos2str(pos) + " [落子概率:" + (policy * 100) + "%" + " 颜色:" + getPlayer() + " 深度:" + depth + "]";
    }

    public String getPlayer() {
        if (color == FeatureBoard.BLACK) {
            return "Black";
        } else {
            return "White";
        }
    }
}
