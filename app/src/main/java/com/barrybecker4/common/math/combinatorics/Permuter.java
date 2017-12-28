/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.combinatorics;

import com.barrybecker4.common.math.MathUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides a way of iterating through all the permutations of a set of N integers (including 0).
 * If there are more than Long.MAX_VALUE permutations, this will not work.
 * The implementation does not require memory for storing the permutations since
 * only one is produced at a time.
 *
 * @author Barry Becker
 */
public class Permuter implements Iterator<List<Integer>> {

    /** the most recently created permutation */
    private List<Integer> lastPermutation;

    private boolean hasMore;

    /**
     * Constructor
     *
     * @param num the number of integer elements to permute.
     */
    public Permuter(int num) {

        long numPermutations = MathUtil.factorial(num);
        if (numPermutations < 0) {
            throw new IllegalArgumentException("The number of permutations is greater than " + Long.MAX_VALUE);
        }
        hasMore = num > 0;

        lastPermutation = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            lastPermutation.add(i);
        }
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
        List<Integer> permutation = lastPermutation;
        List<Integer> nextPermutation = new ArrayList<>(lastPermutation);

        int k = nextPermutation.size() - 2;
        if (k < 0) {
            hasMore = false;
            return lastPermutation;
        }

        while (nextPermutation.get(k) >= nextPermutation.get(k + 1)) {
            k--;
            if (k < 0) {
                hasMore = false;
                return lastPermutation;
            }
        }
        int len = nextPermutation.size() - 1;
        while (nextPermutation.get(k) >= nextPermutation.get(len)) {
            len--;
        }
        swap(nextPermutation, k, len);
        int length = nextPermutation.size() - (k + 1);
        for (int i = 0; i < length / 2; i++) {
            swap(nextPermutation, k + 1 + i, nextPermutation.size() - i - 1);
        }

        lastPermutation = nextPermutation;

        return permutation;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements of the permutation");
    }

    /**
     * Swap two entries in the list
     */
    private void swap(List<Integer> permutation, int i, int j) {
        int temp = permutation.get(i);
        permutation.set(i, permutation.get(j));
        permutation.set(j, temp);
    }
}



