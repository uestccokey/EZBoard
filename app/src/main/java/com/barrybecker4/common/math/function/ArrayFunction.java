/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.math.function;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.common.math.interplolation.InterpolationMethod;
import com.barrybecker4.common.math.interplolation.Interpolator;

/**
 * The function is represented with an array of lookups that
 * can be interpolated using some method.
 *
 * @author Barry Becker
 */
public class ArrayFunction implements InvertibleFunction {

    /** the linear function lookup table. */
    private double[] functionMap;

    /** The inverse lookup for the main function. */
    private double[] inverseFunctionMap;

    private Interpolator interpolator;
    private Interpolator inverseInterpolator;


    /**
     * Constructor.
     *
     * @param func         the array representing function values.
     * @param interpMethod method to use when interpolating
     */
    private ArrayFunction(double[] func, InterpolationMethod interpMethod) {
        this(func, new FunctionInverter(func).createInverseFunction(new Range(0, 1.0)), interpMethod);
    }

    /**
     * Constructor.
     * Use this version of the constructor if you already know the inverse function and do not
     * want to compute it (because computing it will not be as accurate).
     *
     * @param func function definition.
     */
    public ArrayFunction(double[] func, double[] inverseFunc) {
        this(func, inverseFunc, InterpolationMethod.LINEAR);
    }

    /**
     * Constructor.
     * Use this version of the constructor if you already know the inverse function and do not
     * want to compute it (because computing it will not be as accurate).
     *
     * @param func array values representing the function
     */
    private ArrayFunction(double[] func, double[] inverseFunc, InterpolationMethod interpMethod) {
        functionMap = func;
        inverseFunctionMap = inverseFunc;
        interpolator = interpMethod.createInterpolator(func);
        inverseInterpolator = interpMethod.createInterpolator(inverseFunc);
    }

    /**
     * Constructor.
     *
     * @param funcMap array values representing the function
     */
    public ArrayFunction(double[] funcMap) {
        this(funcMap, InterpolationMethod.LINEAR);
    }

    /**
     * Evaluate the function at the specified point.
     *
     * @param value an x value
     * @return y value for specified x value
     */
    @Override
    public double getValue(double value) {

        return interpolator.interpolate(value);
    }

    @Override
    public Range getDomain() {
        return new Range(0, 1.0);
    }

    /**
     * Get x for specified y.
     *
     * @param value y value
     * @return inverse function value.
     */
    @Override
    public double getInverseValue(double value) {

        return inverseInterpolator.interpolate(value);
    }

    public void setInterpolationMethod(InterpolationMethod interp) {
        interpolator = interp.createInterpolator(functionMap);
        inverseInterpolator = interp.createInterpolator(inverseFunctionMap);
    }
}
