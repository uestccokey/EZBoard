/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.analysis;

import com.ezandroid.board.common.board.BoardPosition;

/**
 * Used to check to see if a position is in the corner of the board.
 *
 * @author Barry Becker
 */
public final class CornerChecker {

    private int numRows;
    private int numCols;

    /**
     * Constructor.
     */
    public CornerChecker(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    /**
     * Corner triples are the 3 points closest to a corner
     *
     * @param position position to see if in corner of board.
     * @return true if the specified BoardPosition is on the corner of the board
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

        return ((position.getRow() == 1 && position.getCol() == numCols) ||
                (position.getRow() == 2 && position.getCol() == numCols) ||
                (position.getRow() == 1 && position.getCol() == numCols - 1));
    }

    private boolean isLLCornerTriple(BoardPosition position) {

        return ((position.getRow() == numRows && position.getCol() == 1) ||
                (position.getRow() == numRows && position.getCol() == 2) ||
                (position.getRow() == numRows - 1 && position.getCol() == 1));
    }

    private boolean isLRCornerTriple(BoardPosition position) {
        return ((position.getRow() == numRows && position.getCol() == numCols) ||
                (position.getRow() == numRows - 1 && position.getCol() == numCols) ||
                (position.getRow() == numRows && position.getCol() == numCols - 1));
    }
}