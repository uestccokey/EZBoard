/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;

/**
 * @author Barry Becker
 */
public class HermiteInterpolator extends AbstractSmoothInterpolator {

    private double tension;
    private double bias;

    public HermiteInterpolator(double[] function) {
        this(function, 0, 0);
    }

    public HermiteInterpolator(double[] function, double tension, double bias) {
        super(function);
        this.tension = tension;
        this.bias = bias;
    }

    @Override
    protected double smoothInterpolate(double y0, double y1, double y2, double y3, double mu) {

        double m0, m1;
        double a0, a1, a2, a3;

        double mu2 = mu * mu;
        double mu3 = mu2 * mu;
        m0 = (y1 - y0) * (1 + bias) * (1 - tension) / 2;
        m0 += (y2 - y1) * (1 - bias) * (1 - tension) / 2;
        m1 = (y2 - y1) * (1 + bias) * (1 - tension) / 2;
        m1 += (y3 - y2) * (1 - bias) * (1 - tension) / 2;
        a0 = 2 * mu3 - 3 * mu2 + 1;
        a1 = mu3 - 2 * mu2 + mu;
        a2 = mu3 - mu2;
        a3 = -2 * mu3 + 3 * mu2;

        return (a0 * y1 + a1 * m0 + a2 * m1 + a3 * y2);
    }
}