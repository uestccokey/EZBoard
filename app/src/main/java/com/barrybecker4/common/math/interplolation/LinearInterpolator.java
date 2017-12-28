/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.interplolation;


/**
 * @author Barry Becker
 */
public class LinearInterpolator extends AbstractInterpolator {


    public LinearInterpolator(double[] function) {
        super(function);
    }

    @Override
    public double interpolate(double value) {

        if (value < 0 || value > 1.0) {
            throw new IllegalArgumentException("value out of range [0, 1] :" + value);
        }

        int len = function.length - 1;
        double x = value * (double) len;

        int index0 = (int) x;

        if (x > len) {
            throw new IllegalArgumentException(index0 + " is >= " + len + ". x=" + x);
        }
        int index1 = index0 < len ? index0 + 1 : len;
        if (len == 0)
            index1 = len;
        double xdiff = x - index0;

        if (index0 < 0) {
            throw new IllegalArgumentException("index0 must be greater than 0, but was " + index0);
        }
        if (index1 > len) {
            throw new IllegalArgumentException(
                    "index1 must be less than the size of the array (" + function.length + "), but was " + index1);
        }

        //System.out.println("lin: xdiff="+ xdiff
        // + " f["+index0+"]="+ function[ index0 ]  +" f["+index1+"]="+function[ index1 ] );
        return (1.0 - xdiff) * function[index0] + xdiff * function[index1];
    }

}