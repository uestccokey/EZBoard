// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.optimization.Logger;
import com.barrybecker4.optimization.OptimizationListener;
import com.barrybecker4.optimization.Optimizer;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Abstract base class for Optimization strategy.
 * <p>
 * This and derived classes uses the strategy design pattern.
 *
 * @author Barry Becker
 * @see Optimizer
 * @see Optimizee
 */
public abstract class OptimizationStrategy {

    /** The thing to be optimized */
    Optimizee optimizee_;

    private Logger logger_;

    /** listen for optimization changed events. useful for debugging. */
    protected OptimizationListener listener_;

    /**
     * Constructor
     *
     * @param optimizee the thing to be optimized.
     */
    public OptimizationStrategy(Optimizee optimizee) {
        optimizee_ = optimizee;
    }

    /**
     * @param logger the file that will record the results
     */
    public void setLogger(Logger logger) {
        logger_ = logger;
    }

    protected void log(int iteration, double fitness, double jumpSize, double deltaFitness,
                       ParameterArray params, String msg) {
        if (logger_ != null)
            logger_.write(iteration, fitness, jumpSize, deltaFitness, params, msg);
    }

    /**
     * @param initialParams the initial guess at the solution.
     * @param fitnessRange  the approximate absolute value of the fitnessRange.
     * @return optimized parameters.
     */
    public abstract ParameterArray doOptimization(ParameterArray initialParams, double fitnessRange);

    public void setListener(OptimizationListener listener) {
        listener_ = listener;
    }

    /**
     * @param currentBest current best parameter set.
     * @return true if the optimal fitness has been reached.
     */
    protected boolean isOptimalFitnessReached(ParameterArray currentBest) {
        boolean optimalFitnessReached = false;

        if (!optimizee_.evaluateByComparison()) {
            assert optimizee_.getOptimalFitness() >= 0;
            optimalFitnessReached = currentBest.getFitness() <= optimizee_.getOptimalFitness();
        }
        return optimalFitnessReached;
    }

    protected void notifyOfChange(ParameterArray params) {
        if (listener_ != null) {
            listener_.optimizerChanged(params);
        }
    }
}
