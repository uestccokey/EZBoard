// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.optimizee.optimizees;

import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * A trivial one dimensional example implementation of an {@code OptimizeeProblem}
 *
 * @author Barry Becker
 */
public class TrivialProblem extends OptimizeeProblem {

    public static final double SOLUTION_VALUE = 0.4;

    private final ParameterArray EXACT_SOLUTION = new NumericParameterArray(
            new double[]{SOLUTION_VALUE},
            new double[]{0.0},
            new double[]{1.0},
            new String[]{"param1"});

    @Override
    public ParameterArray getExactSolution() {
        return EXACT_SOLUTION;
    }

    @Override
    public ParameterArray getInitialGuess() {
        return new NumericParameterArray(
                new double[]{0.5},
                new double[]{0.0},
                new double[]{1.0},
                new String[]{"param1"});
    }

    @Override
    public double getFitnessRange() {
        return 1.0;
    }

    @Override
    public String getName() {
        return "Trivial Test Problem";
    }

    @Override
    public boolean evaluateByComparison() {
        return false;
    }

    @Override
    public double evaluateFitness(ParameterArray params) {
        return EXACT_SOLUTION.distance(params);
    }
}
