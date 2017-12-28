// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.optimization.parameter.types.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * represents a 1 dimensional array of parameters
 *
 * @author Barry Becker
 */
public abstract class AbstractParameterArray implements ParameterArray {

    /** Never exceed this amount */
    private static final int POPULATION_MAX = 4000;

    /** the list of parameters */
    protected List<Parameter> params_;

    /** assign a fitness (evaluation value) to this set of parameters */
    private double fitness_ = 0;

    /** Default constructor */
    protected AbstractParameterArray() {}

    /**
     * Constructor
     *
     * @param params an array of params to initialize with.
     */
    public AbstractParameterArray(Parameter[] params) {
        params_ = new ArrayList<>();
        Collections.addAll(params_, params);
    }

    /**
     * Use this constructor if you a list of parameters.
     *
     * @param params list of parameters
     */
    public AbstractParameterArray(List<Parameter> params) {
        params_ = params;
    }

    public int getSamplePopulationSize() {
        int pop = 1;
        assert params_ != null;
        for (Parameter param : params_) {
            pop *= param.isIntegerOnly() ? 2 : 6;
        }
        return Math.min(POPULATION_MAX, pop);
    }

    /**
     * @return the number of parameters in the array.
     */
    public int size() {
        return params_.size();
    }

    public void setFitness(double value) {
        fitness_ = value;
    }

    public double getFitness() {
        return fitness_;
    }

    /**
     * @return a copy of ourselves.
     */
    public AbstractParameterArray copy() {
        List<Parameter> newParams = new ArrayList<>(size());
        for (int k = 0; k < size(); k++) {
            newParams.add(get(k).copy());
        }

        AbstractParameterArray pa = createInstance();
        pa.params_ = newParams;
        pa.setFitness(fitness_);
        return pa;
    }

    protected abstract AbstractParameterArray createInstance();

    /**
     * @return the ith parameter in the array.
     */
    public Parameter get(int i) {
        return params_.get(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append("parameter[").append(i).append("] = ").append(get(i).toString());
            sb.append('\n');
        }
        sb.append("fitness = ").append(this.getFitness());
        return sb.toString();
    }

    /**
     * @return the parameters in a string of Comma Separated Values.
     */
    public String toCSVString() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < size() - 1; i++) {
            sb.append(FormatUtil.formatNumber(get(i).getValue())).append(", ");
        }
        sb.append(FormatUtil.formatNumber(get(size() - 1).getValue()));
        return sb.toString();
    }

    /**
     * Natural ordering based on the fitness evaluation assigned to this parameter array.
     *
     * @param params the parameter array to compare ourselves too.
     * @return -1 if we are less than params, 1 if greater than params, 0 if equal.
     */
    public int compareTo(ParameterArray params) {
        double diff = this.getFitness() - params.getFitness();
        if (diff < 0) {
            return -1;
        }
        return (diff > 0) ? 1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractParameterArray that = (AbstractParameterArray) o;
        return params_ == that.params_ || params_.equals(that.params_);
    }

    @Override
    public int hashCode() {
        return params_ != null ? params_.hashCode() : 0;
    }
}
