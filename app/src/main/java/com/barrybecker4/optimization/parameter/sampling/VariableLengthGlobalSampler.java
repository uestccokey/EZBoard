// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.sampling;

import com.barrybecker4.common.math.combinatorics.Combinater;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.VariableLengthIntArray;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Finds a set of uniformly distributed global samples in a large numeric parameter space.
 * If the number of samples requested is really large, then all possible values will be returned.
 *
 * @author Barry Becker
 */
public class VariableLengthGlobalSampler extends AbstractGlobalSampler<VariableLengthIntArray> {

    /** If the requestedNumSamples is within this percent of the total, then use exhaustive search */
    private static final double CLOSE_FACTOR = 0.5;

    private VariableLengthIntArray params;

    /** used to cache the samples already tried so we do not repeat them if the requestedNumSamples is small */
    List<ParameterArray> globalSamples = new ArrayList<>();

    /** Used to enumerate all possible permutations when doing exhaustive search */
    private Combinater combinater;

    /** becomes true if the requestedNumSamples is close to the total number of permutations in the space */
    private boolean useExhaustiveSearch;

    /**
     * Constructor
     *
     * @param params              an array of params to initialize with.
     * @param requestedNumSamples desired number of samples to retrieve. If very large, will get all of them.
     */
    public VariableLengthGlobalSampler(VariableLengthIntArray params, long requestedNumSamples) {
        this.params = params;

        long totalConfigurations = Long.MAX_VALUE;
        if (params.getMaxLength() <= 60) {
            totalConfigurations = (long) Math.pow(2.0, params.getMaxLength());
        }

        // if the requested number of samples is close to the total number of configurations,
        // then just search through all possible configurations.
        numSamples = requestedNumSamples;

        useExhaustiveSearch = requestedNumSamples > CLOSE_FACTOR * totalConfigurations;
        if (useExhaustiveSearch) {
            combinater = new Combinater(params.getMaxLength());
        }
    }

    @Override
    public VariableLengthIntArray next() {
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
    private VariableLengthIntArray getNextRandomSample() {
        VariableLengthIntArray nextSample = null;
        while (globalSamples.size() < counter) {

            nextSample = (VariableLengthIntArray) params.getRandomSample();
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
    private VariableLengthIntArray getNextExhaustiveSample() {
        VariableLengthIntArray vlParams = (VariableLengthIntArray) params.copy();

        vlParams.setCombination(combinater.next());

        hasNext = combinater.hasNext();
        return vlParams;
    }
}
