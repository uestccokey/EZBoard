/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.update;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;

/**
 * Responsible for updating a go board after making or undoing a move.
 *
 * @author Barry Becker
 */
public class BoardUpdater {

    private PostMoveUpdater postMoveUpdater_;
    private PostRemoveUpdater postRemoveUpdater_;
    private CaptureCounts captureCounts_;

    /**
     * Essentially a copy constructor. The counts are preserved.
     *
     * @param board     board to update
     * @param capCounts current counts so they are not lost.
     */
    public BoardUpdater(GoBoard board, CaptureCounts capCounts) {
        captureCounts_ = capCounts;
        initialize(board);
    }

    /**
     * @return a defensive copy
     */
    public CaptureCounts getCaptureCounts() {
        return captureCounts_.copy();
    }

    private void initialize(GoBoard board) {
        postMoveUpdater_ = new PostMoveUpdater(board, captureCounts_);
        postRemoveUpdater_ = new PostRemoveUpdater(board, captureCounts_);
    }

    /**
     * @param player1StonesCaptured if true then get the black stones captured
     * @return the captured stones of the specified color
     */
    public int getNumCaptures(boolean player1StonesCaptured) {
        return captureCounts_.getNumCaptures(player1StonesCaptured);
    }

    /**
     * Update the board after move has been played.
     *
     * @param move the move that was just made
     */
    public void updateAfterMove(GoMove move) {
        postMoveUpdater_.update(move);
    }

    /**
     * Update the board after backing out a move.
     *
     * @param move the move that was just undone
     */
    public void updateAfterRemove(GoMove move) {
        postRemoveUpdater_.update(move);
    }
}
