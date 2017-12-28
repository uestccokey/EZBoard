// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.improvement;


import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.Vector;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.Direction;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.types.Parameter;

/**
 * Utility class for maintaining the data vectors for the iteration when hill climbing
 * over a numerical parameter space.
 */
public class ImprovementIteration {

    private Vector delta;
    private Vector fitnessDelta;
    private Vector gradient;
    private Vector oldGradient;

    /**
     * Constructor
     *
     * @param params      current parameters
     * @param oldGradient the old steepest ascent gradient if we know it.
     */
    public ImprovementIteration(NumericParameterArray params, Vector oldGradient) {
        delta = params.asVector();
        fitnessDelta = params.asVector();
        gradient = params.asVector();
        if (oldGradient != null) {
            this.oldGradient = oldGradient;
        } else {
            this.oldGradient = params.asVector();

            // initialize the old gradient to the unit vector (any random direction will do)
            for (int i = 0; i < params.size(); i++) {
                this.oldGradient.set(i, 1.0);
            }
            this.oldGradient = this.oldGradient.normalize();
        }
    }

    public Vector getGradient() {
        return gradient;
    }

    public Vector getOldGradient() {
        return oldGradient;
    }

    /**
     * Compute the squares in one of the iteration directions and add it to the running sum.
     *
     * @return the sum of squares in one of the iteration directions.
     */
    public double incSumOfSqs(int i, double sumOfSqs, Optimizee optimizee,
                              ParameterArray params, ParameterArray testParams) {

        double fwdFitness;
        double bwdFitness;

        Parameter p = testParams.get(i);
        // increment forward.
        delta.set(i, p.incrementByEps(Direction.FORWARD));

        fwdFitness = findFitnessDelta(optimizee, params, testParams);

        // revert the increment
        p.incrementByEps(Direction.BACKWARD);
        // this checks the fitness on the other side (backwards).
        p.incrementByEps(Direction.BACKWARD);

        bwdFitness = findFitnessDelta(optimizee, params, testParams);

        fitnessDelta.set(i, fwdFitness - bwdFitness);
        return sumOfSqs + (fitnessDelta.get(i) * fitnessDelta.get(i)) / (delta.get(i) * delta.get(i));
    }

    /**
     * @param optimizee  the thing being optimized
     * @param params     the current parameters
     * @param testParams the new set of parameters being evaluated.
     * @return the incremental change in fitness
     */
    private double findFitnessDelta(Optimizee optimizee, ParameterArray params, ParameterArray testParams) {
        double incFitness;
        if (optimizee.evaluateByComparison()) {
            incFitness = optimizee.compareFitness(testParams, params);
        } else {
            incFitness = params.getFitness() - optimizee.evaluateFitness(testParams);
        }
        return incFitness;
    }

    /**
     * Update gradient.
     * Use EPS if the gradLength is 0.
     */
    void updateGradient(double jumpSize, double gradLength) {
        double gradLen = gradLength == 0 ? MathUtil.EPS_MEDIUM : gradLength;
        for (int i = 0; i < delta.size(); i++) {
            double denominator = delta.get(i) * gradLen;
            gradient.set(i, jumpSize * fitnessDelta.get(i) / denominator);
        }
    }
}