// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.common.geometry;

/**
 * 字节坐标点
 *
 * @author Barry Becker
 */
public class ByteLocation extends Location {

    private static final long serialVersionUID = 1;
    private byte mRow = 0;
    private byte mCol = 0;

    public ByteLocation(int row, int col) {
        assert Math.abs(row) < 128 && Math.abs(col) < 128 : "row=" + row + " or col=" + col + " was out of range.";
        mRow = (byte) row;
        mCol = (byte) col;
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
        return new ByteLocation(mRow, mCol);
    }
}

