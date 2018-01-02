/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import java.util.LinkedList;

import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * Keeps track of the number of dead stones of each color that are on the board.
 * At the very end of the game we visibly mark dead stones dead.
 *
 * @author Barry Becker
 */
final class DeadStones {

//    private int numDeadBlackStonesOnBoard_ = 0;
//    private int numDeadWhiteStonesOnBoard_ = 0;

    private LinkedList<GoBoardPosition> mDeadBlackStonesOnBoard = new LinkedList<>();
    private LinkedList<GoBoardPosition> mDeadWhiteStonesOnBoard = new LinkedList<>();

    /**
     * Constructor.
     */
    public DeadStones() {
    }

    public void clear() {
//        numDeadBlackStonesOnBoard_ = 0;
//        numDeadWhiteStonesOnBoard_ = 0;
        mDeadBlackStonesOnBoard.clear();
        mDeadWhiteStonesOnBoard.clear();
    }

    public LinkedList<GoBoardPosition> getDeadStonesOnBoard(boolean player1) {
        return player1 ? mDeadBlackStonesOnBoard : mDeadWhiteStonesOnBoard;
    }

    /**
     * @param player1 black player if true
     * @return the number of dead stones on the board for the specified player
     */
    public int getNumberOnBoard(boolean player1) {
        return player1 ? mDeadBlackStonesOnBoard.size() : mDeadWhiteStonesOnBoard.size();
    }

    /**
     * Add to the dead stone count for the specified player
     *
     * @param player1 player to add a dead stone for.
     */
    public void increment(GoBoardPosition position, boolean player1) {
        if (player1) {
//            numDeadBlackStonesOnBoard_++;
            mDeadBlackStonesOnBoard.add(position);
        } else {
//            numDeadWhiteStonesOnBoard_++;
            mDeadWhiteStonesOnBoard.add(position);
        }
    }

    public String toString() {
        return "Dead black stones: " + mDeadBlackStonesOnBoard.size()
                + "\nDead white stones: " + mDeadWhiteStonesOnBoard.size();
    }
}