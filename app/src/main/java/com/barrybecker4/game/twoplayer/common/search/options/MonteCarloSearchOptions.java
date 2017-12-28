/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.options;

/**
 * The options for search strategies that use brute-force minimax search like MiniMax, NegaMax, NegaScout,
 * and also the memory and aspiration variations of these strategies.
 * These methods usually use a search window to do pruning of tree branches.
 *
 * @author Barry Becker
 */
public class MonteCarloSearchOptions {

    public enum MaximizationStyle {WIN_RATE, NUM_VISITS}

    /** Number of moves to look ahead while searching for the best move. */
    private static final int DEFAULT_MAX_SIMULATIONS = 2000;

    /** Ratio of exploration to exploitation of good moves. */
    private static final double DEFAULT_EXPLORE_EXPLOIT_RATIO = 1.0;

    /** Default number of random moves to look-ahead when playing a random game. */
    private static final int DEFAULT_RANDOM_LOOKAHEAD = 20;

    private int maxSimulations_ = DEFAULT_MAX_SIMULATIONS;
    private double exploreExploitRatio_ = DEFAULT_EXPLORE_EXPLOIT_RATIO;
    private int randomLookAhead_ = DEFAULT_RANDOM_LOOKAHEAD;
    private MaximizationStyle maxStyle_ = MaximizationStyle.WIN_RATE;

    /**
     * Default Constructor
     */
    public MonteCarloSearchOptions() {}

    /**
     * Constructor
     *
     * @param maxSimulations      default number simulations to run.
     * @param exploreExploitRatio ratio of exploring nodes to exploiting them.
     * @param randomLookAhead     amount to look ahead during random games.
     * @param maxStyle            method by which to select the best node.
     */
    public MonteCarloSearchOptions(int maxSimulations, double exploreExploitRatio, int randomLookAhead,
                                   MaximizationStyle maxStyle) {
        maxSimulations_ = maxSimulations;
        exploreExploitRatio_ = exploreExploitRatio;
        randomLookAhead_ = randomLookAhead;
        maxStyle_ = maxStyle;
    }

    /**
     * Constructor
     *
     * @param maxSimulations      default number simulations to run.
     * @param exploreExploitRatio ratio of explor to explout
     * @param randomLookAhead     amount to look ahead during random games.
     */
    public MonteCarloSearchOptions(int maxSimulations, double exploreExploitRatio, int randomLookAhead) {
        this(maxSimulations, exploreExploitRatio, randomLookAhead, MaximizationStyle.WIN_RATE);
    }

    /**
     * @return the max number of simulations to make while searching.
     */
    public int getMaxSimulations() {
        return maxSimulations_;
    }

    /**
     * @param maxSim the new max number of simulations.
     */
    public void setMaxSimulations(int maxSim) {
        maxSimulations_ = maxSim;
    }

    /**
     * The larger this is (bigger than 1) the closer to uniform search we get (i.e exploration).
     * The smaller it is (less than 1) the more selective the search becomes (i.e. we exploit the known good moves).
     * There needs to be a balance.
     *
     * @return the ratio of exploration to exploitation.
     */
    public double getExploreExploitRatio() {
        return exploreExploitRatio_;
    }

    /**
     * @param ratio the ratio of exploration to exploitation.
     */
    public void setExploreExploitRatio(double ratio) {
        exploreExploitRatio_ = ratio;
    }

    /**
     * @return number of moves to look ahead randomly when playing a random game.
     */
    public int getRandomLookAhead() {
        return randomLookAhead_;
    }

    /**
     * @param randomLookAhead amount of moves to randomly look ahead
     */
    public void setRandomLookAhead(int randomLookAhead) {
        randomLookAhead_ = randomLookAhead;
    }

    public MaximizationStyle getMaxStyle() {
        return maxStyle_;
    }

    public void setMaxStyle(MaximizationStyle style) {
        maxStyle_ = style;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("maxSimulations:").append(maxSimulations_);
        bldr.append("  exploreExploitRatio:").append(exploreExploitRatio_);
        bldr.append("  randomLookAhead:").append(randomLookAhead_);
        return bldr.toString();
    }
}