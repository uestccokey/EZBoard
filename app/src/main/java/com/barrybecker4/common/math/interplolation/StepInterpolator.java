/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;

/**
 * @author Barry Becker
 */
public class StepInterpolator extends AbstractInterpolator {

    public StepInterpolator(double[] function) {
        super(function);
    }

    @Override
    public double interpolate(double value) {
        if (value < 0 || value > 1.0)
            throw new IllegalArgumentException("value out of range [0, 1] :" + value);

        return function[(int) (value * function.length)];
    }
}