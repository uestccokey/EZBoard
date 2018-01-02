// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.go.analysis.territory;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.GoProfiler;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * Analyzes territory on board.
 *
 * @author Barry Becker
 */
public class TerritoryUpdater {

    private GoBoard board_;

    /**
     * The difference between the 2 player's territory.
     * It is computed as black-white = sum(health of stone i)
     */
    private float territoryDelta_ = 0;

    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor
     *
     * @param board board to analyze
     */
    public TerritoryUpdater(GoBoard board, GroupAnalyzerMap analyzerMap) {
        board_ = board;
        analyzerMap_ = analyzerMap;
    }

    public float getTerritoryDelta() {
        return territoryDelta_;
    }

    /**
     * Loops through the groups to determine the territorial difference between the players.
     * Then it loops through and determines a score for positions that are not part of groups.
     * If a position is part of an area that borders only a living group, then it is considered
     * territory for that group's side. If, however, the position borders living groups from
     * both sides, then the score is weighted according to the proportion of the perimeter
     * that borders each living group and how alive those bordering groups are.
     * This is the primary factor in evaluating the board position for purposes of search.
     * This method and the methods it calls are the crux of this go playing program.
     *
     * @return the estimated difference in territory between the 2 sides.
     * A large positive number indicates black is winning, while a negative number indicates that white has the edge.
     */
    public float updateTerritory(boolean isEndOfGame) {
        clearScores();

        GoProfiler prof = GoProfiler.getInstance();
        prof.startUpdateTerritory();
        analyzerMap_.clear();  /// need?

        float delta = calcAbsoluteHealth();
        delta = calcRelativeHealth(prof, delta);
        prof.startUpdateEmpty();
        if (isEndOfGame) {
            EmptyRegionUpdater emptyUpdater = new EmptyRegionUpdater(board_, analyzerMap_);
            delta += emptyUpdater.updateEmptyRegions();
        }
        prof.stopUpdateEmpty();

        prof.stopUpdateTerritory();
        territoryDelta_ = delta;

        return delta;
    }

    /**
     * Clear whatever cached score state we might have before recomputing.
     */
    private void clearScores() {
        for (int i = 1; i <= board_.getNumRows(); i++) {
            for (int j = 1; j <= board_.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition(i, j);
                pos.setScoreContribution(0);

                if (pos.isOccupied()) {
                    GoStone stone = ((GoStone) pos.getPiece());
                    stone.setHealth(0);
                }
            }
        }
    }

    /**
     * First calculate the absolute health of the groups so that measure can
     * be used in the more accurate relative health computation.
     *
     * @return total health of all stones in all groups in absolute terms.
     */
    private float calcAbsoluteHealth() {
        float delta = 0;
        GoProfiler.getInstance().startAbsoluteTerritory();
        for (IGoGroup g : board_.getGroups()) {
            analyzerMap_.getAnalyzer(g).calculateAbsoluteHealth(board_);
        }
        GoProfiler.getInstance().stopAbsoluteTerritory();

        return delta;
    }

    /**
     * @param initDelta initial value.
     * @return total health of all stones in all groups in relative terms.
     */
    private float calcRelativeHealth(GoProfiler prof, float initDelta) {
        float delta = initDelta;
        prof.startRelativeTerritory();
        for (IGoGroup g : board_.getGroups()) {
            float health = analyzerMap_.getAnalyzer(g).calculateRelativeHealth(board_);
            g.updateTerritory(health);
            delta += health * g.getNumStones();
        }
        prof.stopRelativeTerritory();

        return delta;
    }
}