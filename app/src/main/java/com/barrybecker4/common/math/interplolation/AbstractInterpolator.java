/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;

/**
 * Use to interpolate between values on a function defined only at discrete points.
 *
 * @author Barry Becker
 */
abstract class AbstractInterpolator implements Interpolator {

    double[] function;

    AbstractInterpolator(double[] function) {
        this.function = function;
    }

}