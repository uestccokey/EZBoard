// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.types;

import com.barrybecker4.optimization.parameter.Direction;
import com.barrybecker4.optimization.parameter.redistribution.RedistributionFunction;

import java.util.Random;

/**
 * Interface to represent a general parameter of some type to an algorithm.
 *
 * @author Barry Becker
 */
public interface Parameter {

    /**
     * @return the name of the parameter.
     */
    String getName();

    /**
     * increments the parameter a little bit in the specified direction.
     * If we are already at the max end of the range, then we increment in a negative direction.
     *
     * @param direction either forward, or backward.
     * @return the size of the increment taken
     */
    double incrementByEps(Direction direction);

    /**
     * Modify the value of this parameter by a little bit.
     * The amount that it changes depends on the size of r, which is the
     * number of standard deviations of the gaussian probability distribution to use.
     *
     * @param r the size of the (1 std deviation) gaussian neighborhood to select a random nbr from
     *          (relative to each parameter range).
     */
    void tweakValue(double r, Random rand);

    /**
     * Randomizes the value within its range.
     * If no redistribution function, then the distribution is uniform.
     */
    void randomizeValue(Random rand);

    /**
     * @return a value whose type matches the type of the parameter.
     * (e.g. String for StringParameter Integer for IntegerParameter)
     */
    Object getNaturalValue();

    /**
     * @return the double value of this parameter.
     * The natural value needs to be converted to some double.
     * e.g a BooleanParameter is returned as a 0 (false) or 1 (true).
     */
    double getValue();

    /**
     * This optional function redistributes the normally uniform
     * parameter distribution into something potentially completely different
     * like a gaussian, or one where specific values have higher probability than others.
     *
     * @param func the redistribution function to use
     */
    void setRedistributionFunction(RedistributionFunction func);

    /**
     * Set the value of the parameter.
     * If there is a redistribution function, then
     * set the value in the inverse redistribution space - at least
     * until I implement the inverse redistribution function.
     *
     * @param value value to set.
     */
    void setValue(double value);

    /**
     * @return the minimum value of the parameters range.
     */
    double getMinValue();

    /**
     * @return the maximum value of the parameters range.
     */
    double getMaxValue();

    /**
     * @return the parameters range.
     */
    double getRange();

    /**
     * @return true if this parameter should be treated as an integer and not a double.
     */
    boolean isIntegerOnly();

    /**
     * @return class type of the underlying parameter value (e.g. float.class or int.class)
     */
    Class getType();

    /**
     * All parameters can produce copies of themselves.
     *
     * @return new copy of the parameter
     */
    Parameter copy();
}
