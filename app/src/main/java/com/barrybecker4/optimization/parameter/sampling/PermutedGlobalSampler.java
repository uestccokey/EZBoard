// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.sampling;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.combinatorics.Permuter;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.PermutedParameterArray;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Finds a set of uniformly distributed global samples in a large numeric parameter space.
 * If the number of samples requested is really large, then all possible values will be returned.
 *
 * @author Barry Becker
 */
public class PermutedGlobalSampler extends AbstractGlobalSampler<PermutedParameterArray> {

    /** If the requestedNumSamples is within this percent of the total, then use exhaustive search */
    private static final double CLOSE_FACTOR = 0.6;

    private PermutedParameterArray params;

    /** used to cache the samples already tried so we do not repeat them if the requestedNumSamples is small */
    List<ParameterArray> globalSamples = new ArrayList<>();

    /** Used to enumerate all possible permutations when doing exhaustive search */
    private Permuter permuter;

    /** becomes true if the requestedNumSamples is close to the total number of permutations in the space */
    private boolean useExhaustiveSearch;

    /**
     * Constructor
     *
     * @param params              an array of params to initialize with.
     * @param requestedNumSamples desired number of samples to retrieve. If very large, will get all of them.
     */
    public PermutedGlobalSampler(PermutedParameterArray params, long requestedNumSamples) {
        this.params = params;

        // Divide by 2 because it does not matter which param we start with.
        // See page 13 in How to Solve It.
        long numPermutations = MathUtil.factorial(params.size()) / 2;

        // if the requested number of samples is close to the total number of permutations,
        // then we could just enumerate the permutations.
        numSamples = requestedNumSamples;

        useExhaustiveSearch = requestedNumSamples > CLOSE_FACTOR * numPermutations;
        if (useExhaustiveSearch) {
            permuter = new Permuter(params.size());
        }
    }

    @Override
    public PermutedParameterArray next() {
        if (counter >= numSamples) {
            throw new NoSuchElementException("ran out of samples.");
        }
        if (counter == numSamples - 1) {
            hasNext = false;
        }

        counter++;
        return useExhaustiveSearch ? getNextExhaustiveSample() : getNextRandomSample();
    }

    /**
     * Randomly sample the parameter space until a sample that was not seen before is found.
     *
     * @return the next random sample.
     */
    private PermutedParameterArray getNextRandomSample() {
        PermutedParameterArray nextSample = null;
        while (globalSamples.size() < counter) {

            nextSample = (PermutedParameterArray) params.getRandomSample();
            if (!globalSamples.contains(nextSample)) {
                globalSamples.add(nextSample);
            }
        }
        return nextSample;
    }

    /**
     * Globally sample the parameter space searching all possibilities.
     *
     * @return the next exhaustive sample.
     */
    private PermutedParameterArray getNextExhaustiveSample() {
        PermutedParameterArray pParams = (PermutedParameterArray) params.copy();
        pParams.setPermutation(permuter.next());

        hasNext = permuter.hasNext();
        return pParams;
    }
}
