// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.types;

//import com.barrybecker4.optimization.parameter.ParameterChangeListener;
//import com.barrybecker4.optimization.parameter.ui.ParameterWidget;
//import com.barrybecker4.optimization.parameter.ui.StringParameterWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a general parameter to an algorithm
 *
 * @author Barry Becker
 */
public class StringParameter extends IntegerParameter {
    private List<String> values_;

    public StringParameter(int index, List<String> values, String paramName) {
        super(index, 0, values.size() - 1, paramName);
        values_ = values;
    }

    public StringParameter(Enum val, Enum[] enumValues, String paramName) {
        super(val.ordinal(), 0, enumValues.length - 1, paramName);
        List<String> values = new ArrayList<>(enumValues.length);

        for (Enum v : enumValues) {
            values.add(v.toString());
        }
        values_ = values;
    }

    @Override
    public Parameter copy() {
        StringParameter p = new StringParameter((int) getValue(), values_, getName());
        p.setRedistributionFunction(redistributionFunction_);
        return p;
    }

    @Override
    public Object getNaturalValue() {
        return values_.get((int) getValue());
    }

    public List<String> getStringValues() {
        return values_;
    }

    @Override
    protected boolean isOrdered() {
        return false;
    }

    @Override
    public Class getType() {
        return String.class;
    }

//   @Override
//   public ParameterWidget createWidget(ParameterChangeListener listener) {
//        return new StringParameterWidget(this, listener);
//    }
}
