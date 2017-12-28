// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.geometry;

/**
 * Represents a location location of something in byte coordinates.
 * The range of bytes are only -127 to 127.
 * <p>
 * Immutable. Use MutableIntLocation if you really need to modify it (rare).
 *
 * @author Barry Becker
 */
public class ByteLocation extends Location {

    private static final long serialVersionUID = 1;
    private byte row_ = 0;
    private byte col_ = 0;

    /**
     * Constructs a new Location at the given coordinates.
     *
     * @param row the row coordinate (0 - 255).
     * @param col the column coordinate (0 - 255).
     */
    public ByteLocation(int row, int col) {
        assert Math.abs(row) < 128 && Math.abs(col) < 128 : "row=" + row + " or col=" + col + " was out of range.";
        row_ = (byte) row;
        col_ = (byte) col;
    }

    @Override
    public int getRow() {
        return row_;
    }

    @Override
    public int getCol() {
        return col_;
    }

    @Override
    public int getX() {
        return col_;
    }

    @Override
    public int getY() {
        return row_;
    }

    @Override
    public Location copy() {
        return new ByteLocation(row_, col_);
    }

    @Override
    public Location incrementOnCopy(int rowChange, int colChange) {
        return new ByteLocation(row_ + rowChange, col_ + colChange);
    }
}

