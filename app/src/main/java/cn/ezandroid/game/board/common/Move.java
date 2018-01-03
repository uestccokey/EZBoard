/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common;

/**
 * 落子模型
 *
 * @author Barry Becker
 */
public class Move implements Comparable<Move> {

    // 这手棋的价值
    private int mValue;

    protected Move() {}

    public Move(Move move) {
        mValue = move.mValue;
    }

    public Move copy() {
        return new Move(this);
    }

    @Override
    public int compareTo(Move move) {
        if (getValue() < move.getValue()) {
            return -1;
        } else if (getValue() > move.getValue()) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
    }
}

