// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.types;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.optimization.parameter.redistribution.RedistributionFunction;

import java.util.Random;

/**
 * represents a general parameter to an algorithm
 *
 * @author Barry Becker
 */
public abstract class AbstractParameter implements Parameter {

    protected double value_ = 0.0;
    protected double minValue_ = 0.0;
    private double maxValue_ = 0.0;
    private double range_ = 0.0;
    private String name_ = null;
    private boolean integerOnly_ = false;

    protected RedistributionFunction redistributionFunction_;

    /**
     * Constructor
     *
     * @param val       the initial or assign parameter value
     * @param minVal    the minimum value that this parameter is allowed to take on
     * @param maxVal    the maximum value that this parameter is allowed to take on
     * @param paramName of the parameter
     */
    public AbstractParameter(double val, double minVal, double maxVal, String paramName) {
        value_ = val;
        minValue_ = minVal;
        maxValue_ = maxVal;
        range_ = maxVal - minVal;
        name_ = paramName;
        integerOnly_ = false;
    }

    public AbstractParameter(double val, double minVal, double maxVal,
                             String paramName, boolean intOnly) {
        this(val, minVal, maxVal, paramName);
        integerOnly_ = intOnly;
    }

    public boolean isIntegerOnly() {
        return integerOnly_;
    }

    /**
     * Teak the value of this parameter a little. If r is big, you may be tweaking it a lot.
     *
     * @param r the size of the (1 std deviation) gaussian neighborhood to select a random nbr from
     *          r is relative to each parameter range (in other words scaled by it).
     */
    public void tweakValue(double r, Random rand) {
        assert Math.abs(r) <= 1.5;
        if (r == 0) {
            return;  // no change in the param.
        }

        double change = (rand.nextGaussian() - 0.5) * r * getRange();
        value_ += change;
        if (value_ > getMaxValue()) {
            value_ = getMaxValue();
        } else if (value_ < getMinValue()) {
            value_ = getMinValue();
        }
        setValue(value_);
    }

    public void randomizeValue(Random rand) {
        setValue(getMinValue() + rand.nextDouble() * getRange());
    }

    @Override
    public String toString() {
        StringBuilder sa = new StringBuilder(getName());
        sa.append(" = ");
        sa.append(FormatUtil.formatNumber(getValue()));
        sa.append(" [");
        sa.append(FormatUtil.formatNumber(getMinValue()));
        sa.append(", ");
        sa.append(FormatUtil.formatNumber(getMaxValue()));
        sa.append(']');
        if (redistributionFunction_ != null) {
            sa.append(" redistributionFunction=").append(redistributionFunction_);
        }
        return sa.toString();
    }

    public Class getType() {
        if (isIntegerOnly()) {
            return int.class; // Integer.TYPE;  //int.class;
        } else {
            return float.class; // Float.TYPE; //  float.class;
        }
    }

    public void setValue(double value) {
        validateRange(value);
        this.value_ = value;
        // if there is a redistribution function, we need to apply its inverse.
        if (redistributionFunction_ != null) {
            double v = (value - minValue_) / getRange();
            this.value_ =
                    minValue_ + getRange() * redistributionFunction_.getInverseFunctionValue(v);
        }
    }

    public double getValue() {
        double value = value_;
        if (redistributionFunction_ != null) {
            double v = (value_ - minValue_) / getRange();
            v = redistributionFunction_.getValue(v);
            value = v * getRange() + minValue_;
        }
        validateRange(value);
        return value;
    }

    public double getMinValue() {
        return minValue_;
    }

    public double getMaxValue() {
        return maxValue_;
    }

    public double getRange() {
        return range_;
    }

    public String getName() {
        return name_;
    }

    public void setRedistributionFunction(RedistributionFunction function) {
        redistributionFunction_ = function;
    }

    private void validateRange(double value) {
        assert (value >= minValue_ && value <= maxValue_) :
                "Value " + value + " outside range [" + minValue_ + ", " + maxValue_ + "] for parameter " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractParameter that = (AbstractParameter) o;

        if (integerOnly_ != that.integerOnly_) return false;
        if (Double.compare(that.maxValue_, maxValue_) != 0) return false;
        if (Double.compare(that.minValue_, minValue_) != 0) return false;
        if (!that.getNaturalValue().equals(getNaturalValue())) return false;
        //if (name_ != null ? !name_.equals(that.name_) : that.name_ != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = getNaturalValue().hashCode();
        result = (int) (temp ^ (temp >>> 32));
        temp = minValue_ != +0.0d ? Double.doubleToLongBits(minValue_) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxValue_ != +0.0d ? Double.doubleToLongBits(maxValue_) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name_ != null ? name_.hashCode() : 0);
        result = 31 * result + (integerOnly_ ? 1 : 0);
        return result;
    }
}
