/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.function;

import com.barrybecker4.common.math.Range;


/**
 * Piecewise linear function representation.
 *
 * @author Barry Becker
 */
public class PiecewiseFunction implements InvertibleFunction {

    /** These parallel arrays define the piecewise function map. */
    protected double[] xValues;
    protected double[] yValues;

    /**
     * Constructor.
     *
     * @param xValues x function values
     * @param yValues y function values
     */
    public PiecewiseFunction(double[] xValues, double[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
        assert this.xValues.length == this.yValues.length;
    }

    @Override
    public double getValue(double value) {

        return getInterpolatedValue(value);
    }

    /**
     * @param value y value to get x for
     * @return inverse function value.
     */
    @Override
    public double getInverseValue(double value) {

        return getInterpolatedValue(value);
    }

    @Override
    public Range getDomain() {
        return new Range(xValues[0], xValues[xValues.length - 1]);
    }


    /**
     * @param value x value to get interpolated y for.
     * @return the interpolated y value based on the key points in the arrays.
     */
    private double getInterpolatedValue(double value) {

        // first find the x value
        int i = 0;
        while (value > xValues[i]) {
            i++;
        }

        // return the linearly interpolated y value
        if (i == 0) {
            return yValues[0];
        }
        double xValm1 = xValues[i - 1];
        double denom = (xValues[i] - xValm1);

        if (denom == 0) {
            return yValues[i - 1];
        } else {
            double ratio = (value - xValm1) / denom;
            double yValm1 = yValues[i - 1];
            return yValm1 + ratio * (yValues[i] - yValm1);
        }
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder("PiecewiseFunction: "); //NON-NLS
        for (int i = 0; i < xValues.length; i++) {
            bldr.append("x=").append(xValues[i]).append(" y=").append(yValues[i]);
        }
        return bldr.toString();
    }
}
