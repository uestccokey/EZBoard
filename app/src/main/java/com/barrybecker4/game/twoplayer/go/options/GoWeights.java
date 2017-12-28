/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.options;

import com.barrybecker4.game.common.GameWeights;

/**
 * These weights determine how the computer values each term of the polynomial evaluation function.
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * @author Barry Becker Date: Feb 11, 2007
 */
public class GoWeights extends GameWeights {

    /** use these if no others are provided. */
    private static final double[] DEFAULT_WEIGHTS = {2.0, 0.9, 0.3, 10.0};

    /** don't allow the weights to exceed these maximum values. */
    private static final double[] MAX_WEIGHTS = {10.0, 5.0, 10.0, 30.0};

    /** don't allow the weights to go below these minimum values. */
    private static final double[] MIN_WEIGHTS = {0.0, 0.0, 0.0, 0.5};

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
            "Health",
            "Position",
            "Bad shape",
            "CaptureCounts"};

    private static final String[] WEIGHT_DESCRIPTIONS = {
            "Weight to associate with the relative health of groups",
            "Weight to associate with Position",
            "Weight to associate with the Bad Shape Penalty",
            "Weight to give to CaptureCounts"
            //"Min Difference between health of two groups for one to be considered dead relative to the other"
    };

    public static final int HEALTH_WEIGHT_INDEX = 0;
    public static final int POSITIONAL_WEIGHT_INDEX = 1;
    public static final int BAD_SHAPE_WEIGHT_INDEX = 2;
    public static final int CAPTURE_WEIGHT_INDEX = 3;

    public GoWeights() {
        super(DEFAULT_WEIGHTS, MIN_WEIGHTS, MAX_WEIGHTS,
                WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS);
    }
}
