// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.go.analysis.territory;

import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionLists;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * Updates the empty regions on the board at the end of the game.
 * Used to determine ownership of those empty regions.
 * Does it handle dame's correctly?
 *
 * @author Barry Becker
 */
public class EmptyRegionUpdater {

    /**
     * How much score to attribute to a stone that is in an eye.
     * I'm really not sure what this should be. Perhaps -0.1 instead of 0.1.
     */
    private static final float STONE_IN_EYE_CONTRIB = 0.1f;

    private GoBoard board_;
    private NeighborAnalyzer nbrAnalyzer_;
    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor
     *
     * @param board board to analyze
     */
    public EmptyRegionUpdater(GoBoard board, GroupAnalyzerMap analyzerMap) {
        board_ = board;
        nbrAnalyzer_ = new NeighborAnalyzer(board);
        analyzerMap_ = analyzerMap;
    }

    /**
     * Need to loop over the board and determine for each space if it is territory for the specified player.
     * We will first mark visited all the stones that are "controlled" by the specified player.
     * The unoccupied "controlled" positions will be territory.
     *
     * @return the change in score after updating the empty regions.
     */
    public float updateEmptyRegions() {
        float diffScore = 0;

        if (board_.getMoveList().getNumMoves() <= 2 * board_.getNumRows()) {
            return diffScore;
        }
        // later in the game we can take the analysis all the way to the edge.
        //float ratio = (float)board_.getMoveList().getNumMoves() / board_.getTypicalNumMoves();

        int min = 1;
        int rMax = board_.getNumRows();
        int cMax = board_.getNumCols();
        Box box = new Box(min, min, rMax, cMax);

        GoBoardPositionLists emptyLists = new GoBoardPositionLists();

        for (int i = min; i <= rMax; i++) {
            for (int j = min; j <= cMax; j++) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition(i, j);
                diffScore += updateEmptyRegionFromSeed(box, emptyLists, pos);
            }
        }

        emptyLists.unvisitPositionsInLists();
        return diffScore;
    }

    /**
     * Update diffScore for the string connected to pos and mark it visited.
     * If pos is in an eye, update the score contribution for that eye space.
     *
     * @return diffScore value.
     */
    private float updateEmptyRegionFromSeed(Box box, GoBoardPositionLists emptyLists,
                                            GoBoardPosition pos) {
        float diffScore = 0;
        if (pos.getString() == null && !pos.isInEye()) {
            assert pos.isUnoccupied();
            if (!pos.isVisited()) {

                // don't go all the way to the borders (until the end of the game),
                // since otherwise we will likely get only one big empty region.
                GoBoardPositionList empties =
                        nbrAnalyzer_.findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED, box);
                emptyLists.add(empties);

                GoBoardPositionSet nbrs = nbrAnalyzer_.findOccupiedNobiNeighbors(empties);
                float avg = calcAverageScore(nbrs);

                float score = avg * (float) nbrs.size() / Math.max(1, Math.max(nbrs.size(), empties.size()));
                assert (score <= 1.0 && score >= -1.0) : "score=" + score + " avg=" + avg;

                for (GoBoardPosition space : empties) {
                    space.setScoreContribution(score);
                    diffScore += score;
                }
            }
        } else if (pos.isInEye()) {
            pos.setScoreContribution(pos.getGroup().isOwnedByPlayer1() ? STONE_IN_EYE_CONTRIB : -STONE_IN_EYE_CONTRIB);
        }
        return diffScore;
    }

    /**
     * @param stones actually the positions containing the stones.
     * @return the average scores of the stones in the list.
     */
    private float calcAverageScore(GoBoardPositionSet stones) {
        float totalScore = 0;

        for (GoBoardPosition stone : stones) {
            IGoGroup group = stone.getString().getGroup();
            boolean useCached = false;
            totalScore += analyzerMap_.getAnalyzer(group).getRelativeHealth(board_, useCached);
        }
        return totalScore / stones.size();
    }
}