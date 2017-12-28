// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter;

import com.barrybecker4.optimization.parameter.types.Parameter;

import java.util.LinkedList;
import java.util.List;

/**
 * Finds the distance between two PermutedParameterArrays.
 *
 * @author Barry Becker
 */
public class PermutedDistanceCalculator {

    /**
     * The distance computation will be quite different for this than a regular parameter array.
     * We want the distance to represent a measure of the amount of similarity between two permutations.
     * If there are similar runs between two permutations, then the distance should be relatively small.
     * N^2 operation, where N is the number of params.
     *
     * @param pa1 first parameter array
     * @param pa2 second parameter array
     * @return the distance between this parameter array and another.
     */
    public double findDistance(PermutedParameterArray pa1, PermutedParameterArray pa2) {
        assert (pa1.size() == pa2.size());

        ParameterArray paReverse = pa2.reverse();
        return Math.min(difference(pa1, pa2), difference(pa1, paReverse));
    }

    /**
     * The amount of difference can be used as a measure of distance
     *
     * @return the amount of difference between pa and ourselves.
     */
    private double difference(ParameterArray pa1, ParameterArray pa2) {

        List<Integer> runLengths = new LinkedList<>();
        int len = pa1.size();
        int i = 0;

        while (i < len) {
            int runLength = determineRunLength(pa1, pa2, len, i, runLengths);
            i += runLength;
        }
        return calcDistance(pa1, runLengths);
    }

    /**
     * Adds the computed runlength to the runLengths list.
     *
     * @return the computed runlength
     */
    private int determineRunLength(ParameterArray pa1, ParameterArray pa2, int len, int i, List<Integer> runLengths) {
        int k;
        int ii = i;
        k = 1;
        int j = findCorrespondingEntryIndex(pa2, len, pa1.get(i));

        boolean matchFound = false;
        boolean matched;
        do {
            ii = ++ii % len;
            j = ++j % len;
            k++;
            matched = pa1.get(ii).equals(pa2.get(j));
            matchFound |= matched;
        } while (matched && k <= len);

        int runLength = k - 1;

        if (matchFound) {
            runLengths.add(runLength);
        }
        return runLength;
    }

    /**
     * @return the entry in pa that corresponds to param.
     * @throws AssertionError if not there. It must be there.
     */
    private int findCorrespondingEntryIndex(ParameterArray pa, int len, Parameter param) {
        int j = 0;
        while (j < len && !param.equals(pa.get(j))) {
            j++;
        }
        assert (j < len) : "Param " + param + " did not match any values in " + pa;
        return j;
    }

    /**
     * Find the distance between two permutations that each have runs of the specified lengths.
     *
     * @param runLengths list of run lengths.
     * @return the approximate distance between two permutations.
     */
    private double calcDistance(ParameterArray pa1, List<Integer> runLengths) {
        // careful this could overflow if the run is really long.
        // If it does we may need to switch to BigInteger.
        double max = Math.pow(2, pa1.size());

        if (runLengths.isEmpty()) return max;

        double denom = 0;
        for (int run : runLengths) {
            denom += Math.pow(2, run - 1);
        }
        return max / denom - 2.0;
    }
}
