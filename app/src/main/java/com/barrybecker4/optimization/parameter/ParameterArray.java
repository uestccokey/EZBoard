// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.improvement.Improvement;
import com.barrybecker4.optimization.parameter.types.Parameter;

import java.util.Iterator;
import java.util.Set;

/**
 * represents an array of parameters
 *
 * @author Barry Becker
 */
public interface ParameterArray extends Comparable<ParameterArray> {

    /**
     * Some parameter arrays may have variable numbers of parameters.
     *
     * @return the number of parameters in the array.
     */
    int size();

    /**
     * Lower values are better.
     *
     * @param value fitness value to assign to this parameter array.
     */
    void setFitness(double value);

    /**
     * Lower values are better.
     *
     * @return the fitness value.
     */
    double getFitness();

    /**
     * @return a copy of ourselves.
     */
    ParameterArray copy();

    /**
     * @return the ith parameter in the array.
     */
    Parameter get(int i);

    /**
     * @return a reasonable size for a sampling of values from the parameter space.
     */
    int getSamplePopulationSize();

    /**
     * Globally sample the parameter space with a uniform distribution.
     *
     * @param requestedNumSamples approximate number of samples to retrieve.
     *                            If the problem space is small and requestedNumSamples is large, it may not be possible to return this
     *                            many unique samples. In this case all possibilities will be returned.
     * @return an iterator that is capable of producing the specified number of unique samples.
     */
    Iterator<? extends ParameterArray> findGlobalSamples(long requestedNumSamples);

    /**
     * Try to find a parameterArray that is better than what we have now by evaluating using the optimizee passed in.
     *
     * @param optimizee       something that can evaluate parameterArrays.
     * @param jumpSize        how far to move in the direction of improvement
     * @param lastImprovement the improvement we had most recently. May be null if none.
     * @param cache           set of parameters that have already been tested. This is important for cases where the
     *                        parameters are discrete and not continuous.
     * @return the improvement which contains the improved parameter array and possibly a revised jumpSize.
     */
    Improvement findIncrementalImprovement(Optimizee optimizee, double jumpSize,
                                           Improvement lastImprovement, Set<ParameterArray> cache);

    /**
     * Some measure of the distance between parameter arrays of the same type.
     * The way this is computed is very dependent on the implementation of each sort of parameter array.
     *
     * @return a measure of the distance between this parameter array and another.
     */
    double distance(ParameterArray pa);

    /**
     * @param radius the size of the (1 std deviation) gaussian neighborhood to select a random nbr from
     *               (relative to each parameter range).
     * @return the random nbr.
     */
    ParameterArray getRandomNeighbor(double radius);

    /**
     * @return get a completely random solution in the parameter space.
     */
    ParameterArray getRandomSample();

    String toString();

    /**
     * @return the parameters in a string of Comma Separated Values.
     */
    String toCSVString();
}
