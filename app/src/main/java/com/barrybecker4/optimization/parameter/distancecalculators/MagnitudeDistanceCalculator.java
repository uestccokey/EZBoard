// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.distancecalculators;

import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * @author Barry Becker
 */
public class MagnitudeDistanceCalculator implements DistanceCalculator {

    /**
     * The distance computation will be quite different for this than a regular parameter array.
     * We want the distance to represent a measure of the amount of similarity between two instances.
     * There are two ways in which instance can differ, and the weighting assigned to each may depend on the problem.
     * - the length of the parameter array
     * - the set of values in the parameter array.
     * Generally, the distance is greater the greater the number of parameters that are different.
     *
     * @return the distance between this parameter array and another.
     */
    public double calculateDistance(ParameterArray pa1, ParameterArray pa2) {
        int thisLength = pa1.size();
        int thatLength = pa2.size();

        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < thisLength; i++) {
            sum1 += pa1.get(i).getValue();
        }
        for (int i = 0; i < thatLength; i++) {
            sum2 += pa2.get(i).getValue();
        }

        return Math.abs(sum1 - sum2);
    }
}
