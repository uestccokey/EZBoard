// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.math.cutpoints;

import com.barrybecker4.common.math.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for cut point finders.
 *
 * @author Barry Becker
 */
abstract class AbstractCutPointFinder {

    /** The range should not get smaller than this. */
    static final double MIN_RANGE = 1.0E-10;


    /**
     * Retrieve the cut point values.
     * If its a really small range include both min and max to avoid having just one label.
     *
     * @param range       range to be divided into intervals.
     * @param maxNumTicks upper limit on number of cut points to return.
     * @return the cut points
     */
    public double[] getCutPoints(Range range, int maxNumTicks) {

        validateArguments(range);
        Range finalRange = new Range(range);
        if (range.getExtent() <= MIN_RANGE) {
            finalRange.add(range.getMin() + MIN_RANGE);
        }

        List<Double> positions = new ArrayList<>(10);

        if (finalRange.getExtent() < MIN_RANGE) {
            positions.add(finalRange.getMin());
        } else {
            determineCutPoints(maxNumTicks, finalRange, positions);
        }

        double[] result = new double[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            result[i] = positions.get(i);
        }

        return result;
    }

    private void determineCutPoints(int maxTicks, Range finalRange, List<Double> positions) {

        double extent = Rounder.roundUp(finalRange.getExtent());
        double d = Rounder.roundDown(extent / (maxTicks - 1));
        Range roundedRange =
                new Range(Math.floor(finalRange.getMin() / d) * d, Math.ceil(finalRange.getMax() / d) * d);

        addPoints(positions, roundedRange, finalRange, d);
    }

    protected abstract void addPoints(List<Double> positions, Range roundedRange, Range finalRange, double d);

    /**
     * Verify that the min and max are valid.
     *
     * @param range range to check for NaN values.
     */
    private void validateArguments(Range range) {
        if (Double.isNaN(range.getExtent())) {
            throw new IllegalArgumentException("Min cannot be greater than max for " + range);
        }
        if (Double.isNaN(range.getMin())
                || Double.isInfinite(range.getMin())
                || Double.isNaN(range.getMax())
                || Double.isInfinite(range.getMax())) {
            throw new IllegalArgumentException("Min or max of the range [" + range + "] is not a number.");
        }
    }

    /**
     * If real small just assume it is zero.
     *
     * @param value the value to check.
     * @return zero if value is below the smallest allowed range, else return value.
     */
    double checkSmallNumber(double value) {
        if (Math.abs(value) < MIN_RANGE) {
            return 0;
        }
        return value;
    }
}