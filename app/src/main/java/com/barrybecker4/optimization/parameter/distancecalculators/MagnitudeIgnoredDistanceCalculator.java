// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.distancecalculators;

import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Barry Becker
 */
public class MagnitudeIgnoredDistanceCalculator implements DistanceCalculator {

    /**
     * The distance computation will be quite different for this than a regular parameter array.
     * We want the distance to represent a measure of the amount of similarity between two instances.
     * There are two ways in which instance can differ, and the weighting assigned to each may depend on the problem.
     * - the length of the parameter array
     * - the set of values in the parameter array.
     * Generally, the distance is greater the greater the number of parameters that are different.
     *
     * @return the distance between this parameter array and another.
     */
    public double calculateDistance(ParameterArray pa1, ParameterArray pa2) {

        int thisLength = pa1.size();
        int thatLength = pa2.size();

        List<Integer> theseValues = new ArrayList<>(thisLength);
        List<Integer> thoseValues = new ArrayList<>(thatLength);

        for (int i = 0; i < thisLength; i++) {
            theseValues.add((int) pa1.get(i).getValue());
        }
        for (int i = 0; i < thatLength; i++) {
            thoseValues.add((int) pa2.get(i).getValue());
        }

        Collections.sort(theseValues);
        Collections.sort(thoseValues);

        int valueDifferences = calcValueDifferences(theseValues, thoseValues);

        return Math.abs(thisLength - thatLength) + valueDifferences;
    }

    /**
     * Perform a sort of merge sort on the two sorted lists of values to find matches.
     * The more matches there are between the two lists, the more similar they are.
     * The magnitude of the differences between values does not matter, only whether
     * they are the same or different.
     *
     * @param theseValues first ordered list
     * @param thoseValues second ordered list
     * @return measure of the difference between the two sorted lists.
     * It will return 0 if the two lists are the same.
     */
    private int calcValueDifferences(List<Integer> theseValues, List<Integer> thoseValues) {

        int thisLen = theseValues.size();
        int thatLen = thoseValues.size();
        int thisCounter = 0;
        int thatCounter = 0;
        int matchCount = 0;

        while (thisCounter < thisLen && thatCounter < thatLen) {
            double thisVal = theseValues.get(thisCounter);
            double thatVal = thoseValues.get(thatCounter);
            if (thisVal < thatVal) {
                thisCounter++;
            } else if (thisVal > thatVal) {
                thatCounter++;
            } else {  // they are the same
                thisCounter++;
                thatCounter++;
                matchCount++;
            }
        }
        return Math.max(thisLen, thatLen) - matchCount;
    }
}
