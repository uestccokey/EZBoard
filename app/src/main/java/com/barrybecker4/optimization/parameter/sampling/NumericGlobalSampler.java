// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.sampling;

import com.barrybecker4.common.math.MultiDimensionalIndexer;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.types.Parameter;

import java.util.NoSuchElementException;

/**
 * Finds a set of uniformly distributed global samples in a large numeric parameter space.
 *
 * @author Barry Becker
 */
public class NumericGlobalSampler extends AbstractGlobalSampler<NumericParameterArray> {

    private NumericParameterArray params;

    /** number of discrete samples to take along each parameter */
    private int samplingRate;

    private MultiDimensionalIndexer samples;


    /**
     * Constructor
     *
     * @param params an array of params to initialize with.
     */
    public NumericGlobalSampler(NumericParameterArray params, long requestedNumSamples) {
        this.params = params;

        int[] dims = new int[params.size()];
        samplingRate = determineSamplingRate(requestedNumSamples);
        for (int i = 0; i < dims.length; i++) {
            dims[i] = samplingRate;
        }
        // this potentially takes a lot of memory - may need to revisit
        samples = new MultiDimensionalIndexer(dims);

        numSamples = samples.getNumValues();
    }

    @Override
    public NumericParameterArray next() {
        if (counter >= numSamples) {
            throw new NoSuchElementException("ran out of samples.");
        }
        if (counter == numSamples - 1) {
            hasNext = false;
        }

        int[] index = samples.getIndexFromRaw((int) counter);
        NumericParameterArray nextSample = params.copy();

        for (int j = 0; j < nextSample.size(); j++) {
            Parameter p = nextSample.get(j);
            double increment = (p.getMaxValue() - p.getMinValue()) / samplingRate;
            p.setValue(p.getMinValue() + increment / 2.0 + index[j] * increment);
        }
        counter++;
        return nextSample;
    }


    private int determineSamplingRate(long requestedNumSamples) {
        int numDims = params.size();
        return (int) Math.pow((double) requestedNumSamples, 1.0 / numDims);
    }

}
