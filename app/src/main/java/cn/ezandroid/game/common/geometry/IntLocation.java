// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.common.geometry;

/**
 * Represents a location location of something in integer coordinates.
 * Immutable. Use MutableIntLocation if you really need to modify it (rare).
 *
 * @author Barry Becker
 */
public class IntLocation extends Location {

    private static final long serialVersionUID = 1;
    protected int row_ = 0;
    protected int col_ = 0;

    /**
     * Constructs a new point at (0, 0).
     * Default empty constructor
     */
    public IntLocation() {
    }

    public IntLocation(Location loc) {
        row_ = loc.getRow();
        col_ = loc.getCol();
    }

    /**
     * Constructs a new Location at the given coordinates.
     *
     * @param row the row coordinate.
     * @param col the column coordinate.
     */
    public IntLocation(int row, int col) {
        row_ = row;
        col_ = col;
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
        return new IntLocation(row_, col_);
    }

    @Override
    public Location incrementOnCopy(int rowChange, int colChange) {
        return new IntLocation(row_ + rowChange, col_ + colChange);
    }
}