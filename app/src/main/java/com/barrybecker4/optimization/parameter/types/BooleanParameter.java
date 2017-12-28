// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.types;

import com.barrybecker4.optimization.parameter.redistribution.BooleanRedistribution;

/**
 * Represents a boolean parameter to an algorithm.
 *
 * @author Barry Becker
 */
public class BooleanParameter extends IntegerParameter {

    public BooleanParameter(boolean val, String paramName) {
        super(val ? 1 : 0, 0, 1, paramName);
    }

    public static BooleanParameter createSkewedParameter(boolean value, String paramName,
                                                         double percentTrue) {
        BooleanParameter param = new BooleanParameter(value, paramName);
        param.setRedistributionFunction(new BooleanRedistribution(percentTrue));
        return param;
    }

    @Override
    public Parameter copy() {
        BooleanParameter p = new BooleanParameter((Boolean) getNaturalValue(), getName());
        p.setRedistributionFunction(redistributionFunction_);
        return p;
    }

    /**
     * @return true if getValue is odd.
     */
    @Override
    public Object getNaturalValue() {
        return ((int) getValue() % 2) == 1;
    }

    @Override
    protected boolean isOrdered() {
        return false;
    }

    @Override
    public Class getType() {
        return boolean.class;
    }
}
