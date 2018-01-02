/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.analysis.territory.TerritoryAnalyzer;
import cn.ezandroid.game.board.go.analysis.territory.TerritoryUpdater;

/**
 * For statically evaluating the current state of a GoBoard.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public class WorthCalculator {

    private TerritoryAnalyzer territoryAnalyzer;
    private TerritoryUpdater territoryUpdater;

    /**
     * Constructor.
     */
    public WorthCalculator(GoBoard board, GroupAnalyzerMap analyzerMap) {
        territoryAnalyzer = new TerritoryAnalyzer(board, analyzerMap);
        territoryUpdater = new TerritoryUpdater(board, analyzerMap);
    }

    /**
     * Get estimate of territory for specified player.
     *
     * @param forPlayer1  the player to get the estimate for
     * @param isEndOfGame then we need the estimate to be more accurate.
     * @return estimate of size of territory for specified player.
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        return territoryAnalyzer.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * @return the estimated difference in territory between the 2 sides.
     */
    public float updateTerritoryAtEndOfGame() {
        return territoryUpdater.updateTerritory(true);
    }
}