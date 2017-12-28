/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;

/**
 * @author Barry Becker
 */
public class CosineInterpolator extends AbstractSmoothInterpolator {

    public CosineInterpolator(double[] function) {
        super(function);
    }

    /**
     * Cosine interpolate between 2 values in the function that is defined as a double array.
     *
     * @param value x value in range [0,1] to determine y value for using specified function.
     * @return interpolated value.
     */
    @Override
    public double interpolate(double value) {

        if (value < 0 || value > 1.0)
            throw new IllegalArgumentException("value out of range [0, 1] :" + value);
        int len = function.length - 1;
        double x = value * (double) len;

        int index0 = (int) x;

        int index1 = index0 + 1;
        if (len == 0)
            index1 = len;
        double xdiff = x - index0;

        return smoothInterpolate(xdiff, function[index0], function[index1], 0, 0);
    }

    @Override
    protected double smoothInterpolate(double mu, double y0, double y1, double y2, double y3) {

        double mu2 = (1.0 - Math.cos(mu * Math.PI)) / 2.0;
        return (y0 * (1.0 - mu2) + y1 * mu2);
    }
}