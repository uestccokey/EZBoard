/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.combinatorics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Provides a way of iterating through all the combinations of a set of N integers.
 * For example if N = 3, then the set of N integers is {0, 1, 2}
 * and the list of all subsets that will be iterated through will be
 * {0}, {1}, {0, 1}, {2}, {2, 0}, {2, 1}, {2, 1, 0}
 * Note that the order of the elements in the subsets does not matter.
 * The implementation does not require memory for storing the permutations since
 * only one is produced at a time.
 * This algorithm will only work as long as the number of subsets is less than Long.MAX_VALUE.
 * In other words N must be less than 60 since 2^60 is close to Long.MAX_VALUE.
 *
 * @author Barry Becker
 */
public class Combinater implements Iterator<List<Integer>> {

    private int num;
    private long numCombinations;

    /** acts as a bit set to find all combinations. */
    private long counter;

    private boolean hasMore;

    /**
     * Constructor
     *
     * @param num the number of integer elements to permute.
     */
    public Combinater(int num) {

        this.num = num;
        numCombinations = (long) Math.pow(2, num) - 1;

        if (numCombinations >= Long.MAX_VALUE - 1) {
            throw new IllegalArgumentException("The number of combinations is greater than " + Long.MAX_VALUE);
        }
        hasMore = num > 0;

        counter = 0L;
    }

    @Override
    public boolean hasNext() {
        return hasMore;
    }

    /**
     * @return the next permutation
     */
    @Override
    public List<Integer> next() {

        counter++;
        if (!hasMore) {
            throw new NoSuchElementException("There are no more combinations");
        }
        hasMore = counter < numCombinations;

        List<Integer> subset = new ArrayList<>(num);
        String bits = Long.toBinaryString(counter);

        for (int i = bits.length() - 1; i >= 0; i--) {
            if (bits.charAt(i) == '1') {
                subset.add(bits.length() - i - 1);
            }
        }
        return subset;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements of the combinations");
    }
}



