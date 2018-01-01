/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.territory;

import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;

/**
 * Analyzes territory on board.
 *
 * @author Barry Becker
 */
public class TerritoryAnalyzer {

    private GoBoard board_;
    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor
     *
     * @param board board to analyze
     */
    public TerritoryAnalyzer(GoBoard board, GroupAnalyzerMap analyzerMap) {
        board_ = board;
        analyzerMap_ = analyzerMap;
    }

    /**
     * Get an estimate of the territory for the specified player.
     * This estimate is computed by summing all spaces in eyes with dead opponent stones that are still on the board.
     * Empty spaces are weighted by how likely they are to eventually be territory of one side or the other.
     * At the end of the game this + the number of pieces captured so far should give the true score.
     *
     * @param forPlayer1  player to get the estimate for.
     * @param isEndOfGame use 0 or 1 instead of pos.scoreContribution if true.
     * @return estimate of territory for forPlayer1
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        float territoryEstimate = 0;

        // we should be able to just sum all the position scores now.
        for (int i = 1; i <= board_.getNumRows(); i++) {
            for (int j = 1; j <= board_.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition(i, j);
                double val = getTerritoryEstimateForPosition(pos, forPlayer1, isEndOfGame);
                territoryEstimate += val;
//                if (forPlayer1 && val > 0) {
//                    Log.e("TA", pos + " " + val + " " + territoryEstimate);
//                }
            }
        }
        return (int) territoryEstimate;
    }

    /**
     * @return the estimate for a single position.
     */
    private float getTerritoryEstimateForPosition(GoBoardPosition pos, boolean forPlayer1,
                                                  boolean isEndOfGame) {
        double val = isEndOfGame ? (forPlayer1 ? 1.0 : -1.0) : pos.getScoreContribution();
        float territoryEstimate = 0;

        // the territory estimate will always be positive for both sides.
        if (pos.isUnoccupied()) {
            if (forPlayer1 && pos.getScoreContribution() > 0) {
                territoryEstimate += val;
            } else if (!forPlayer1 && pos.getScoreContribution() < 0) {
                territoryEstimate -= val;  // will be positive
            }
        } else { // occupied
            GamePiece piece = pos.getPiece();
            IGoGroup group = pos.getGroup();
            assert (piece != null);
            if (group != null) {
                // add credit for probable captured stones.
                double relHealth = analyzerMap_.getAnalyzer(group).getRelativeHealth(board_, isEndOfGame);
                if (forPlayer1 && !piece.isOwnedByPlayer1() && relHealth >= 0) {
                    territoryEstimate += val;
                } else if (!forPlayer1 && piece.isOwnedByPlayer1() && relHealth <= 0) {
                    territoryEstimate -= val;
                }
//                if (forPlayer1 && !piece.isOwnedByPlayer1() && relHealth >= -0.1) {
//                    territoryEstimate += val;
//                } else if (!forPlayer1 && piece.isOwnedByPlayer1() && relHealth <= 0.1) {
//                    territoryEstimate -= val;
//                }
            }
        }
        return territoryEstimate;
    }
}