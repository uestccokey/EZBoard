// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.strategy;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Simulated annealing optimization strategy.
 * See http://en.wikipedia.org/wiki/Annealing for an explanation of the name.
 *
 * @author Barry Becker
 */
public class SimulatedAnnealingStrategy extends OptimizationStrategy {

    /** The number of iterations in the inner loop divided by the number of dimensions in the search space */
    private static final int N = 10;
    private static final int NUM_TEMP_ITERATIONS = 20;

    /** the amount to drop the temperature on each temperature iteration. */
    private static final double TEMP_DROP_FACTOR = 0.5;

    /** the client should really set the tempMax using setTemperatureMax before running. */
    private static final double DEFAULT_TEMP_MAX = 1000;
    private double tempMax = DEFAULT_TEMP_MAX;


    /**
     * Constructor.
     * use a hardcoded static data interface to initialize.
     * so it can be easily run in an applet without using resources.
     *
     * @param optimizee the thing to be optimized.
     */
    public SimulatedAnnealingStrategy(Optimizee optimizee) {
        super(optimizee);
    }

    /**
     * @param tempMax the initial temperature at the start of the simulated annealing process (before cooling).
     */
    public void setMaxTemperature(double tempMax) {
        this.tempMax = tempMax;
    }

    /**
     * finds a local maxima.
     * <p>
     * The concept is based on the manner in which liquids freeze or metals recrystallize in the process of annealing.
     * In an annealing process, an initially at high temperature and disordered liquid, is slowly cooled so that the system
     * is approximately in thermodynamic equilibrium at any point in the process. As cooling proceeds, the system becomes
     * more ordered and approaches a "frozen" ground state at T=0. Hence the process can be thought of as an adiabatic
     * approach to the lowest energy state. If the initial temperature of the system is too low, or cooling is too fast,
     * the system may become quenched, forming defects or freezing out in metastable states
     * (ie. trapped in a local minimum energy state).
     * <p>
     * In many ways the algorithm is similar to hill-climbing.
     * The main differences are:
     * - The next candidate solution is selected randomly within a gaussian neighborhood that shrinks
     * with the temperature and within the current iteration.
     * - You can actually make a move toward a solution that is worse. This allows the algorithm to
     * move out of local optima.
     *
     * @param params       the initial value for the parameters to optimize.
     * @param fitnessRange the approximate absolute value of the fitnessRange.
     * @return the optimized params.
     */
    @Override
    public ParameterArray doOptimization(ParameterArray params, double fitnessRange) {

        int ct = 0;
        double temperature = tempMax;
        double tempMin = tempMax / Math.pow(2.0, NUM_TEMP_ITERATIONS);

        if (!optimizee_.evaluateByComparison()) {
            double currentFitness = optimizee_.evaluateFitness(params);
            params.setFitness(currentFitness);
        }

        // store the best solution we found at any given temperature iteration and use that as the initial
        // start of the next temperature iteration.
        ParameterArray bestParams = params.copy();
        ParameterArray currentParams;

        do { // temperature iteration (temperature drops each time through)
            currentParams = bestParams;

            do {
                currentParams = findNeighbor(currentParams, ct, temperature);

                if (currentParams.getFitness() < bestParams.getFitness()) {
                    bestParams = currentParams.copy();
                    notifyOfChange(bestParams);
                }
                ct++;

            } while (ct < N * currentParams.size() && !isOptimalFitnessReached(currentParams));

            ct = 0;
            // keep Reducing the temperature until it reaches tempMin
            temperature *= TEMP_DROP_FACTOR;

        } while (temperature > tempMin && !isOptimalFitnessReached(currentParams));

        //System.out.println("T=" + temperature + "  currentFitness = " + bestParams.getFitness());
        log(ct, bestParams.getFitness(), 0, 0, bestParams, FormatUtil.formatNumber(temperature));

        return bestParams;
    }

    /**
     * Select a new point in the neighborhood of our current location
     * The neighborhood we select from has a radius of r.
     *
     * @param params      current location in the parameter space.
     * @param ct          iteration count.
     * @param temperature current temperature. Gets cooler with every successive temperature iteration.
     * @return neighboring point that is hopefully better than params.
     */
    private ParameterArray findNeighbor(ParameterArray params, int ct, double temperature) {

        //double r = (tempMax/5.0+temperature) / (8.0*(N/5.0+ct)*tempMax);
        double r = temperature / ((N + ct) * tempMax);
        ParameterArray newParams = params.getRandomNeighbor(r);
        double dist = params.distance(newParams);

        double deltaFitness;
        double newFitness;
        if (optimizee_.evaluateByComparison()) {
            deltaFitness = optimizee_.compareFitness(newParams, params);
        } else {
            newFitness = optimizee_.evaluateFitness(newParams);
            newParams.setFitness(newFitness);
            deltaFitness = params.getFitness() - newFitness;
        }

        double probability = Math.pow(Math.E, tempMax * deltaFitness / temperature);
        boolean useWorseSolution = MathUtil.RANDOM.nextDouble() < probability;

        if (deltaFitness > 0 || useWorseSolution) {
            // we always select the solution if it has a better fitness,
            // but we sometimes select a worse solution if the second term evaluates to true.
            if (deltaFitness < 0 && useWorseSolution) {
                System.out.println("Selected worse solution with prob="
                        + probability + " delta=" + deltaFitness + " / temp=" + temperature);
            }
            params = newParams;
        }
        //System.out.println("T="+temperature+" ct="+ct+" dist="+dist+" deltaFitness="
        //        + deltaFitness+"  currentFitness = "+ params.getFitness() );
        log(ct, params.getFitness(), r, dist, params, FormatUtil.formatNumber(temperature));
        return params;
    }

}
