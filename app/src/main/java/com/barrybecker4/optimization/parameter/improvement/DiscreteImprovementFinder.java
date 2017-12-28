// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.improvement;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.Set;

/**
 * represents a 1 dimensional, variable length, array of unique integer parameters.
 * The order of the integers does not matter.
 *
 * @author Barry Becker
 */
public class DiscreteImprovementFinder {

    /** don't try more than this many times to find improvement on any iteration */
    private static final int MAX_TRIES = 1000;

    public ParameterArray params;

    /** Default constructor */
    public DiscreteImprovementFinder(ParameterArray params) {
        this.params = params;
    }

    /**
     * Try to find a parameterArray that is better than what we have now by evaluating using the optimizee passed in.
     * Try swapping parameters randomly until we find an improvement (if we can).
     *
     * @param optimizee something that can evaluate parameterArrays.
     * @param jumpSize  how far to move in the direction of improvement
     * @param cache     set of parameters that have already been tested. This is important for cases where the
     *                  parameters are discrete and not continuous.
     * @return the improvement which contains the improved parameter array and possibly a revised jumpSize.
     */
    public Improvement findIncrementalImprovement(Optimizee optimizee, double jumpSize,
                                                  Set<ParameterArray> cache) {
        int numTries = 0;
        double fitnessDelta;
        jumpSize *= 0.98;
        Improvement improvement = new Improvement(params, 0, jumpSize);

        do {
            ParameterArray nbr = params.getRandomNeighbor(jumpSize);
            fitnessDelta = 0;

            if (!cache.contains(nbr)) {
                cache.add(nbr);
                if (optimizee.evaluateByComparison()) {
                    fitnessDelta = optimizee.compareFitness(nbr, params);
                } else {
                    double fitness = optimizee.evaluateFitness(nbr);
                    fitnessDelta = params.getFitness() - fitness;
                    nbr.setFitness(fitness);
                }

                if (fitnessDelta > 0) {
                    improvement = new Improvement(nbr, fitnessDelta, jumpSize);
                }
            }
            numTries++;
            jumpSize *= 1.001;

        } while (fitnessDelta <= 0 && numTries < MAX_TRIES);
        //System.out.println("incremental improvement = " + improvement.getImprovement() + " numTries=" + numTries + " jumpSize=" + jumpSize
        //        + "\n num nodes in improvedParams=" + improvement.getParams().size() + " fit=" + improvement.getParams().getFitness());

        return improvement;
    }
}
