// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.redistribution;

/**
 * Integer case of UniformRedistributionFunction.
 *
 * @author Barry Becker
 */
public class DiscreteRedistribution extends UniformRedistribution {

    /**
     * If you have just a purely uniform distribution you do not need to add any redistribution function as that is the default.
     * Use this function though, if you have uniform except for a few special values.
     * If the sum of all special value probabilities is equal to one, then no non-special values are ever selected.
     *
     * @param numValues                         number of values
     * @param discreteSpecialValues             certain values that are more likely to occur than other regular values. (must be in increasing order)
     * @param discreteSpecialValueProbabilities sum of all special value probabilities must be less than or equal to one.
     */
    public DiscreteRedistribution(int numValues, int[] discreteSpecialValues, double[]
            discreteSpecialValueProbabilities) {
        int len = discreteSpecialValues.length;
        specialValues = new double[len];
        specialValueProbabilities = new double[len];

        for (int i = 0; i < len; i++) {
            assert discreteSpecialValues[i] < numValues :
                    " A discrete special value (" + discreteSpecialValues[i] + ") was >= " + numValues;
            specialValues[i] = ((double) (discreteSpecialValues[i])) / (double) (numValues - 1);
            specialValueProbabilities[i] = discreteSpecialValueProbabilities[i];
        }

        initializeFunction();
    }
}
