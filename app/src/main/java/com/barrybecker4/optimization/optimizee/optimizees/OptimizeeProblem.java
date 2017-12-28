// Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.optimizee.optimizees;

import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Abstract base class for optimizer test problems.
 *
 * @author Barry Becker
 */
public abstract class OptimizeeProblem implements Optimizee {

    /** @return the exact solution for this problem. */
    public abstract ParameterArray getExactSolution();

    /** @return the exact solution for this problem. */
    public abstract ParameterArray getInitialGuess();

    /**
     * @param sol solution
     * @return distance from the exact solution as the error.
     */
    public double getError(ParameterArray sol) {
        return 100.0 * (sol.getFitness() - getOptimalFitness()) / getFitnessRange();
    }

    @Override
    public double getOptimalFitness() {
        return 0;
    }

    @Override
    public double compareFitness(ParameterArray a, ParameterArray b) {
        return 0.0;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return approximate range of fitness values (usually 0 to this number).
     */
    public abstract double getFitnessRange();


    public static void showSolution(OptimizeeProblem problem, ParameterArray solution) {
        System.out.println("\n************************************************************************");
        System.out.println("The solution to the " + problem.getName() + " test problem is :\n" + solution);
        System.out.println("Which evaluates to: " + problem.evaluateFitness(solution));
        System.out.println("We expected to get exactly " + problem.getExactSolution());
    }
}
