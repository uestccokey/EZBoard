// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * This is a hybrid optimization strategy.
 *
 * @author Barry Becker
 * @see GlobalSampleStrategy
 * @see HillClimbingStrategy
 */
public class GlobalHillClimbingStrategy extends OptimizationStrategy {

    private static final int NUM_SAMPLES = 1000;

    /**
     * Constructor.
     * use a hardcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     *
     * @param optimizee the thing to be optimized.
     */
    public GlobalHillClimbingStrategy(Optimizee optimizee) {
        super(optimizee);
    }

    /**
     * Perform the optimization of the optimizee.
     *
     * @param params       parameter array
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return optimized params
     */
    @Override
    public ParameterArray doOptimization(ParameterArray params, double fitnessRange) {

        GlobalSampleStrategy gsStrategy = new GlobalSampleStrategy(optimizee_);
        gsStrategy.setListener(listener_);
        // 3 sample points along each dimension
        gsStrategy.setSamplingRate(NUM_SAMPLES);

        // first find a good place to start
        // perhaps we should try several of the better results from global sampling.
        ParameterArray sampledParams = gsStrategy.doOptimization(params, fitnessRange);

        OptimizationStrategy strategy = new HillClimbingStrategy(optimizee_);
        strategy.setListener(listener_);
        return strategy.doOptimization(sampledParams, fitnessRange);
    }
}