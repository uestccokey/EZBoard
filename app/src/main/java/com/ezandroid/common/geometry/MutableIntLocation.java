/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.common.geometry;

/**
 * Represents a location of something in coordinates.
 * Use this version if you really need it to be mutable
 * This version <i>is</i> mutable.
 *
 * @author Barry Becker
 */
public final class MutableIntLocation extends IntLocation {

    /**
     * Constructs a new Location at the given coordinates.
     *
     * @param row the row  coordinate (0 - 255).
     * @param col the column coordinate (0 - 255).
     */
    public MutableIntLocation(int row, int col) {
        super(row, col);
    }

    public MutableIntLocation(Location loc) {
        super(loc.getRow(), loc.getCol());
    }

    public void setRow(int row) {
        this.row_ = (byte) row;
    }

    public void setCol(int col) {
        this.col_ = (byte) col;
    }

    public void incrementRow(int rowChange) {
        row_ += rowChange;
    }

    public void incrementCol(int colChange) {
        col_ += colChange;
    }

    public void increment(int rowChange, int colChange) {
        incrementRow(rowChange);
        incrementCol(colChange);
    }
}

