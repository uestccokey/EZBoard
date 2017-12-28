/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;

/**
 * @author Barry Becker
 */
public class CubicInterpolator extends AbstractSmoothInterpolator {

    public CubicInterpolator(double[] function) {
        super(function);
    }

    @Override
    protected double smoothInterpolate(double y0, double y1, double y2, double y3, double mu) {
        double mu2 = mu * mu;
        double a0 = y3 - y2 - y0 + y1;
        double a1 = y0 - y1 - a0;
        double a2 = y2 - y0;

        return (a0 * mu * mu2 + a1 * mu2 + a2 * mu + y1);
    }
}