// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.math.cutpoints;

import com.barrybecker4.common.math.Range;

import java.util.List;

/**
 * The min and max cut-points will be nice round numbers.
 *
 * @author Barry Becker
 */
public class LooseCutPointFinder extends AbstractCutPointFinder {

    @Override
    protected void addPoints(List<Double> positions, Range roundedRange, Range finalRange, double d) {

        double stop = roundedRange.getMax() + 0.5 * d;
        for (double x = roundedRange.getMin(); x < stop; x += d) {
            positions.add(checkSmallNumber(x));
        }
    }

}