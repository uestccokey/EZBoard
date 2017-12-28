// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.Iterator;

/**
 * A strategy which naively tries all possibilities.
 * This will not be practical for problems with real valued parameters where the search space is infinite.
 *
 * @author Barry Becker
 */
public class BruteForceStrategy extends OptimizationStrategy {

    /**
     * Constructor
     *
     * @param optimizee the thing to be optimized.
     */
    public BruteForceStrategy(Optimizee optimizee) {
        super(optimizee);
    }

    /**
     * Systematically search the entire global space and return the best of the samples.
     * Stops if the optimal fitness is reached.
     *
     * @param params       the params to compare evaluation against if we evaluate BY_COMPARISON.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return best solution found using global sampling.
     */
    @Override
    public ParameterArray doOptimization(ParameterArray params, double fitnessRange) {
        Iterator<? extends ParameterArray> samples = params.findGlobalSamples(Long.MAX_VALUE);
        double bestFitness = Double.MAX_VALUE;
        ParameterArray bestParams = params.copy();

        while (samples.hasNext()) {
            ParameterArray sample = samples.next();
            double fitness;
            if (optimizee_.evaluateByComparison())
                fitness = optimizee_.compareFitness(sample, params);
            else
                fitness = optimizee_.evaluateFitness(sample);
            sample.setFitness(fitness);

            if (fitness < bestFitness) {
                bestFitness = fitness;
                notifyOfChange(sample);
                bestParams = sample.copy();
            }
            if (isOptimalFitnessReached(bestParams))
                break;
        }
        return bestParams;
    }
}
