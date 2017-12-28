/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;

/**
 * Represents the array of positions on the board.
 * Assumes an M*N grid.
 * Legal positions are [1, numRows_][1, numCols_]
 *
 * @author Barry Becker
 */
public class BoardPositions {

    /** the internal data structures representing the positions on the game board. */
    protected BoardPosition positions_[][];

    private int numRows_;
    private int numCols_;
    private int rowsTimesCols_;

    /**
     * Constructor
     */
    public BoardPositions(int numRows, int numCols) {
        setSize(numRows, numCols);
    }

    /**
     * Copy constructor.
     * Makes a deep copy of the board and all its parts.
     */
    protected BoardPositions(BoardPositions b) {
        this(b.getNumRows(), b.getNumCols());

        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                positions_[i][j] = b.getPosition(i, j).copy();
            }
        }
    }

    public BoardPositions copy() {
        return new BoardPositions(this);
    }

    /**
     * Change the dimensions of this game board.
     * Note that there are sentinels around the border
     */
    public void setSize(int numRows, int numCols) {
        numRows_ = numRows;
        numCols_ = numCols;
        rowsTimesCols_ = numRows_ * numCols_;
        positions_ = new BoardPosition[getNumRows() + 1][getNumCols() + 1];
    }

    public void clear(BoardPosition proto) {
        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                proto.setLocation(new ByteLocation(i, j));
                positions_[i][j] = proto.copy();
            }
        }
    }

    /**
     * @return retrieve the number of rows that the board has.
     */
    public final int getNumRows() {
        return numRows_;
    }

    /**
     * @return retrieve the number of cols that the board has.
     */
    public final int getNumCols() {
        return numCols_;
    }

    public final int getNumBoardSpaces() {
        return rowsTimesCols_;
    }

    /**
     * returns null if there is no game piece at the position specified.
     *
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final BoardPosition getPosition(int row, int col) {
        if (row < 1 || row > numRows_ || col < 1 || col > numCols_) {
            return null;
        }
        return positions_[row][col];
    }

    /**
     * returns null if there is no game piece at the position specified.
     *
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final BoardPosition getPosition(Location loc) {
        return getPosition(loc.getRow(), loc.getCol());
    }

    public void setPosition(BoardPosition pos) {
        positions_[pos.getRow()][pos.getCol()] = pos;
    }

    /**
     * Two boards are considered equal if all the pieces are in the same spot and have like ownership.
     *
     * @param b the board to compare to.
     * @return true if all the pieces in board b in the same spot and have like ownership as this.
     */
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
                    GameContext.log(2, "Inconsistent occupation status  p1=" + p1 + " and p2=" + p2);
                    return false;
                }
                if (p1.isOccupied()) {
                    GamePiece piece1 = p1.getPiece();
                    GamePiece piece2 = p2.getPiece();
                    if (piece1.isOwnedByPlayer1() != piece2.isOwnedByPlayer1() ||
                            piece1.getType() != piece2.getType()) {
                        GameContext.log(2, "There was an inconsistency between p1=" + p1 + " and " + p2);
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
     * @return true if the specified position is within the bounds of the board
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
     * Check the 4 corners
     *
     * @param position position to see if in corner of board.
     * @return true if the specified BoardPosition is on the corder of the board
     */
    public boolean isInCorner(BoardPosition position) {
        return ((position.getRow() == 1 && position.getCol() == 1) ||
                (position.getRow() == getNumRows() && position.getCol() == getNumCols()) ||
                (position.getRow() == getNumRows() && position.getCol() == 1) ||
                (position.getRow() == 1 && position.getCol() == getNumCols()));
    }

    /**
     * Corner points are also on the edge.
     *
     * @param position position to see if on edge of board.
     * @return true if the specified BoardPosition is on the edge of the board
     */
    public boolean isOnEdge(BoardPosition position) {
        return (position.getRow() == 1 || position.getRow() == getNumRows()
                || position.getCol() == 1 || position.getCol() == getNumCols());
    }
}
