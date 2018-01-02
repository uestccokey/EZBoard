/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.common.geometry;

/**
 * A box defined by 2 locations.
 * The coordinates have the resolution of integers.
 *
 * @author Barry Becker
 */
public class Box {

    private IntLocation topLeftCorner;
    private IntLocation bottomRightCorner;

    /**
     * Constructor
     * Two points that define the box.
     *
     * @param pt0 one corner of the box
     * @param pt1 the opposite corner of the box.
     */
    public Box(Location pt0, Location pt1) {
        this(Math.min(pt0.getRow(), pt1.getRow()), Math.min(pt0.getCol(), pt1.getCol()),
                Math.max(pt0.getRow(), pt1.getRow()), Math.max(pt0.getCol(), pt1.getCol()));
    }

    /** Degenerate box consisting of a point in space */
    public Box(Location pt0) {
        this(pt0, pt0);
    }

    /**
     * Constructor which takes the coordinates of any two diagonal corners.
     */
    public Box(int rowMin, int colMin, int rowMax, int colMax) {

        if (rowMin > rowMax) {
            int temp = rowMin;
            rowMin = rowMax;
            rowMax = temp;
        }
        if (colMin > colMax) {
            int temp = colMin;
            colMin = colMax;
            colMax = temp;
        }

        topLeftCorner = new IntLocation(rowMin, colMin);
        bottomRightCorner = new IntLocation(rowMax, colMax);
    }

    /**
     * Constructs a box with dimensions of oldBox, but expanded by the specified point
     *
     * @param oldBox box to base initial dimensions on.
     * @param point  point to expand new box by.
     */
    public Box(Box oldBox, Location point) {
        this(oldBox.getTopLeftCorner(), oldBox.getBottomRightCorner());
        expandBy(point);
    }

    /** @return the width of the box */
    public int getWidth() {
        return Math.abs(bottomRightCorner.getCol() - topLeftCorner.getCol());
    }

    /** @return the height of the box */
    public int getHeight() {
        return Math.abs(bottomRightCorner.getRow() - topLeftCorner.getRow());
    }

    public int getMaxDimension() {
        return Math.max(getWidth(), getHeight());
    }

    public IntLocation getTopLeftCorner() {
        return topLeftCorner;
    }

    public IntLocation getBottomRightCorner() {
        return bottomRightCorner;
    }

    public int getMinRow() {
        return topLeftCorner.getRow();
    }

    public int getMinCol() {
        return topLeftCorner.getCol();
    }

    public int getMaxRow() {
        return bottomRightCorner.getRow();
    }

    public int getMaxCol() {
        return bottomRightCorner.getCol();
    }

    public int getArea() {
        return getWidth() * getHeight();
    }

    /**
     * @param pt point to check for containment in the box.
     * @return true if the box contains the specified point
     */
    public boolean contains(Location pt) {
        int row = pt.getRow();
        int col = pt.getCol();
        return (row >= getMinRow() && row <= getMaxRow() && col >= getMinCol() && col <= getMaxCol());
    }

    /**
     * Note that the corner locations are immutable so we create new objects for them if they change.
     *
     * @param loc location to expand out box by.
     */
    public void expandBy(Location loc) {
        expandBy(new IntLocation(loc));
    }

    /**
     * Note that the corner locations are immutable so we create new objects for them if they change.
     *
     * @param loc location to expand out box by.
     */
    public void expandBy(IntLocation loc) {
        if (loc.getRow() < topLeftCorner.getRow()) {
            topLeftCorner = new IntLocation(loc.getRow(), topLeftCorner.getCol());
        } else if (loc.getRow() > bottomRightCorner.getRow()) {
            bottomRightCorner = new IntLocation(loc.getRow(), bottomRightCorner.getCol());
        }
        if (loc.getCol() < topLeftCorner.getCol()) {
            topLeftCorner = new IntLocation(topLeftCorner.getRow(), loc.getCol());
        } else if (loc.getCol() > bottomRightCorner.getCol()) {
            bottomRightCorner = new IntLocation(bottomRightCorner.getRow(), loc.getCol());
        }
    }

    /**
     * @param location the location to check if on border.
     * @return true if location is on this box's border
     */
    public boolean isOnEdge(Location location) {
        return (location.getRow() == bottomRightCorner.getRow()
                || location.getRow() == topLeftCorner.getRow()
                || location.getCol() == bottomRightCorner.getCol()
                || location.getCol() == topLeftCorner.getCol());
    }

    /**
     * @param location the location to check if on corner.
     * @return true if location is on this box's border
     */
    public boolean isOnCorner(Location location) {
        return location.equals(bottomRightCorner)
                || location.equals(topLeftCorner)
                || ((location.getRow() == bottomRightCorner.getRow()
                && location.getCol() == topLeftCorner.getCol())
                || (location.getRow() == topLeftCorner.getRow()
                && location.getCol() == bottomRightCorner.getCol()));
    }

    /**
     * @param amount amount to expand all borders of the box by.
     * @param maxRow don't go further than this though.
     * @param maxCol don't go further than this though.
     */
    public void expandGloballyBy(int amount, int maxRow, int maxCol) {

        topLeftCorner =
                new IntLocation(Math.max(topLeftCorner.getRow() - amount, 1),
                        Math.max(topLeftCorner.getCol() - amount, 1));

        bottomRightCorner =
                new IntLocation(Math.min(bottomRightCorner.getRow() + amount, maxRow),
                        Math.min(bottomRightCorner.getCol() + amount, maxCol));
    }

    /**
     * @param threshold if withing this distance to the edge, extend the box all the way to that edge.
     * @param maxRow    don't go further than this though.
     * @param maxCol    don't go further than this though.
     */
    public void expandBordersToEdge(int threshold, int maxRow, int maxCol) {
        if (topLeftCorner.getRow() <= threshold + 1) {
            topLeftCorner = new IntLocation(1, topLeftCorner.getCol());
        }
        if (topLeftCorner.getCol() <= threshold + 1) {
            topLeftCorner = new IntLocation(topLeftCorner.getRow(), 1);
        }
        if (maxRow - bottomRightCorner.getRow() <= threshold) {
            bottomRightCorner = new IntLocation(maxRow, bottomRightCorner.getCol());
        }
        if (maxCol - bottomRightCorner.getCol() <= threshold) {
            bottomRightCorner = new IntLocation(bottomRightCorner.getRow(), maxCol);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("Box:"); //NON-NLS
        buf.append(topLeftCorner);
        buf.append(" - ");
        buf.append(bottomRightCorner);
        return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Box box = (Box) o;

        return !(bottomRightCorner != null ? !bottomRightCorner.equals(box.bottomRightCorner) : box.bottomRightCorner != null)
                && !(topLeftCorner != null ? !topLeftCorner.equals(box.topLeftCorner) : box.topLeftCorner != null);
    }

    @Override
    public int hashCode() {
        int result = topLeftCorner != null ? topLeftCorner.hashCode() : 0;
        result = 31 * result + (bottomRightCorner != null ? bottomRightCorner.hashCode() : 0);
        return result;
    }
}
