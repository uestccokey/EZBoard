/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;

import java.util.Collections;

/**
 * Find the best moves from a list of reasonable next moves using configured search options.
 * The search options provide a set constraints which all must be minimally satisfied.
 *
 * @author Barry Becker
 */
public class BestMoveFinder<M extends TwoPlayerMove> {

    private BestMovesSearchOptions searchOptions_;

    private boolean isPlayer1;

    /**
     * Constructor.
     *
     * @param searchOptions best move search options.
     */
    public BestMoveFinder(BestMovesSearchOptions searchOptions) {
        searchOptions_ = searchOptions;
    }

    /**
     * Take the list of all possible next moves and return just the best of them, based
     * on the constraints specified my the search options.
     * <p>
     * Sort the list so that the better moves appear first.
     * This is a terrific improvement when used in conjunction with alpha-beta pruning.
     *
     * @param moveList the list of all generated moves
     * @return the best moves in order of how good they are.
     */
    public MoveList<M> getBestMoves(MoveList<M> moveList) {

        Collections.sort(moveList);

        // reverse the order so the best (highest scoring) move (using static board evaluation) is first.
        isPlayer1 = moveList.isEmpty() || (moveList.getFirstMove()).isPlayer1();
        if (isPlayer1) {
            Collections.reverse(moveList);
        }

        return determineBestMoves(moveList);
    }

    /**
     * Select just the best moves after sorting the reasonable next moves.
     * We could potentially eliminate the best move doing this, but we need to trade that off against search time.
     * A move which has a low score this time might actually lead to the best move later.
     *
     * @param moveList a sorted list of reasonable next moves.
     * @return set of best moves from the original list
     */
    private MoveList<M> determineBestMoves(MoveList<M> moveList) {

        int minBest = searchOptions_.getMinBestMoves();
        int requestedBest = getNumRequestedBest(moveList);
        double thresholdValue = getThresholdValue(moveList);
        int minToGet = Math.max(minBest, requestedBest);

        return getBestMoves(moveList, minToGet, thresholdValue);
    }

    /**
     * @return number of moves corresponding to percent of best.
     */
    private int getNumRequestedBest(MoveList<M> moveList) {
        int numMoves = moveList.size();
        int topPercent = searchOptions_.getPercentageBestMoves();
        return (int) ((float) topPercent / 100.0 * numMoves + 0.5);
    }

    /**
     * Must consider the full range of values, not just the biggest, because values can be negative.
     * For example, if the score values are 90, 80, 70, 10, 0, -20, -110, and the percentLessThanPestThresh is 20%,
     * then the threshold will be 90 - 0.2 (200) = 50. So only 90, 80, 70 would be included.
     *
     * @return score that needs to be exceeded to be considered an acceptable move.
     */
    private double getThresholdValue(MoveList<M> moveList) {
        double thresholdValue = 0;
        if (!moveList.isEmpty()) {
            double highValue = moveList.getFirstMove().getValue();
            double lowValue = moveList.getLastMove().getValue();
            double range = Math.abs(highValue - lowValue);
            double percent = searchOptions_.getPercentLessThanBestThresh() / 100.0;
            double diff = range * percent;
            thresholdValue = highValue + (isPlayer1 ? -diff : diff);
        }
        return thresholdValue;
    }

    /**
     * @param moveList       list of moves to select from
     * @param minToGet       return no fewer than this unless there are not that many in moveList to begin with.
     * @param thresholdValue percent of best score to exceed to get included
     * @return best moves that satisfy requirements
     */
    private MoveList<M> getBestMoves(MoveList<M> moveList, int minToGet, double thresholdValue) {

        MoveList<M> bestMoves = new MoveList<>();

        if (!moveList.isEmpty()) {
            for (M move : moveList) {
                boolean threshExceeded = isPlayer1 ?
                        move.getValue() >= thresholdValue :
                        move.getValue() <= thresholdValue;
                if (bestMoves.size() < minToGet || threshExceeded) {
                    bestMoves.add(move);
                }
            }
        }
        return bestMoves;
    }
}