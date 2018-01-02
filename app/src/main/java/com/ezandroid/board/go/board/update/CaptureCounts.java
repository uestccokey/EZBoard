/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.update;

import com.ezandroid.board.go.board.move.GoMove;

/**
 * Maintains the count of captured stones for each side.
 *
 * @author Barry Becker
 */
public class CaptureCounts {

    private int numWhiteStonesCaptured_ = 0;
    private int numBlackStonesCaptured_ = 0;

    public CaptureCounts copy() {
        CaptureCounts countsCopy = new CaptureCounts();
        countsCopy.numBlackStonesCaptured_ = this.numBlackStonesCaptured_;
        countsCopy.numWhiteStonesCaptured_ = this.numWhiteStonesCaptured_;
        return countsCopy;
    }

    public int getNumCaptures(boolean player1StonesCaptured) {
        return player1StonesCaptured ? numBlackStonesCaptured_ : numWhiteStonesCaptured_;
    }

    /**
     * @param move      the move just made or removed.
     * @param increment if true then add to number of captures, else subtract.
     */
    public void updateCaptures(GoMove move, boolean increment) {
        int numCaptures = move.getNumCaptures();
        int num = increment ? move.getNumCaptures() : -move.getNumCaptures();

        if (numCaptures > 0) {
            if (move.isPlayer1()) {
                numWhiteStonesCaptured_ += num;
                assert numWhiteStonesCaptured_ >= 0 :
                        "The number of captured white stones became less than 0 :  " + numWhiteStonesCaptured_;
            } else {
                numBlackStonesCaptured_ += num;
                assert numBlackStonesCaptured_ >= 0 :
                        "The number of captured black stones became less than 0 :  " + numBlackStonesCaptured_;
            }
        }
    }
}
