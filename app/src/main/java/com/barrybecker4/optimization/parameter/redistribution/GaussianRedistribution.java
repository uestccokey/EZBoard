// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.redistribution;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.Range;
import com.barrybecker4.common.math.function.ArrayFunction;
import com.barrybecker4.common.math.function.ErrorFunction;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.common.math.function.FunctionInverter;

/**
 * Convert the uniform distribution to a normal (gaussian) one.
 *
 * @author Barry Becker
 */
public class GaussianRedistribution extends AbstractRedistributionFunction {

    private double mean;
    private double stdDeviation;

    private static final int NUM_MAP_VALUES = 10;
    private static final double SQRT2 = Math.sqrt(2.0);

    private Function errorFunction;


    public GaussianRedistribution(double mean, double standardDeviation) {
        verifyInRange(mean);
        errorFunction = new ErrorFunction();
        this.mean = mean;
        this.stdDeviation = standardDeviation;
        initializeFunction();
    }

    @Override
    protected void initializeFunction() {
        double[] functionMap;
        double inc = 1.0 / (NUM_MAP_VALUES - 1);
        double[] cdfFunction = new double[NUM_MAP_VALUES];
        cdfFunction[0] = 0;

        double x = 0;
        for (int index = 1; index < NUM_MAP_VALUES; index++) {
            x += inc;
            double v = cdf(x);
            cdfFunction[index] = v;
        }
        double lowMissing = cdf(0);
        double highMissing = 1.0 - cdfFunction[NUM_MAP_VALUES - 1];

        System.out.println("lowMissing=" + lowMissing + " highMissing=" + highMissing);

        // reallocate the part that is missing.
        int numMapValsm1 = NUM_MAP_VALUES - 1;
        for (int i = 1; i < NUM_MAP_VALUES; i++) {
            double aliasAllocation =
                    -lowMissing * (double) (numMapValsm1 - i) / numMapValsm1 + highMissing * (double) i / numMapValsm1;
            cdfFunction[i] += aliasAllocation;
            if (cdfFunction[i] > 1.0 && i < NUM_MAP_VALUES - 1) {
                cdfFunction[i] = 1.0 - MathUtil.EPS;
            }
        }
        double max = cdfFunction[NUM_MAP_VALUES - 1];
        assert (max > 0.9 && max < 1.01);

        cdfFunction[NUM_MAP_VALUES - 1] = 1.0;
        Range xRange = new Range(0.0, 1.0);
        FunctionInverter inverter = new FunctionInverter(cdfFunction);
        functionMap = inverter.createInverseFunction(xRange);
        redistributionFunction = new ArrayFunction(functionMap, cdfFunction);
    }

    /**
     * 1/2 (1 + erf((x-mean)/(SQRT2 *stdDeviation))
     *
     * @param x x coordinate position.
     * @return cdf cumulative distribution function value
     */
    private double cdf(double x) {

        double denom = SQRT2 * stdDeviation;
        double xx = (Math.min(1.0, x) - mean) / denom;
        double erf = errorFunction.getValue(xx);
        return 0.5 * (1.0 + erf);
    }

    public static void main(String[] args) {
        new GaussianRedistribution(0.5, 10.0);
    }

}
