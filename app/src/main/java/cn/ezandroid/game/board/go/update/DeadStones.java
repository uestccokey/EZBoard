/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import java.util.LinkedList;

import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * 跟踪并记录每方的死子
 *
 * @author Barry Becker
 */
final class DeadStones {

    private LinkedList<GoBoardPosition> mDeadBlackStonesOnBoard = new LinkedList<>();
    private LinkedList<GoBoardPosition> mDeadWhiteStonesOnBoard = new LinkedList<>();

    public DeadStones() {
    }

    /**
     * 清空记录的死子信息
     */
    public void clear() {
        mDeadBlackStonesOnBoard.clear();
        mDeadWhiteStonesOnBoard.clear();
    }

    /**
     * 获取指定玩家的死子
     *
     * @param player1
     * @return
     */
    public LinkedList<GoBoardPosition> getDeadStonesOnBoard(boolean player1) {
        return player1 ? mDeadBlackStonesOnBoard : mDeadWhiteStonesOnBoard;
    }

    /**
     * 获取指定玩家的死子数
     *
     * @param player1
     * @return
     */
    public int getNumberOnBoard(boolean player1) {
        return player1 ? mDeadBlackStonesOnBoard.size() : mDeadWhiteStonesOnBoard.size();
    }

    /**
     * 记录指定玩家的死子
     *
     * @param position
     * @param player1
     */
    public void increment(GoBoardPosition position, boolean player1) {
        if (player1) {
            mDeadBlackStonesOnBoard.add(position);
        } else {
            mDeadWhiteStonesOnBoard.add(position);
        }
    }

    public String toString() {
        return "Dead black stones: " + mDeadBlackStonesOnBoard.size()
                + "\nDead white stones: " + mDeadWhiteStonesOnBoard.size();
    }
}