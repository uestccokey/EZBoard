/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.function;

import com.barrybecker4.common.math.Range;

/**
 * The function defines a line. It scales and offsets values.
 *
 * @author Barry Becker
 */
public class LinearFunction implements InvertibleFunction {

    private static final Range DOMAIN = new Range(Double.MIN_VALUE, Double.MAX_VALUE);

    private double scale;
    private double offset;


    /**
     * Constructor.
     */
    public LinearFunction(double scale) {
        this(scale, 0);
    }

    /**
     * Constructor.
     *
     * @param scale  amount to multiply/scale the value by
     * @param offset amount to add after scaling.
     */
    public LinearFunction(double scale, double offset) {
        this.scale = scale;
        this.offset = offset;
        if (scale == 0) {
            throw new IllegalArgumentException("scale cannot be 0.");
        }
    }

    /**
     * Constructor that creates a linear mapping from a range to a set of bin indices that go from 0 to numBins.
     *
     * @param range   The range of the domain. From min  to max value.
     * @param numBins number of bins to map to.
     */
    public LinearFunction(Range range, int numBins) {
        if (numBins == 0) {
            throw new IllegalArgumentException("numBins cannot be 0.");
        }
        if (range.getExtent() == 0) {
            throw new IllegalArgumentException("The range extent cannot be 0.");
        }
        this.scale = numBins / range.getExtent();
        this.offset = -range.getMin() * scale;
        if (scale == 0) {
            throw new IllegalArgumentException("scale cannot be 0.");
        }
    }

    @Override
    public double getValue(double value) {
        return scale * value + offset;
    }

    @Override
    public double getInverseValue(double value) {
        return (value - offset) / scale;
    }


    @Override
    public Range getDomain() {
        return DOMAIN;
    }
}