// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.improvement;

import com.barrybecker4.common.math.Vector;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Represents an incremental improvement for a ParameterArray.
 * (unless the improvement is 0 or negative that is)
 *
 * @author Barry Becker
 */
public class Improvement {

    /** The (hopefully) improved set of parameters */
    private ParameterArray parameters;

    /** The amount we improved compared to where we were before (if any) */
    private double improvement;

    /** Possibly revised jumpSize */
    private double newJumpSize;

    /** The direction we moved toward this improvement */
    private Vector gradient;

    /**
     * Constructor
     *
     * @param improvedParams the (hopefully) improved parameters.
     * @param improvement    the amount of improvement since last iteration.
     * @param newJumpSize    size of the iteration increment.
     * @param gradient       direction that we are currently moving in.
     */
    public Improvement(ParameterArray improvedParams, double improvement, double newJumpSize, Vector gradient) {
        this(improvedParams, improvement, newJumpSize);
        this.gradient = gradient;
    }

    /**
     * Constructor
     *
     * @param improvedParams the newly improved parameters.
     * @param improvement    the amount of improvement since last iteration.
     * @param newJumpSize    size of the iteration increment.
     */
    public Improvement(ParameterArray improvedParams, double improvement, double newJumpSize) {
        parameters = improvedParams;
        this.improvement = improvement;
        this.newJumpSize = newJumpSize;
    }

    public ParameterArray getParams() {
        return parameters;
    }

    public double getImprovement() {
        return improvement;
    }

    public double getNewJumpSize() {
        return newJumpSize;
    }

    public Vector getGradient() {
        return gradient;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("Improvement = ").append(getImprovement())
                .append(" New jumpsize=").append(getNewJumpSize())
                .append(" Gradient = ").append(getGradient());
        return bldr.toString();
    }
}
