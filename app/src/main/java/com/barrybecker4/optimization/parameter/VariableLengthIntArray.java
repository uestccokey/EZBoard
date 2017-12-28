// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.distancecalculators.DistanceCalculator;
import com.barrybecker4.optimization.parameter.distancecalculators.MagnitudeIgnoredDistanceCalculator;
import com.barrybecker4.optimization.parameter.improvement.DiscreteImprovementFinder;
import com.barrybecker4.optimization.parameter.improvement.Improvement;
import com.barrybecker4.optimization.parameter.sampling.VariableLengthGlobalSampler;
import com.barrybecker4.optimization.parameter.types.IntegerParameter;
import com.barrybecker4.optimization.parameter.types.Parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a 1 dimensional, variable length, array of unique integer parameters.
 * The order of the integers does not matter.
 *
 * @author Barry Becker
 */
public class VariableLengthIntArray extends AbstractParameterArray {

    private List<Integer> fullSet;

    /** Default constructor */
    protected VariableLengthIntArray() {}

    private DistanceCalculator distCalculator;

    /**
     * Constructor
     *
     * @param params  an array of params to initialize with.
     * @param fullSet the full set of all integer parameters.
     */
    public VariableLengthIntArray(List<Parameter> params, List<Integer> fullSet, DistanceCalculator distCalc) {
        super(params);
        this.fullSet = fullSet;
        assert distCalc != null;
        this.distCalculator = distCalc;
    }

    public static VariableLengthIntArray createInstance(List<Parameter> params, List<Integer> fullSet) {
        return createInstance(params, fullSet, new MagnitudeIgnoredDistanceCalculator());
    }

    public static VariableLengthIntArray createInstance(
            List<Parameter> params, List<Integer> fullSet, DistanceCalculator distanceCalculator) {
        return new VariableLengthIntArray(params, fullSet, distanceCalculator);
    }

    /** @return the maximum length of the variable length array */
    public int getMaxLength() {
        return fullSet.size();
    }

    @Override
    protected VariableLengthIntArray createInstance() {
        return new VariableLengthIntArray();
    }

    /**
     * The distance computation will be quite different for this than a regular parameter array.
     * We want the distance to represent a measure of the amount of similarity between two instances.
     * There are two ways in which instance can differ, and the weighting assigned to each may depend on the problem.
     * - the length of the parameter array
     * - the set of values in the parameter array.
     * Generally, the distance is greater the greater the number of parameters that are different.
     *
     * @return the distance between this parameter array and another.
     */
    public double distance(ParameterArray pa) {
        return distCalculator.calculateDistance(this, pa);
    }

    /**
     * Create a new permutation that is not too distant from what we have now.
     * The two ways a configuration of marked nodes can change is
     * - add or remove nodes
     * - change values of nodes
     *
     * @param radius a indication of the amount of variation to use. 0 is none, 2 is a lot.
     *               Change Math.min(1, 10 * radius * N/100) of the entries, where N is the number of params
     * @return the random nbr.
     */
    public VariableLengthIntArray getRandomNeighbor(double radius) {
        if (size() <= 1) return this;

        double probAddRemove = 1.0 / (1.0 + radius);
        boolean add = false;
        boolean remove = false;
        if (MathUtil.RANDOM.nextDouble() > probAddRemove) {
            if ((MathUtil.RANDOM.nextDouble() > 0.5 || size() <= 1) && size() < getMaxLength() - 1) {
                add = true;
            } else {
                remove = true;
            }
        }
        int numNodesToMove;
        VariableLengthIntArray nbr = (VariableLengthIntArray) this.copy();

        if (add || remove) {
            numNodesToMove = MathUtil.RANDOM.nextInt(Math.min(size(), (int) (radius + 1)));
        } else {
            numNodesToMove = 1 + MathUtil.RANDOM.nextInt(1 + (int) radius);
        }

        if (remove) {
            removeRandomParam(nbr);
        }
        if (add) {
            addRandomParam(nbr);
        }
        moveNodes(numNodesToMove, nbr);
        return nbr;
    }

    public void setCombination(List<Integer> indices) {
        assert indices.size() <= getMaxLength() :
                "The number of indices (" + indices.size() + ") was greater than the size (" + size() + ")";
        List<Parameter> newParams = new ArrayList<>(size());
        for (int i : indices) {
            newParams.add(createParam(fullSet.get(i)));
        }
        params_ = newParams;
    }

    /**
     * Globally sample the parameter space.
     *
     * @param requestedNumSamples approximate number of samples to retrieve.
     *                            If the problem space is small and requestedNumSamples is large, it may not be possible to return this
     *                            many unique samples.
     * @return some number of unique samples.
     */
    public Iterator<VariableLengthIntArray> findGlobalSamples(long requestedNumSamples) {
        return new VariableLengthGlobalSampler(this, requestedNumSamples);
    }

    /**
     * {@inheritDoc}
     * Try swapping parameters randomly until we find an improvement (if we can).
     */
    public Improvement findIncrementalImprovement(Optimizee optimizee, double jumpSize,
                                                  Improvement lastImprovement, Set<ParameterArray> cache) {
        DiscreteImprovementFinder finder = new DiscreteImprovementFinder(this);
        return finder.findIncrementalImprovement(optimizee, jumpSize, cache);
    }

    /**
     * @return get a completely random solution in the parameter space.
     */
    public ParameterArray getRandomSample() {
        List<Integer> marked = new LinkedList<>();
        for (int i = 0; i < getMaxLength(); i++) {
            if (MathUtil.RANDOM.nextDouble() > 0.5) {
                marked.add(fullSet.get(i));
            }
        }
        List<Parameter> newParams = new ArrayList<>();
        for (int markedNode : marked) {
            newParams.add(createParam(markedNode));
        }

        return new VariableLengthIntArray(newParams, fullSet, distCalculator);
    }

    /**
     * @return a copy of ourselves.
     */
    public AbstractParameterArray copy() {
        VariableLengthIntArray copy = (VariableLengthIntArray) super.copy();
        copy.fullSet = this.fullSet;
        copy.distCalculator = this.distCalculator;
        return copy;
    }

    /**
     * @param i the integer parameter's value. May be Negative
     * @return a new integer parameter.
     */
    private Parameter createParam(int i) {
        return new IntegerParameter(i, (i < 0) ? i : 0, (i >= 0) ? i : 0, "p" + i);
    }

    private void removeRandomParam(VariableLengthIntArray nbr) {
        int indexToRemove = MathUtil.RANDOM.nextInt(size());
        assert nbr.size() > 0;
        List<Parameter> newParams = new ArrayList<>(nbr.size() - 1);

        for (int i = 0; i < nbr.size(); i++) {
            if (i != indexToRemove) {
                newParams.add(nbr.get(i));
            }
        }
        nbr.params_ = newParams;
    }

    private void addRandomParam(VariableLengthIntArray nbr) {
        List<Integer> freeNodes = getFreeNodes(nbr);
        int newSize = nbr.size() + 1;
        assert newSize <= getMaxLength();
        List<Parameter> newParams = new ArrayList<>(newSize);
        for (Parameter p : nbr.params_) {
            newParams.add(p);
        }
        int value = freeNodes.get(MathUtil.RANDOM.nextInt(freeNodes.size()));
        newParams.add(createParam(value));
        nbr.params_ = newParams;
    }

    /**
     * select num free nodes randomly and and swap them with num randomly selected marked nodes.
     *
     * @param numNodesToMove number of nodes to move to new locations
     * @param nbr            neighbor parameter array
     */
    private void moveNodes(int numNodesToMove, VariableLengthIntArray nbr) {
        List<Integer> freeNodes = getFreeNodes(nbr);
        int numSelect = Math.min(freeNodes.size(), numNodesToMove);
        List<Integer> swapNodes = selectRandomNodes(numSelect, freeNodes);

        for (int i = 0; i < numSelect; i++) {
            int index = MathUtil.RANDOM.nextInt(nbr.size());
            nbr.get(index).setValue(swapNodes.get(i));
        }
    }

    private List<Integer> selectRandomNodes(int numNodesToSelect, List<Integer> freeNodes) {
        List<Integer> selected = new LinkedList<>();
        for (int i = 0; i < numNodesToSelect; i++) {
            int node = freeNodes.get(MathUtil.RANDOM.nextInt(freeNodes.size()));
            selected.add(node);
            freeNodes.remove((Integer) node);
        }
        return selected;
    }

    private List<Integer> getFreeNodes(VariableLengthIntArray nbr) {
        List<Integer> freeNodes = new ArrayList<>(getMaxLength());
        Set<Integer> markedNodes = new HashSet<>();
        for (Parameter p : nbr.params_) {
            markedNodes.add((int) p.getValue());
        }

        for (int i = 0; i < getMaxLength(); i++) {
            if (!markedNodes.contains(fullSet.get(i))) {
                freeNodes.add(fullSet.get(i));
            }
        }
        return freeNodes;
    }
}
