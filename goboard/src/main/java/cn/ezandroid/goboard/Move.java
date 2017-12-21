package cn.ezandroid.goboard;

import java.util.HashSet;
import java.util.Set;

/**
 * 落子模型
 *
 * @author like
 */
public class Move implements Cloneable {

    private Stone mStone; // 落子的棋子
    private Set<Chain> mCaptured; // 落子后提走的棋串集合
    private Intersection mKO; // 劫的位置

    public Move(Stone s, Set<Chain> c) {
        mStone = s;
        mCaptured = c;
    }

    @Override
    public Move clone() throws CloneNotSupportedException {
        Move move = (Move) super.clone();
        move.mStone = mStone.clone();
        move.mCaptured = new HashSet<>();
        for (Chain chain : mCaptured) {
            move.mCaptured.add(chain.clone());
        }
        if (mKO != null) {
            move.mKO = mKO.clone();
        }
        return move;
    }

    public Stone getStone() {
        return mStone;
    }

    public Set<Chain> getCaptured() {
        return mCaptured;
    }

    public void setKO(Intersection ko) {
        mKO = ko;
    }

    public Intersection getKO() {
        return mKO;
    }

    @Override
    public String toString() {
        return "Move{" +
                "mStone=" + mStone +
                ", mCaptured=" + mCaptured +
                ", mKO=" + mKO +
                '}';
    }
}
