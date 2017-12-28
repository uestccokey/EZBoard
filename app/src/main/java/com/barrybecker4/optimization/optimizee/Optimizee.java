// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.optimizee;

import com.barrybecker4.optimization.parameter.ParameterArray;


/**
 * This interface needs to be implemented for any object you wish to have optimized.
 * Optimization of an Optimizee is done by the Optimizer class.
 *
 * @author Barry Becker
 */
public interface Optimizee {

    /**
     * @return the name of the thing to be optimized
     */
    String getName();

    /**
     * If true is returned then compareFitness will be used and evaluateFitness will not
     * otherwise the reverse will be true.
     *
     * @return return true if we evaluate the fitness by comparison
     */
    boolean evaluateByComparison();

    /**
     * Assigns a measure of fitness to the specified set of parameters.
     * This method must return a value greater than or equal to 0.
     * This method is used if evaluateByComparison returns false.
     * Zero is considered optimal. Higher values imply lesser fitness.
     *
     * @param params the set of parameters to misc
     * @return the fitness measure. The higher the better
     */
    double evaluateFitness(ParameterArray params);

    /**
     * Compares two sets of parameters.  Used if evaluateByComparison returns true.
     *
     * @return the amount that params1 are better than params2. May be negative if params2 are better than params1.
     */
    double compareFitness(ParameterArray params1, ParameterArray params2);

    /**
     * if non-0, then we terminate when the fitness reaches this value.
     *
     * @return the best (largest) expected fitness value.
     */
    double getOptimalFitness();
}
