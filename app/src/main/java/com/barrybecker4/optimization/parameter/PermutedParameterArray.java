// Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.improvement.DiscreteImprovementFinder;
import com.barrybecker4.optimization.parameter.improvement.Improvement;
import com.barrybecker4.optimization.parameter.sampling.PermutedGlobalSampler;
import com.barrybecker4.optimization.parameter.types.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * represents a 1 dimensional array of unique permuted parameters.
 * There are no duplicates among the parameters, and this array holds them in some permuted order.
 * This sort of parameter array could be used to represent the order of cities visited in
 * the traveling salesman problem, for example.
 *
 * @author Barry Becker
 */
public class PermutedParameterArray extends AbstractParameterArray {

    private PermutedDistanceCalculator distanceCalculator = new PermutedDistanceCalculator();

    /** Default constructor */
    protected PermutedParameterArray() {
    }

    /**
     * Constructor
     *
     * @param params an array of params to initialize with.
     */
    public PermutedParameterArray(Parameter[] params) {
        super(params);
    }

    /**
     * Constructor
     *
     * @param params an list of params to initialize with.
     */
    public PermutedParameterArray(List<Parameter> params) {
        super(params);
    }

    /**
     * Permute the parameters according to the specified permutation
     * of 0 based indices.
     */
    public void setPermutation(List<Integer> indices) {

        assert indices.size() == size();
        List<Parameter> newParams = new ArrayList<>(size());
        for (int i : indices) {
            newParams.add(get(i));
        }
        params_ = newParams;
    }

    @Override
    protected PermutedParameterArray createInstance() {
        return new PermutedParameterArray();
    }

    protected ParameterArray reverse() {
        AbstractParameterArray paramCopy = this.copy();
        int len = size();

        for (int i = 0; i < len / 2; i++) {
            Parameter temp = paramCopy.params_.get(i);
            paramCopy.params_.set(i, paramCopy.params_.get(len - i - 1));
            paramCopy.params_.set(len - i - 1, temp);
        }
        return paramCopy;
    }

    /**
     * The distance computation will be quite different for this than a regular parameter array.
     * We want the distance to represent a measure of the amount of similarity between two permutations.
     * If there are similar runs between two permutations, then the distance should be relatively small.
     * N^2 operation, where N is the number of params.
     *
     * @return the distance between this parameter array and another.
     */
    public double distance(ParameterArray pa) {
        return distanceCalculator.findDistance(this, (PermutedParameterArray) pa);
    }

    /**
     * Create a new permutation that is not too distant from what we have now.
     *
     * @param radius a indication of the amount of variation to use. 0 is none, 3 is a lot.
     *               Change Math.min(1, 10 * radius * N/100) of the entries, where N is the number of params
     * @return the random nbr.
     */
    public PermutedParameterArray getRandomNeighbor(double radius) {

        if (size() <= 1) return this;

        int numToSwap = Math.max(1, (int) (10.0 * radius * size() / 100.0));

        PermutedParameterArray nbr = (PermutedParameterArray) this.copy();
        for (int k = 0; k < numToSwap; k++) {
            int index1 = MathUtil.RANDOM.nextInt(size());
            int index2 = MathUtil.RANDOM.nextInt(size());
            while (index2 == index1) {
                index2 = MathUtil.RANDOM.nextInt(size());
            }
            Parameter temp = nbr.params_.get(index1);
            nbr.params_.set(index1, nbr.params_.get(index2));
            nbr.params_.set(index2, temp);
        }
        return nbr;
    }

    /**
     * Globally sample the parameter space.
     *
     * @param requestedNumSamples approximate number of samples to retrieve.
     *                            If the problem space is small and requestedNumSamples is large, it may not be possible to return this
     *                            many unique samples.
     * @return some number of unique samples.
     */
    public Iterator<PermutedParameterArray> findGlobalSamples(long requestedNumSamples) {
        return new PermutedGlobalSampler(this, requestedNumSamples);
    }

    /**
     * {@inheritDoc}
     * Try swapping parameters randomly until we find an improvement (if we can);
     */
    public Improvement findIncrementalImprovement(Optimizee optimizee, double jumpSize,
                                                  Improvement lastImprovement, Set<ParameterArray> cache) {
        DiscreteImprovementFinder finder = new DiscreteImprovementFinder(this);
        return finder.findIncrementalImprovement(optimizee, jumpSize, cache);
    }

    /**
     * @return get a completely random solution in the parameter space.
     */
    public ParameterArray getRandomSample() {

        List<Parameter> theParams = new ArrayList<>(params_);
        Collections.shuffle(theParams, MathUtil.RANDOM);

        List<Parameter> newParams = new ArrayList<>(size());
        for (Parameter p : theParams) {
            newParams.add(p.copy());
        }

        return new PermutedParameterArray(newParams);
    }
}
