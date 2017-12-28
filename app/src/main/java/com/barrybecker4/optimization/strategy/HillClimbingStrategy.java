// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.improvement.Improvement;

import java.util.HashSet;
import java.util.Set;

/**
 * Hill climbing optimization strategy.
 *
 * @author Barry Becker
 */
public class HillClimbingStrategy extends OptimizationStrategy {

    /** make steps of this size toward the local maxima, until we need something else. */
    private static final double INITIAL_JUMP_SIZE = 0.9;

    /** continue optimization iteration until the improvement in fitness is less than this. */
    private static final double FITNESS_EPS_PERCENT = 0.0000001;
    protected static final double JUMP_SIZE_EPS = 0.000000001;


    /**
     * Constructor
     * use a hardcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     *
     * @param optimizee the thing to be optimized.
     */
    public HillClimbingStrategy(Optimizee optimizee) {
        super(optimizee);
    }

    /**
     * Finds a local maxima.
     * It is a bit like newton's method, but in n dimensions.
     * If we make a jump and find that we are worse off than before, we will backtrack and reduce the stepsize so
     * that we can be guaranteed to improve my some amount on every iteration until the incremental improvement
     * is less than the threshold fitness_eps.
     *
     * @param params       the initial value for the parameters to optimize.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return the optimized params.
     */
    @Override
    public ParameterArray doOptimization(ParameterArray params, double fitnessRange) {

        ParameterArray currentParams = params.copy();

        double jumpSize = INITIAL_JUMP_SIZE;

        if (!optimizee_.evaluateByComparison()) {
            // get the initial baseline fitness value.
            currentParams.setFitness(optimizee_.evaluateFitness(currentParams));
        }
        int numIterations = 0;
        log(0, currentParams.getFitness(), 0.0, 0.0, currentParams, "initial test");
        notifyOfChange(currentParams);

        double fitnessEps = fitnessRange * FITNESS_EPS_PERCENT / 100.0;

        // Use cache to avoid repeats. This can be a real issue if  we have a discrete problem space.
        Set<ParameterArray> cache = new HashSet<ParameterArray>();
        cache.add(currentParams);

        Improvement improvement = null;
        boolean improved;

        // iterate until there is no significant improvement between iterations,
        // of the jumpSize is too small (below some threshold).
        do {
            //System.out.println( "iter=" + numIterations + " FITNESS = " + currentParams.getFitness() + "  ------------");

            improvement = currentParams.findIncrementalImprovement(optimizee_, jumpSize, improvement, cache);

            numIterations++;
            currentParams = improvement.getParams();
            jumpSize = improvement.getNewJumpSize();
            notifyOfChange(currentParams);
            improved = improvement.getImprovement() > fitnessEps;

        } while (improved
                && (jumpSize > JUMP_SIZE_EPS)
                && !isOptimalFitnessReached(currentParams));

        System.out.println("The optimized parameters after " + numIterations + " iterations are " + currentParams);
        System.out.println("Last improvement = " + improvement + " jumpSize=" + jumpSize + " improved=" + improved);
        return currentParams;
    }

}
