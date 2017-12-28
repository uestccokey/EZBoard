// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.Iterator;

/**
 * Global sampling optimization strategy.
 *
 * @author Barry Becker
 */
public class GlobalSampleStrategy extends OptimizationStrategy {

    /** Some number of samples to try. */
    private static final int DEFAULT_NUM_SAMPLES = 10000;

    /** the user should set this explicitly. */
    int numSample;

    /**
     * Constructor
     * No log file specified in this constructor. (use this version if running in unsigned applet).
     *
     * @param optimizee the thing to be optimized.
     */
    public GlobalSampleStrategy(Optimizee optimizee) {
        super(optimizee);
        numSample = DEFAULT_NUM_SAMPLES;
    }

    /**
     * @param samplingRate the rate at which to sample along each dimension when trying guesses globally.
     */
    public void setSamplingRate(int samplingRate) {
        assert samplingRate > 0;
        numSample = samplingRate;
    }

    /**
     * Sparsely sample the global space and return the best of the samples.
     * If the number of dimensions is large, you must use a very small number of samples per dimension
     * since the number of samples tested is equal to samplesPerDim ^ numDims.
     * For example if you have 8 dimensions and samplesPerDim = 4, then the
     * number of samples checked will be 4^8 = 65,536
     * If the number of samples for a dimension is 3 then the samples look like the following:
     * Min|--X----X----X--|Max
     * <p>
     * Doing this sampling before pursuing a search strategy increases the chance
     * that you will find the global maxima. It does not guarantee it, because the space
     * you are sampling may have a high frequency of peaks and valleys.
     *
     * @param params       the params to compare evaluation against if we evaluate BY_COMPARISON.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return best solution found using global sampling.
     */
    @Override
    public ParameterArray doOptimization(ParameterArray params, double fitnessRange) {
        Iterator<? extends ParameterArray> samples = params.findGlobalSamples(numSample);
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

            //System.out.println( "key = " + hashKey + '\n' + testParams + "\n  fitness=" + fitness );
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
