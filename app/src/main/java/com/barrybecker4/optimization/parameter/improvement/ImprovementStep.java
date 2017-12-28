// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.improvement;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.Set;

/**
 * A step in the Hill climbing optimization strategy.
 * Hopefully heads in the right direction.
 *
 * @author Barry Becker
 */
public class ImprovementStep {

    private Optimizee optimizee_;
    private ImprovementIteration iter_;
    private double gradLength;
    private Set<ParameterArray> cache;
    private double jumpSize;
    private double improvement;
    private double oldFitness;
    private boolean improved;

    /** continue optimization iteration until the improvement in fitness is less than this. */
    protected static final double JUMP_SIZE_EPS = 0.000000001;

    /** Increase the size of the radius by this multiplier */
    private static final double RADIUS_EXPANDER = 1.5;

    public static final double JUMP_SIZE_INC_FACTOR = 1.3;
    public static final double JUMP_SIZE_DEC_FACTOR = 0.7;

    /**
     * Constructor
     * use a hardcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     *
     * @param optimizee the thing to be optimized.
     */
    public ImprovementStep(Optimizee optimizee, ImprovementIteration iter, double gradLength, Set<ParameterArray> cache,
                           double jumpSize, double oldFitness) {
        optimizee_ = optimizee;
        iter_ = iter;
        this.improvement = 0;
        this.gradLength = gradLength;
        this.cache = cache;
        this.jumpSize = jumpSize;
        this.oldFitness = oldFitness;
    }

    public double getJumpSize() {
        return jumpSize;
    }

    public double getImprovement() {
        return improvement;
    }

    /**
     * @param params the initial value for the parameters to optimize.
     * @return the parameters to try next.
     */
    public NumericParameterArray findNextParams(NumericParameterArray params) {
        NumericParameterArray currentParams = params;
        int maxTries = 100;
        int numTries = 0;

        do {
            currentParams = findNextCandidateParams(currentParams);
            numTries++;

        } while (!improved && (jumpSize > JUMP_SIZE_EPS) && numTries < maxTries);

        return currentParams;
    }

    /**
     * Consider a nearby neighbor of the passed in params to see if it will yield improvement.
     *
     * @param params parameter set to find neighbor of.
     * @return nearby location.
     */
    private NumericParameterArray findNextCandidateParams(NumericParameterArray params) {
        improved = true;
        NumericParameterArray currentParams = params;
        NumericParameterArray oldParams = currentParams.copy();

        iter_.updateGradient(jumpSize, gradLength);
        //log("gradient = " + iter_.gradient + " jumpSize="+ jumpSize);
        currentParams = currentParams.copy();
        currentParams.add(iter_.getGradient());
        double gaussRadius = 0.01;
        boolean sameParams = false;

        // for problems with integer params, we want to avoid testing the same candidate over again. */
        while (cache.contains(currentParams)) {
            sameParams = true;
            currentParams = currentParams.getRandomNeighbor(gaussRadius);
            gaussRadius *= RADIUS_EXPANDER;
        }
        cache.add(currentParams);

        if (optimizee_.evaluateByComparison()) {
            currentParams.setFitness(optimizee_.compareFitness(currentParams, oldParams));
            if (currentParams.getFitness() < 0) {
                improved = false;
            }
            improvement = currentParams.getFitness();
        } else {
            currentParams.setFitness(optimizee_.evaluateFitness(currentParams));
            if (currentParams.getFitness() >= oldFitness) {
                improved = false;
            }
            improvement = oldFitness - currentParams.getFitness();
        }

        if (!improved) {
            currentParams = oldParams;
            if (!sameParams) {
                // we have not improved, try again with a reduced jump size.
                //log( "Warning: the new params are worse so reduce the step size and try again");
                //log(numIterations, currentParams.getFitness(), jumpSize, Double.NaN, currentParams, "not improved");
                jumpSize *= JUMP_SIZE_DEC_FACTOR;
            }
        }
        return currentParams;
    }
}
