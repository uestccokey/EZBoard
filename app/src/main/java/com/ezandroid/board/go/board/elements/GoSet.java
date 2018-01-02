/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.elements;

import java.util.Set;

/**
 * A GoSet is an abstract class representing a set of go entities
 * (stones, strings, groups, or armies)
 *
 * @author Barry Becker
 */
public abstract class GoSet implements IGoSet {

    /** true if this set of stones is owned by player one (black) */
    protected boolean ownedByPlayer1_;

    /**
     * constructor.
     */
    protected GoSet() {
        initializeMembers();
    }

    /**
     * @return true if set is owned by player one
     */
    @Override
    public final boolean isOwnedByPlayer1() {
        return ownedByPlayer1_;
    }

    /**
     * @return the number of stones in the set
     */
    @Override
    public final int size() {
        return getMembers().size();
    }

    /**
     * @return the hashSet containing the members
     */
    @Override
    public abstract Set<? extends IGoMember> getMembers();

    /**
     * Some initialization of the set members.
     */
    protected abstract void initializeMembers();
}
