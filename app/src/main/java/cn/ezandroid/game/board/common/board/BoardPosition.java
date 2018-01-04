/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.board;

import cn.ezandroid.game.board.common.geometry.ByteLocation;
import cn.ezandroid.game.board.common.geometry.Location;

/**
 * 棋盘位置点模型
 *
 * @author Barry Becker
 */
public class BoardPosition {

    // 当前位置
    protected Location mLocation;

    // 当前位置棋子，可以为空
    protected GamePiece mPiece;

    public BoardPosition(int row, int col, GamePiece piece) {
        this(new ByteLocation(row, col), piece);
    }

    public BoardPosition(BoardPosition p) {
        mLocation = new ByteLocation(p.getRow(), p.getCol());
        mPiece = (p.mPiece != null) ? p.mPiece.copy() : null;
    }

    protected BoardPosition(Location loc, GamePiece piece) {
        mLocation = loc;
        mPiece = piece;
    }

    public BoardPosition copy() {
        return new BoardPosition(this);
    }

    @Override
    public boolean equals(Object pos) {
        if ((pos == null) || !(pos.getClass().equals(this.getClass()))) {
            return false;
        }

        BoardPosition comparisonPos = (BoardPosition) pos;
        boolean sameSide;
        if (getPiece() != null && comparisonPos.getPiece() != null) {
            sameSide = (getPiece().isOwnedByPlayer1() == comparisonPos.getPiece().isOwnedByPlayer1());
        } else {
            sameSide = (getPiece() == null && comparisonPos.getPiece() == null);
        }
        return (getRow() == comparisonPos.getRow()) &&
                (getCol() == comparisonPos.getCol()) && (sameSide);
    }

    @Override
    public int hashCode() {
        return getRow() * 300 + getCol();
    }

    /**
     * 获取当前位置棋子
     *
     * @return
     */
    public GamePiece getPiece() {
        return mPiece;
    }

    public void setPiece(GamePiece piece) {
        mPiece = piece;
    }

    /**
     * 当前位置是否未被占用
     *
     * @return
     */
    public final boolean isUnoccupied() {
        return (mPiece == null);
    }

    /**
     * 当前位置是否已被占用
     */
    public final boolean isOccupied() {
        return (mPiece != null);
    }

    public final int getRow() {
        return mLocation.getRow();
    }

    public final int getCol() {
        return mLocation.getCol();
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public final Location getLocation() {
        return mLocation;
    }

    public final void setLocation(Location loc) {
        mLocation = loc;
    }

    /**
     * 获取当前位置到指定位置的欧几里德距离
     *
     * @param position
     * @return
     */
    public final double getDistanceFrom(BoardPosition position) {
        return mLocation.getDistanceFrom(position.getLocation());
    }

    /**
     * 是否当前位置与指定位置相邻
     *
     * @param position
     * @return
     */
    public final boolean isNeighbor(BoardPosition position) {
        return getDistanceFrom(position) == 1.0;
    }

    /**
     * 清除当前位置棋子
     */
    public void clear() {
        setPiece(null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mPiece != null) {
            sb.append(mPiece.toString());
        }
        sb.append(mLocation.toString());
        return sb.toString();
    }
}

