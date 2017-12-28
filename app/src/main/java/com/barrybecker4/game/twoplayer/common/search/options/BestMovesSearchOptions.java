/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.options;

/**
 * Options to limit the number of moves searched at each ply to just the ones most likely to be selected.
 * There is no guarantee that the subset returned will contain the actual best move, but it's a trade-off
 * for search time. The options are for search strategies that use brute-force minimax search
 * like MiniMax, NegaMax, NegaScout, and also the memory and aspiration variations of these strategies.
 * These methods usually use a search window to do pruning of tree branches.
 * The different options define a number of constraints on limiting the number of moves returned.
 * All the constraints must be satisfied in the sense that there will never be fewer moves returned than
 * any one of them dictates unless there are simple not that many moves available.
 * <p>
 * Some example use cases (see {@code }BestMoveFinder} unit tests)
 * All moves - Set minBestMoves to something large, or set bestPercentage to 100%,
 * or set percentLessThanBestThreshold to 100%.
 * Best 50% - Set minBestMoves to 1, set bestPercentage to 50%, set percentLessThanBestThreshold to 0%.
 * 80% lest than best or better - Set minBestMoves to 0, set bestPercentage to 0%, and
 * set percentLessThanBestThreshold to 80%.
 * Only really good ones - Set minBestMoves to 5, set bestPercentage to 10%, and
 * set percentLessThanBestThreshold to 10%.
 * Any fairly good move - Set minBestMoves to 10, set bestPercentage to 60%, and
 * set percentLessThanBestThreshold to 60%.
 *
 * @author Barry Becker
 */
public class BestMovesSearchOptions {

    /**
     * Of the total number of reasonable candidate moves, this will limit X percent of the most promising ones.
     * The larger this percent is, the more moves that will be included.
     * If 100%, then all candidates will be considered.
     */
    private static final int DEFAULT_BEST_PERCENTAGE = 50;

    /**
     * Select the best moves whose value is greater than this percent less than the highest value in the set.
     * For example if the best score is 4000 and this percent threshold is 50%, then only moves with a
     * score of 2000 or higher will be kept.
     * When computing the percentage the range between the maximum and minimum value must be considered since
     * can could go negative.
     */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 60;

    /**
     * No matter what the percentBestMoves is, we should not prune if less than this number remaining.
     * For example, suppose this constant is 10, and only 50% of the best moves are kept, and there are 12 considered,
     * then 10 will still be returned instead of 6. This rule has the highest precedence.
     */
    private static final int DEFAULT_MIN_BEST_MOVES = 10;

    /** percent of the best moves to consider */
    private int bestPercentage_ = DEFAULT_BEST_PERCENTAGE;

    /** consider moves that are percent less than best or greater */
    private int percentLessThanBestThreshold_ = DEFAULT_PERCENT_LESS_THAN_BEST_THRESH;

    /** highest priority criteria */
    private int minBestMoves_ = DEFAULT_MIN_BEST_MOVES;


    /**
     * Default Constructor with default values for all constraints.
     */
    public BestMovesSearchOptions() {}

    /**
     * Constructor
     *
     * @param bestPercentage            default number of best moves to consider at each ply.
     * @param percentLessThanBestThresh Select best moves whose values is no less than this percent less
     * @param minBestMoves              we will never consider fewer than this many moves when searching.
     */
    public BestMovesSearchOptions(int bestPercentage, int percentLessThanBestThresh, int minBestMoves) {
        bestPercentage_ = bestPercentage;
        percentLessThanBestThreshold_ = percentLessThanBestThresh;
        minBestMoves_ = minBestMoves;
    }

    /**
     * @return the percentage of top moves considered at each ply
     */
    public final int getPercentageBestMoves() {
        return bestPercentage_;
    }

    /**
     * @param bestPercentage the percentage of top moves considered at each ply
     */
    public final void setPercentageBestMoves(int bestPercentage) {
        bestPercentage_ = bestPercentage;
    }

    /**
     * @return never return fewer than this many best moves.
     */
    public int getMinBestMoves() {
        return minBestMoves_;
    }

    public void setMinBestMoves(int minBest) {
        if (minBest <= 0) {
            throw new IllegalArgumentException("minBest must be greater than 0. It was " + minBest);
        }
        minBestMoves_ = minBest;
    }

    /**
     * @return the "percent less than best" threshold percentage.
     */
    public int getPercentLessThanBestThresh() {
        return percentLessThanBestThreshold_;
    }

    public void setPercentLessThanBestThresh(int percent) {
        assert percent >= 0 && percent <= 100 : "percent out of range : " + percent;
        percentLessThanBestThreshold_ = percent;
    }

    public String toString() {
        return "bestPercentage: " + bestPercentage_
                + "  minBestMoves:" + minBestMoves_
                + "  percentLessThanBestThreshold:" + percentLessThanBestThreshold_;
    }

}