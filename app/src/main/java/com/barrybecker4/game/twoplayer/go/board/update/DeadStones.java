/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.update;

/**
 * Keeps track of the number of dead stones of each color that are on the board.
 * At the very end of the game we visibly mark dead stones dead.
 *
 * @author Barry Becker
 */
final class DeadStones {

    private int numDeadBlackStonesOnBoard_ = 0;
    private int numDeadWhiteStonesOnBoard_ = 0;

    /**
     * Constructor.
     */
    public DeadStones() {
    }

    public void clear() {
        numDeadBlackStonesOnBoard_ = 0;
        numDeadWhiteStonesOnBoard_ = 0;
    }

    /**
     * @param player1 black player if true
     * @return the number of dead stones on the board for the specified player
     */
    public int getNumberOnBoard(boolean player1) {
        return player1 ? numDeadBlackStonesOnBoard_ : numDeadWhiteStonesOnBoard_;
    }

    /**
     * Add to the dead stone count for the specified player
     *
     * @param player1 player to add a dead stone for.
     */
    public void increment(boolean player1) {
        if (player1) {
            numDeadBlackStonesOnBoard_++;
        } else {
            numDeadWhiteStonesOnBoard_++;
        }
    }

    public String toString() {
        return "Dead black stones: " + numDeadBlackStonesOnBoard_
                + "\nDead white stones: " + numDeadWhiteStonesOnBoard_;
    }
}