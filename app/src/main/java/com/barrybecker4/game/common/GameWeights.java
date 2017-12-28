/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common;

import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * The GameWeights define the coefficients to use by the
 * evaluation polynomial used by each computer player.
 * Optimizing these weights is the primary way that the game
 * learns to play better.
 *
 * @author Barry Becker
 */
public class GameWeights {

    /** scores computed from weights are assumed to be between [0 and ASSUMED_WINNING_VALUE] for player1 */
    protected static final double ASSUMED_WINNING_VALUE = 1024;

    /**
     * the weights are created assuming a winning value of ASSUMED_WINNING_VALUE.
     * If that changes we need to scale them
     */
    private static final double SCALE = SearchStrategy.WINNING_VALUE / ASSUMED_WINNING_VALUE;

    private int numWeights_;

    private final ParameterArray defaultWeights_;
    private ParameterArray p1Weights_;
    private ParameterArray p2Weights_;

    private final String[] names_;
    private final String[] descriptions_;

    /**
     * Constructor
     *
     * @param defaultWeights default weights to use (will also be used for p1 and p2 weights).
     */
    public GameWeights(ParameterArray defaultWeights) {
        // this will not change once set.
        numWeights_ = defaultWeights.size();

        defaultWeights_ = defaultWeights;
        names_ = new String[numWeights_];
        descriptions_ = new String[numWeights_];

        for (int i = 0; i < numWeights_; i++) {
            names_[i] = "Weight " + i;
            descriptions_[i] = "The weighting coefficient for the " + i + "th term of the evaluation polynomial";
        }
        init();
    }

    /**
     * Constructor
     */
    public GameWeights(double[] defaultWeights, double[] minWeights, double[] maxWeights,
                       String[] names, String[] descriptions) {
        numWeights_ = defaultWeights.length;
        double[] minVals = new double[numWeights_];
        double[] defaultVals = new double[numWeights_];
        double[] maxVals = new double[numWeights_];

        for (int i = 0; i < numWeights_; i++) {
            minVals[i] = SCALE * minWeights[i];
            defaultVals[i] = SCALE * defaultWeights[i];
            maxVals[i] = SCALE * maxWeights[i];
        }
        defaultWeights_ = new NumericParameterArray(defaultVals, minVals, maxVals, names);

        names_ = names;
        descriptions_ = descriptions;

        init();
    }

    private void init() {
        p1Weights_ = defaultWeights_.copy();
        p2Weights_ = defaultWeights_.copy();
    }

    /**
     * @return the weights for player1. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getPlayer1Weights() {
        return p1Weights_;
    }

    /**
     * @return the weights for player1. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getPlayer2Weights() {
        return p2Weights_;
    }

    public final void setPlayer1Weights(ParameterArray p1Weights) {
        verify(p1Weights);
        p1Weights_ = p1Weights;
    }

    public final void setPlayer2Weights(ParameterArray p2Weights) {
        verify(p2Weights);
        p1Weights_ = p2Weights;
    }

    private void verify(ParameterArray wts) {
        assert wts.size() == numWeights_ :
                "Incorrect number of weights: " + wts.size() + " you need " + numWeights_;
    }

    /**
     * @return the default weights. It a reference, so changing them will change the weights in this structure
     */
    public final ParameterArray getDefaultWeights() {
        return defaultWeights_;
    }

    /**
     * @return short description of weight i
     */
    public final String getName(int i) {
        return names_[i];
    }

    /**
     * @return description of weight i (good for putting in a tooltip)
     */
    public final String getDescription(int i) {
        return descriptions_[i];
    }

    /**
     * @return the maximum allowed value of weight i
     */
    public final double getMaxWeight(int i) {
        return defaultWeights_.get(i).getMaxValue();
    }

    /**
     * nicely print the weights
     */
    @Override
    public final String toString() {
        return "Player1's weights are:" + p1Weights_ + "\nPlayer2's weights are " + p2Weights_;
    }
}

