// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.math.function;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.Range;

import java.util.Arrays;

/**
 * Use to find the inverse of a given function
 *
 * @author Barry Becker
 */
public class FunctionInverter {

    private static final double EPS_BIG = .1;
    private double[] func;
    private int length;
    private int lengthm1;

    /**
     * @param function the range values of the function to invert assuming domain is [0,1]
     */
    public FunctionInverter(double[] function) {
        func = function;
        length = func.length;
        lengthm1 = length - 1;
        assert (func[lengthm1] == 1.0) : func[lengthm1] + " was not = 1.0";
    }

    /**
     * Creates an inverse of the function specified with the same precision as the passed in function.
     * Assumes that function func is monotonic and maps [xRange] into [yRange].
     * This can be quite inaccurate if there are not that many sample points.
     *
     * @param xRange the extent of the domain
     * @return inverse error function for specified range
     */
    public double[] createInverseFunction(Range xRange) {
        double[] invFunc = new double[length];
        int j = 0;
        double xMax = xRange.getMax();

        for (int i = 0; i < length; i++) {

            double xval = (double) i / lengthm1;
            while (j < lengthm1 && func[j] <= xval) {
                j++;
                if (func[j - 1] > func[j] + MathUtil.EPS) {
                    throw new IllegalStateException(func[j - 1] + " was not less than " + func[j]
                            + ". That means the function was not monotonic as we assumed for func=" + Arrays.toString(func)
                            + " at position=" + j);
                }
            }

            invFunc[i] = xRange.getMin();
            if (j > 0) {
                double fm1 = func[j - 1];
                assert (xval >= fm1);

                double denom = func[j] - fm1;
                double nume = xval - fm1;
                assert denom >= 0;
                if (denom == 0) {
                    assert nume == 0;
                    denom = 1.0;
                }
                double y = ((double) (j - 1) + nume / denom) / (double) lengthm1;
                invFunc[i] = xRange.getMin() + y * xRange.getExtent();
                assert (invFunc[i] < xMax + EPS_BIG) : invFunc[i] + " was not less than " + xMax;
            }
        }
        assert (invFunc[lengthm1] > xMax - EPS_BIG) : invFunc[lengthm1] + " was not greater than " + xMax;
        invFunc[lengthm1] = xMax;

        return invFunc;
    }
}
