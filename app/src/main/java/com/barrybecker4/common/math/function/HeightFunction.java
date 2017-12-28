/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.function;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.common.math.interplolation.Interpolator;
import com.barrybecker4.common.math.interplolation.LinearInterpolator;

/**
 * Represents a general y values function. It does not have to be monotonic or 1-1.
 *
 * @author Barry Becker
 */
public class HeightFunction implements Function {

    private Range domain;

    // a function that maps from the domain to indices within yValues.
    private LinearFunction domainToBinFunc;
    Interpolator interpolator;

    /**
     * Constructor.
     *
     * @param domain  the extent of the regularly spaced x axis values
     * @param yValues the y values.
     */
    public HeightFunction(Range domain, double[] yValues) {
        this.domain = domain;

        domainToBinFunc = new LinearFunction(1.0 / domain.getExtent(), -domain.getMin() / domain.getExtent());
        interpolator = new LinearInterpolator(yValues);
    }

    /**
     * Constructor. A domain of [0, 1.0] is assumed
     *
     * @param yValues the y values.
     */
    public HeightFunction(double[] yValues) {
        this(new Range(0, 1.0), yValues);
    }

    /** X axis domain */
    @Override
    public Range getDomain() {
        return domain;
    }

    /**
     * @param xValue x value to get y value for.
     * @return y value
     */
    @Override
    public double getValue(double xValue) {
        //return yValues[(int)(domainToBinFunc.getValue(xValue) + 0.5)];
        return interpolator.interpolate(domainToBinFunc.getValue(xValue));
    }
}

