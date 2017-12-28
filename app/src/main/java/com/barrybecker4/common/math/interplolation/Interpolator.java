/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;

/**
 * Defines a way to interpolate between 2 points in function that is defined by an array of y values
 * .
 *
 * @author Barry Becker
 */
public interface Interpolator {

    /**
     * Given an x value, returns f(x)   (i.e. y)
     *
     * @param value value to find interpolated function value for.
     * @return the interpolated value.
     */
    double interpolate(double value);
}