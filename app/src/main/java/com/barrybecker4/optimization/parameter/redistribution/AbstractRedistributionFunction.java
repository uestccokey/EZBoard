// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.redistribution;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.common.math.function.InvertibleFunction;

/**
 * @author Barry Becker
 */
public abstract class AbstractRedistributionFunction implements RedistributionFunction {

    /** the discretized redistribution function */
    protected InvertibleFunction redistributionFunction;

    /**
     * @param value x value
     * @return the value for the function at position value.
     */
    public double getValue(double value) {
        verifyInRange(value);

        double newValue = redistributionFunction.getValue(value);

        verifyInRange(newValue);
        return newValue;
    }

    /**
     * @param value
     * @return the inverse of the specified value.
     */
    public double getInverseFunctionValue(double value) {
        verifyInRange(value);

        double newValue = redistributionFunction.getInverseValue(value);

        verifyInRange(newValue);
        return newValue;
    }

    public Range getDomain() {
        return new Range(0, 1.0);
    }

    protected abstract void initializeFunction();

    protected static void verifyInRange(double value) {
        assert (value >= 0) && (value <= 1.0) : "value, " + value + ", was outside the range 0 to 1.";
    }
}
