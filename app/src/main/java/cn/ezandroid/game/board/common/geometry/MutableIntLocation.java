/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.geometry;

/**
 * 可变整数坐标点
 *
 * @author Barry Becker
 */
public final class MutableIntLocation extends IntLocation {

    public MutableIntLocation(int row, int col) {
        super(row, col);
    }

    public MutableIntLocation(Location loc) {
        super(loc.getRow(), loc.getCol());
    }

    public void setRow(int row) {
        this.mRow = (byte) row;
    }

    public void setCol(int col) {
        this.mCol = (byte) col;
    }

    public void incrementRow(int rowChange) {
        mRow += rowChange;
    }

    public void incrementCol(int colChange) {
        mCol += colChange;
    }

    public void increment(int rowChange, int colChange) {
        incrementRow(rowChange);
        incrementCol(colChange);
    }
}

