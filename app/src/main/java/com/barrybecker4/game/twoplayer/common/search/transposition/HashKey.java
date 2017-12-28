/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.common.geometry.Location;

/**
 * As an experiment to see the sequence of moves that led to a certain hash key,
 * include the move history in the key itself. Should never do this in practice.
 *
 * @author Barry Becker
 */
public class HashKey {

    private volatile Long key;

    /**
     * Create the static table of random numbers to use for the Hash from a sample board.
     */
    public HashKey() {
        key = 0L;
    }

    /**
     * Constructor used for tests where we want to create a HashKey with a specific value.
     * ^ is exclusive or (XOR)
     *
     * @param key key value.
     */
    public HashKey(Long key) {
        this.key = key;
    }

    /**
     * copy constructor
     */
    public HashKey(HashKey key) {
        this(key.getKey());
    }

    public HashKey copy() {
        return new HashKey(this);
    }

    public void applyMove(Location move, long specialNumber) {
        // note ^ is XOR (exclusive OR) in java.
        key ^= specialNumber;
    }

    public boolean matches(Long key) {
        return this.key.equals(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashKey)) return false;

        HashKey hashKey = (HashKey) o;

        return !(key != null ? !key.equals(hashKey.key) : hashKey.key != null);
    }

    protected Long getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public String toString() {
        return Long.toBinaryString(key);
    }
}
