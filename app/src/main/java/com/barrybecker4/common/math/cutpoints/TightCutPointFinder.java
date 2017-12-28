// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.math.cutpoints;

import com.barrybecker4.common.math.Range;

import java.util.List;

/**
 * The min and max cut-points will be specific full precision numbers.
 *
 * @author Barry Becker
 */
public class TightCutPointFinder extends AbstractCutPointFinder {

    /** Labels should not get closer to each other than this. */
    private static final double MIN_LABEL_SEPARATION = 0.2;


    @Override
    protected void addPoints(List<Double> positions, Range roundedRange, Range finalRange, double d) {

        positions.add(checkSmallNumber(finalRange.getMin()));

        double initialInc = d;
        double pct = (roundedRange.getMin() + d - finalRange.getMin()) / d;
        if (MIN_LABEL_SEPARATION > pct) {
            initialInc = 2 * d;
        }
        double finalInc = 0.5 * d;
        pct = (finalRange.getMax() - (roundedRange.getMax() - d)) / d;
        if (MIN_LABEL_SEPARATION > pct) {
            finalInc = 1.5 * d;
        }
        double stop = roundedRange.getMax() - finalInc;
        for (double x = roundedRange.getMin() + initialInc; x < stop; x += d) {
            double val = checkSmallNumber(x);
            positions.add(val);
        }
        positions.add(checkSmallNumber(finalRange.getMax()));
    }
}