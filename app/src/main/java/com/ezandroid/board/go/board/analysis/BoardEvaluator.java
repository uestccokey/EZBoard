/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.analysis;

import com.ezandroid.board.go.board.GoBoard;
import com.ezandroid.board.go.board.analysis.group.GroupAnalyzerMap;

/**
 * Responsible for evaluating groups and territory on a go board.
 * we keep a cache of the score for each unique board position to avoid recalculating.
 *
 * @author Barry Becker
 */
public final class BoardEvaluator {

    private WorthCalculator worthCalculator_;
    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor.
     */
    public BoardEvaluator(GoBoard board) {
        analyzerMap_ = new GroupAnalyzerMap();
        worthCalculator_ = new WorthCalculator(board, analyzerMap_);
    }

    /**
     * Get estimate of territory for specified player.
     *
     * @param forPlayer1  the player to get the estimate for
     * @param isEndOfGame then we need the estimate to be more accurate.
     * @return estimate of size of territory for specified player.
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        return worthCalculator_.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * @return the estimated difference in territory between the 2 sides.
     */
    public float updateTerritoryAtEndOfGame() {
        return worthCalculator_.updateTerritoryAtEndOfGame();
    }
}
