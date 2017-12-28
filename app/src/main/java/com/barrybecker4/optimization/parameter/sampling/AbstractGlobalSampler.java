// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization.parameter.sampling;

import java.util.Iterator;

/**
 * Finds a set of uniformly distributed global samples in a large numeric parameter space.
 * If the number of samples requested is really large, then all possible values will be returned (if possible).
 *
 * @author Barry Becker
 */
public abstract class AbstractGlobalSampler<E> implements Iterator<E> {

    /** becomes false when no more samples to iterate through */
    protected boolean hasNext = true;

    /** counts up to the number of samples as we iterate */
    protected long counter = 0;

    /**
     * Approximate number of samples to retrieve.
     * If the problem space is small and requestedNumSamples is large, it may not be possible to return this
     * many unique samples.
     */
    protected long numSamples;

    /**
     * Globally sample the parameter space.
     *
     * @return the next sample.
     */
    public abstract E next();

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("cannot remove global samples from the iterator");
    }
}
