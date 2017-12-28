// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.redistribution;

/**
 * Integer case of UniformRedistributionFunction.
 *
 * @author Barry Becker
 */
public class BooleanRedistribution extends DiscreteRedistribution {

    private static final int[] SPECIAL_VALUES = {0, 1};

    /**
     * Boolean redistribution.
     *
     * @param percentTrue percent chance that the boolean parameter will have the value true. must be in range 0 to 1.0.
     */
    public BooleanRedistribution(double percentTrue) {

        super(2, SPECIAL_VALUES, getSpecialValueProbs(percentTrue));
    }

    private static double[] getSpecialValueProbs(double percentTrue) {
        double[] probs = new double[2];
        probs[0] = percentTrue;
        probs[1] = 1.0 - percentTrue;
        return probs;
    }
}
