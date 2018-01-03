// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.common.geometry;

/**
 * 整数坐标点
 *
 * @author Barry Becker
 */
public class IntLocation extends Location {

    private static final long serialVersionUID = 1;
    protected int mRow = 0;
    protected int mCol = 0;

    public IntLocation() {
    }

    public IntLocation(Location loc) {
        mRow = loc.getRow();
        mCol = loc.getCol();
    }

    public IntLocation(int row, int col) {
        mRow = row;
        mCol = col;
    }

    @Override
    public int getRow() {
        return mRow;
    }

    @Override
    public int getCol() {
        return mCol;
    }

    @Override
    public Location copy() {
        return new IntLocation(mRow, mCol);
    }
}