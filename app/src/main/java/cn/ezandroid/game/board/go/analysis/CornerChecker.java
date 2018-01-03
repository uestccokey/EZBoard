/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis;

import cn.ezandroid.game.board.common.board.BoardPosition;

/**
 * 角点位置检查器
 *
 * @author Barry Becker
 */
public final class CornerChecker {

    private int mNumRows;
    private int mNumCols;

    public CornerChecker(int numRows, int numCols) {
        this.mNumRows = numRows;
        this.mNumCols = numCols;
    }

    /**
     * 是否角落3角点
     * <p>
     * 如下图所示，x点为角落3角点
     * |  |  |
     * ---------
     * |  |  |
     * x--------
     * |  |  |
     * x--x-----
     *
     * @param position
     * @return
     */
    public boolean isCornerTriple(BoardPosition position) {
        return (isULCornerTriple(position) || isURCornerTriple(position)
                || isLLCornerTriple(position) || isLRCornerTriple(position));
    }

    private boolean isULCornerTriple(BoardPosition position) {
        return ((position.getRow() == 1 && position.getCol() == 1) ||
                (position.getRow() == 2 && position.getCol() == 1) ||
                (position.getRow() == 1 && position.getCol() == 2));
    }

    private boolean isURCornerTriple(BoardPosition position) {
        return ((position.getRow() == 1 && position.getCol() == mNumCols) ||
                (position.getRow() == 2 && position.getCol() == mNumCols) ||
                (position.getRow() == 1 && position.getCol() == mNumCols - 1));
    }

    private boolean isLLCornerTriple(BoardPosition position) {
        return ((position.getRow() == mNumRows && position.getCol() == 1) ||
                (position.getRow() == mNumRows && position.getCol() == 2) ||
                (position.getRow() == mNumRows - 1 && position.getCol() == 1));
    }

    private boolean isLRCornerTriple(BoardPosition position) {
        return ((position.getRow() == mNumRows && position.getCol() == mNumCols) ||
                (position.getRow() == mNumRows - 1 && position.getCol() == mNumCols) ||
                (position.getRow() == mNumRows && position.getCol() == mNumCols - 1));
    }
}