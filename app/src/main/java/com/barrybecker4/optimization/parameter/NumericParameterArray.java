// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.Vector;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.improvement.Improvement;
import com.barrybecker4.optimization.parameter.improvement.ImprovementIteration;
import com.barrybecker4.optimization.parameter.improvement.ImprovementStep;
import com.barrybecker4.optimization.parameter.sampling.NumericGlobalSampler;
import com.barrybecker4.optimization.parameter.types.DoubleParameter;
import com.barrybecker4.optimization.parameter.types.Parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * represents a 1 dimensional array of parameters
 *
 * @author Barry Becker
 */
public class NumericParameterArray extends AbstractParameterArray {

    /** default number of steps to go from the min to the max */
    private static final int DEFAULT_NUM_STEPS = 10;
    private int numSteps_ = DEFAULT_NUM_STEPS;

    /** If the dot product of the new gradient with the old is less than this, then decrease the jump size. */
    private static final double MIN_DOT_PRODUCT = 0.3;

    /** If the dot product of the new gradient with the old is greater than this, then increase the jump size. */
    private static final double MAX_DOT_PRODUCT = 0.98;

    /** Default constructor */
    protected NumericParameterArray() {}

    /**
     * Constructor
     *
     * @param params an array of params to initialize with.
     */
    public NumericParameterArray(Parameter[] params) {
        super(params);
    }

    /**
     * Constructor if all the parameters are DoubleParameters
     *
     * @param vals    the values for each parameter.
     * @param minVals the minimum value allowed for each parameter respectively.
     * @param maxVals the maximum value allowed for each parameter respectively.
     * @param names   the display name for each parameter in the array.
     */
    public NumericParameterArray(double[] vals, double[] minVals, double[] maxVals, String names[]) {
        int len = vals.length;
        params_ = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            params_.add(new DoubleParameter(vals[i], minVals[i], maxVals[i], names[i]));
        }
    }

    /**
     * Use this constructor if you have mixed types of parameters.
     *
     * @param params params in array
     */
    public NumericParameterArray(List<Parameter> params) {
        super(params);
    }

    /**
     * @return a copy of ourselves.
     */
    @Override
    public NumericParameterArray copy() {

        NumericParameterArray pa = (NumericParameterArray) super.copy();
        pa.setNumSteps(numSteps_);
        return pa;
    }

    @Override
    protected NumericParameterArray createInstance() {
        return new NumericParameterArray();
    }

    /**
     * Globally sample the parameter space with a uniform distribution.
     *
     * @param requestedNumSamples approximate number of samples to retrieve.
     *                            If the problem space is small and requestedNumSamples is large, it may not be possible to return this
     *                            many unique samples.
     * @return some number of unique samples.
     */
    public Iterator<NumericParameterArray> findGlobalSamples(long requestedNumSamples) {
        return new NumericGlobalSampler(this, requestedNumSamples);
    }

    /**
     * {@inheritDoc}
     */
    public Improvement findIncrementalImprovement(Optimizee optimizee, double jumpSize,
                                                  Improvement lastImprovement, Set<ParameterArray> cache) {
        NumericParameterArray currentParams = this;
        double oldFitness = currentParams.getFitness();
        Vector oldGradient = null;
        if (lastImprovement != null) {
            oldFitness = lastImprovement.getParams().getFitness();
            oldGradient = lastImprovement.getGradient();
        }

        ImprovementIteration iter = new ImprovementIteration(this, oldGradient);

        double sumOfSqs = 0;

        for (int i = 0; i < size(); i++) {
            ParameterArray testParams = this.copy();
            sumOfSqs = iter.incSumOfSqs(i, sumOfSqs, optimizee, currentParams, testParams);
        }
        double gradLength = Math.sqrt(sumOfSqs);

        ImprovementStep step =
                new ImprovementStep(optimizee, iter, gradLength, cache, jumpSize, oldFitness);
        currentParams = step.findNextParams(currentParams);

        double newJumpSize = step.getJumpSize();
        // the improvement may be zero or negative, meaning it did not improve.
        double improvement = step.getImprovement();

        double dotProduct = iter.getGradient().normalizedDot(iter.getOldGradient());
        //System.out.println("dot between " + iter.getGradient() + " and " + iter.getOldGradient()+ " is "+ dotProduct);
        newJumpSize = findNewJumpSize(newJumpSize, dotProduct);

        iter.getGradient().copyFrom(iter.getOldGradient());

        return new Improvement(currentParams, improvement, newJumpSize, iter.getGradient());
    }

    /**
     * If we are headed in pretty much the same direction as last time, then we increase the jumpSize.
     * If we are headed off in a completely new direction, reduce the jumpSize until we start to stabilize.
     *
     * @param jumpSize   the current amount that is stepped in the assumed solution direction.
     * @param dotProduct determines the angle between the new gradient and the old.
     * @return the new jump size - which is usually the same as the old one.
     */
    private double findNewJumpSize(double jumpSize, double dotProduct) {
        double newJumpSize = jumpSize;
        if (dotProduct > MAX_DOT_PRODUCT) {
            newJumpSize *= ImprovementStep.JUMP_SIZE_INC_FACTOR;
        } else if (dotProduct < MIN_DOT_PRODUCT) {
            newJumpSize *= ImprovementStep.JUMP_SIZE_DEC_FACTOR;
        }
        //System.out.println( "dotProduct = " + dotProduct + " new jumpsize = " + jumpSize );
        return newJumpSize;
    }

    /**
     * @return the distance between this parameter array and another.
     * sqrt(sum of squares)
     */
    public double distance(ParameterArray pa) {
        assert (size() == pa.size());
        double sumOfSq = 0.0;
        for (int k = 0; k < size(); k++) {
            double dif = pa.get(k).getValue() - get(k).getValue();
            sumOfSq += dif * dif;
        }
        return Math.sqrt(sumOfSq);
    }

    /**
     * add a vector of deltas to the parameters.
     *
     * @param vec must be the same size as the parameter list.
     */
    public void add(Vector vec) {
        assert (vec.size() == size()) : "Parameter vec has magnitude " + vec.size() + ", expecting " + size();
        for (int i = 0; i < size(); i++) {
            Parameter param = get(i);
            param.setValue(param.getValue() + vec.get(i));
            if (param.getValue() > param.getMaxValue()) {
                System.out.println("Warning param " + param.getName() +
                        " is exceeding is maximum value. It is being pegged to that maximum of " + param.getMaxValue());
                param.setValue(param.getMaxValue());
            }
            if (param.getValue() < param.getMinValue()) {
                System.out.println("Warning param " + param.getName() +
                        " is exceeding is minimum value. It is being pegged to that minimum of " + param.getMinValue());
                param.setValue(param.getMinValue());
            }
        }
    }

    /**
     * @param radius the size of the (1 std deviation) gaussian neighborhood to select a random nbr from
     *               (relative to each parameter range).
     * @return the random nbr.
     */
    public NumericParameterArray getRandomNeighbor(double radius) {
        NumericParameterArray nbr = this.copy();
        for (int k = 0; k < size(); k++) {
            Parameter param = nbr.get(k);
            param.tweakValue(radius, MathUtil.RANDOM);
        }

        return nbr;
    }

    /**
     * @return get a completely random solution in the parameter space.
     */
    public NumericParameterArray getRandomSample() {
        NumericParameterArray nbr = this.copy();
        for (int k = 0; k < size(); k++) {
            Parameter newPar = nbr.get(k);
            newPar.setValue(newPar.getMinValue() + MathUtil.RANDOM.nextDouble() * newPar.getRange());
            assert (newPar.getValue() < newPar.getMaxValue() && newPar.getValue() > newPar.getMinValue()) :
                    "newPar " + newPar.getValue() + " not between " + newPar.getMinValue()
                            + " and  " + newPar.getMaxValue();
        }
        return nbr;
    }

    /**
     * @return a new double array the same magnitude as the parameter list
     */
    public Vector asVector() {
        return new Vector(this.size());
    }

    public void setNumSteps(int numSteps) {
        numSteps_ = numSteps;
    }

    public int getNumSteps() {
        return numSteps_;
    }
}
