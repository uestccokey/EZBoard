// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.optimizee;

import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Concrete adapter class for optimizee that does not evaluate by comparison.
 *
 * @author Barry Becker
 */
public abstract class AbsoluteOptimizee implements Optimizee {

    public boolean evaluateByComparison() {
        return false;
    }

    /** {@inheritDoc} */
    public double compareFitness(ParameterArray params1, ParameterArray params2) {
        return evaluateFitness(params2) - evaluateFitness(params1);
    }

    /**
     * Optional.
     * Override this only if you know that there is some optimal fitness that you need to reach.
     *
     * @return optimal fitness value. Terminate search when reached.
     */
    public double getOptimalFitness() {
        return 0;
    }

}
