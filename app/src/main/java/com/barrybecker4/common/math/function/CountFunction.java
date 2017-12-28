/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.function;

import com.barrybecker4.common.math.Range;

import java.util.LinkedList;
import java.util.List;

/**
 * Tracks a positive integer over time.
 * For example, it might be used to track a population count.
 *
 * @author Barry Becker
 */
public class CountFunction implements Function {

    /** When we get more than this many x values, scroll to the right instead of compressing the domain. */
    private static final int DEFAULT_MAX_X_VALUES = 500;

    /** These parallel arrays define the piecewise function map. */
    protected List<Double> xValues;
    protected List<Double> yValues;

    private int maxXValues = DEFAULT_MAX_X_VALUES;

    /**
     * Constructor.
     *
     * @param initialYValue y value for x=0
     */
    public CountFunction(double initialYValue) {
        setInitialValue(initialYValue);
    }

    public void setInitialValue(double initialYValue) {
        xValues = new LinkedList<>();
        yValues = new LinkedList<>();
        xValues.add(0.0);
        yValues.add(initialYValue);
    }

    public void addValue(double x, double y) {
        if (xValues.size() > maxXValues) {
            xValues.remove(0);
            yValues.remove(0);
        }
        xValues.add(x);
        yValues.add(y);

        // keep x values in range 0-1
        double len = xValues.size();
        for (int i = 0; i < len; i++) {
            xValues.set(i, (double) i / (len - 1));
        }
    }

    public void setMaxXValues(int max) {
        maxXValues = max;
    }

    /** X axis domain */
    @Override
    public Range getDomain() {
        return new Range(xValues.get(0), xValues.get(xValues.size() - 1));
    }

    /**
     * @param xValue x value to get y value for.
     * @return y value
     */
    @Override
    public double getValue(double xValue) {
        return getValue(xValue, xValues, yValues);
    }

    /**
     * get an interpolated y values for a specified x
     *
     * @param xValue x value
     * @return y value
     */
    private double getValue(double xValue, List<Double> xVals, List<Double> yVals) {
        // first find the x value
        int i = 0;
        while (i < xVals.size() && xValue > xVals.get(i)) {
            i++;
        }

        if (i == 0 || xVals.size() == 1) {
            return yVals.get(0);
        }
        double xValm1 = xVals.get(i - 1);
        double denom = (xVals.get(i) - xValm1);

        if (denom == 0) {
            return yVals.get(i - 1);
        } else {
            double ratio = (xValue - xValm1) / denom;
            double yValm1 = yVals.get(i - 1);
            return yValm1 + ratio * (yVals.get(i) - yValm1);
        }
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder("CountFunction: ");
        for (int i = 0; i < xValues.size(); i++) {
            bldr.append("\nx=")
                    .append(xValues.get(i))
                    .append(" y=").append(yValues.get(i));
        }
        return bldr.toString();
    }
}

