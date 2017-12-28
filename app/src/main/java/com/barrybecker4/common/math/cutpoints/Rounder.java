package com.barrybecker4.common.math.cutpoints;

import com.barrybecker4.common.math.MathUtil;

/**
 * Rounds numbers in a "nice" way so that they are easy to read.
 * From an article by Paul Heckbert in Graphics Gems 1.
 *
 * @author Barry Becker
 */
public final class Rounder {

    /**
     * Find a "nice" number approximately equal to the numberToRound (rounding up).
     * Corresponds to the "nicenum" method in graphics gems (page 659).
     *
     * @param numberToRound the value to round
     * @return nice rounded number. Something that has a final significant digit of 1, 2, or 5 x10^j
     */
    public static double roundUp(double numberToRound) {
        return round(numberToRound, false);
    }

    /**
     * Find a "nice" number approximately equal to the numberToRound (rounding up).
     * Corresponds to the "nicenum" method in graphics gems (page 659).
     *
     * @param numberToRound the value to round
     * @return nice rounded number. Something that has a final significant digit of 1, 2, or 5 x10^j
     */
    public static double roundDown(double numberToRound) {
        return round(numberToRound, true);
    }

    /**
     * Find a "nice" number approximately equal to the numberToRound.
     * Round the number down if round is true, round up if round is false.
     * Corresponds to the "nicenum" method in graphics gems (page 659).
     *
     * @param numberToRound the value to round
     * @param roundDown     if true, then round the number down. If false, take the ceil of it.
     * @return nice rounded number. Something that has a final significant digit of 1, 2, or 5 x10^j
     */
    private static double round(double numberToRound, boolean roundDown) {
        int exp = (int) Math.floor(MathUtil.log10(numberToRound));

        double normalizedNumber = numberToRound / MathUtil.exp10(exp);
        double niceNum = roundDown ? roundNumberDown(normalizedNumber) : roundNumberUp(normalizedNumber);

        return niceNum * MathUtil.exp10(exp);
    }

    private static double roundNumberUp(double num) {
        double niceNum;
        if (num < 1.0) {
            niceNum = 1.0;
        } else if (num <= 2.0) {
            niceNum = 2.0;
        } else if (num < 5.0) {
            niceNum = 5.0;
        } else {
            niceNum = 10.0;
        }
        return niceNum;
    }

    private static double roundNumberDown(double num) {
        double niceNum;
        if (num < 1.5) {
            niceNum = 1.0;
        } else if (num < 3.0) {
            niceNum = 2.0;
        } else if (num < 7.0) {
            niceNum = 5.0;
        } else {
            niceNum = 10.0;
        }
        return niceNum;
    }

    /** private constructor since all methods are static. */
    private Rounder() {}
}