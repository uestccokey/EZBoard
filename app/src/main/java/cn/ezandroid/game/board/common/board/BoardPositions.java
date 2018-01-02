/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.board;

import cn.ezandroid.game.common.geometry.ByteLocation;
import cn.ezandroid.game.common.geometry.Location;

/**
 * 棋盘位置点二维数组
 * <p>
 * 合法的位置为 [1, mNumRows],[1, mNumCols]
 *
 * @author Barry Becker
 */
public class BoardPositions {

    protected BoardPosition mPositions[][];

    private int mNumRows;
    private int mNumCols;

    public BoardPositions(int numRows, int numCols) {
        setSize(numRows, numCols);
    }

    protected BoardPositions(BoardPositions b) {
        this(b.getNumRows(), b.getNumCols());

        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                mPositions[i][j] = b.getPosition(i, j).copy();
            }
        }
    }

    public BoardPositions copy() {
        return new BoardPositions(this);
    }

    public void setSize(int numRows, int numCols) {
        mNumRows = numRows;
        mNumCols = numCols;
        mPositions = new BoardPosition[getNumRows() + 1][getNumCols() + 1];
    }

    public void clear(BoardPosition proto) {
        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                proto.setLocation(new ByteLocation(i, j));
                mPositions[i][j] = proto.copy();
            }
        }
    }

    /**
     * 获取棋盘行数
     *
     * @return
     */
    public final int getNumRows() {
        return mNumRows;
    }

    /**
     * 获取棋盘列数
     *
     * @return
     */
    public final int getNumCols() {
        return mNumCols;
    }

    public final BoardPosition getPosition(int row, int col) {
        if (row < 1 || row > mNumRows || col < 1 || col > mNumCols) {
            return null;
        }
        return mPositions[row][col];
    }

    public final BoardPosition getPosition(Location loc) {
        return getPosition(loc.getRow(), loc.getCol());
    }

    public void setPosition(BoardPosition pos) {
        mPositions[pos.getRow()][pos.getCol()] = pos;
    }

    @Override
    public boolean equals(Object b) {
        if (!(b instanceof BoardPositions)) return false;
        BoardPositions board = (BoardPositions) b;
        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                BoardPosition p1 = this.getPosition(i, j);
                BoardPosition p2 = board.getPosition(i, j);
                assert p1 != null;
                assert p2 != null;
                if (p1.isOccupied() != p2.isOccupied()) {
                    return false;
                }
                if (p1.isOccupied()) {
                    GamePiece piece1 = p1.getPiece();
                    GamePiece piece2 = p2.getPiece();
                    if (piece1.isOwnedByPlayer1() != piece2.isOwnedByPlayer1() ||
                            piece1.getType() != piece2.getType()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        int nRows = getNumRows();
        int nCols = getNumCols();
        for (int i = 1; i <= nRows; i++) {
            int pos = (i - 1) * nCols;
            for (int j = 1; j <= nCols; j++) {
                BoardPosition p1 = this.getPosition(i, j);
                assert p1 != null;
                if (p1.isOccupied()) {
                    hash += 2 * (pos + j) + (p1.getPiece().isOwnedByPlayer1() ? 1 : 2);
                }
            }
        }
        return hash;
    }

    /**
     * 是否指定位置在棋盘范围内
     *
     * @param r
     * @param c
     * @return
     */
    public final boolean inBounds(int r, int c) {
        return !(r < 1 || r > getNumRows() || c < 1 || c > getNumCols());
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(1000);
        bldr.append("\n");
        int nRows = getNumRows();
        int nCols = getNumCols();
        for (int i = 1; i <= nRows; i++) {
            for (int j = 1; j <= nCols; j++) {
                BoardPosition pos = this.getPosition(i, j);
                if (pos.isOccupied()) {
                    bldr.append(pos.getPiece());
                } else {
                    bldr.append(" _ ");
                }
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }

    /**
     * 检查是否在4个角落
     *
     * @param position
     * @return
     */
    public boolean isInCorner(BoardPosition position) {
        return ((position.getRow() == 1 && position.getCol() == 1) ||
                (position.getRow() == getNumRows() && position.getCol() == getNumCols()) ||
                (position.getRow() == getNumRows() && position.getCol() == 1) ||
                (position.getRow() == 1 && position.getCol() == getNumCols()));
    }

    /**
     * 检查是否在边线上（角落的点也在边线上）
     *
     * @param position
     * @return
     */
    public boolean isOnEdge(BoardPosition position) {
        return (position.getRow() == 1 || position.getRow() == getNumRows()
                || position.getCol() == 1 || position.getCol() == getNumCols());
    }
}
