/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.common.geometry;

import java.io.Serializable;

/**
 * Represents a location location of something in byte coordinates.
 * The range of bytes are only -127 to 127.
 * Immutable. Use MutableIntLocation if you really need to modify it (rare).
 *
 * @author Barry Becker
 */
public abstract class Location implements Serializable {

    public abstract int getRow();

    public abstract int getCol();

    public abstract int getX();

    public abstract int getY();

    public abstract Location copy();

    /**
     * Checks to see if the given location has the same coordinates as this one.
     *
     * @param location The location whose coordinates are to be compared.
     * @return true  The location's coordinates exactly equal this location's.
     */
    @Override
    public boolean equals(Object location) {
        if (!(location instanceof Location)) return false;
        Location loc = (Location) location;
        return (loc.getRow() == getRow()) && (loc.getCol() == getCol());
    }

    /**
     * If override equals, should also override hashCode
     */
    public int hashCode() {
        return (100 * getRow() + getCol());
    }

    /**
     * @param loc another location to measure distance from.
     * @return the euclidean distance from this location to another.
     */
    public double getDistanceFrom(Location loc) {
        float xDif = Math.abs(getCol() - loc.getCol());
        float yDif = Math.abs(getRow() - loc.getRow());
        return Math.sqrt(xDif * xDif + yDif * yDif);
    }

    /**
     * @return an immutable copy of the original incremented by the amount specified.
     */
    public abstract Location incrementOnCopy(int rowChange, int colChange);

    /**
     * @return an immutable copy of the original incremented by the amount specified.
     */
    public Location incrementOnCopy(Location loc) {
        return incrementOnCopy(loc.getRow(), loc.getCol());
    }

    /**
     * @return an immutable copy of the original incremented by the amount specified.
     */
    public Location decrementOnCopy(Location loc) {
        return incrementOnCopy(-loc.getRow(), -loc.getCol());
    }

    /**
     * @return the string form
     */
    public String toString() {
        return "(row=" + getRow() + ", column=" + getCol() + ")"; //NON-NLS
    }
}

