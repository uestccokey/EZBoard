/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.board;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;

/**
 * the Board describes the physical layout of the game.
 * It is an abstract class that provides a common implementation for many of the
 * methods in the IRectangularBoard.
 * Assumes an M*N grid.
 * Legal positions are [1, numRows_][1, numCols_]
 * <p>
 * Games like gomoku, go, chess, checkers, go-moku,
 * shoji, othello, connect4, squares, Stratego, Blockade fit this pattern.
 * Other games like Risk, Galactic Empire, or Monopoly and might be supportable in the future.
 * They are harder because they do not have perfect information (i.e. they use dice).
 * and have multiple players.
 *
 * @author Barry Becker
 */
public abstract class Board<M extends Move> implements IRectangularBoard<M> {

    /** the internal data structures representing the game board and the positions on it. */
    protected BoardPositions positions_;

    /**
     * We keep a list of the moves that have been made.
     * We can navigate forward or backward in time using this
     */
    private MoveList<M> moveList_;

    /**
     * Default constructor
     */
    public Board() {
        moveList_ = new MoveList<>();
    }

    /**
     * Copy constructor.
     * Makes a deep copy of the board and all its parts.
     */
    protected Board(Board<M> b) {
        this();
        this.setSize(b.getNumRows(), b.getNumCols());

        moveList_ = b.moveList_.copy();
        positions_ = b.positions_.copy();
    }

    /**
     * Reset the board to its initial state.
     */
    @Override
    public void reset() {
        getMoveList().clear();
        positions_.clear(getPositionPrototype());
    }

    protected BoardPosition getPositionPrototype() {
        return new BoardPosition(1, 1, null);
    }

    /**
     * Change the dimensions of this game board.
     * Note: we must call reset after changing the size, since the original game board will now be invalid.
     *
     * @param numRows the new number of rows for the board to have.
     * @param numCols the new number of cols for the board to have.
     */
    @Override
    public void setSize(int numRows, int numCols) {
        GameContext.log(3, "Board rows cols== " + numRows + ", " + numCols);
        positions_ = new BoardPositions(numRows, numCols);
        reset();
    }

    /**
     * @return retrieve the number of rows that the board has.
     */
    @Override
    public final int getNumRows() {
        return positions_.getNumRows();
    }

    /**
     * @return retrieve the number of columns that the board has.
     */
    @Override
    public final int getNumCols() {
        return positions_.getNumCols();
    }

    /**
     * consider making a defensive copy to avoid concurrent modification exception.
     *
     * @return moves made so far.
     */
    public MoveList<M> getMoveList() {
        return moveList_;
    }

    public int getTypicalNumMoves() {
        return 40;
    }

    @Override
    public BoardPosition getPosition(int row, int col) {
        return positions_.getPosition(row, col);
    }

    @Override
    public BoardPosition getPosition(Location loc) {
        return getPosition(loc.getRow(), loc.getCol());
    }

    protected void setPosition(BoardPosition pos) {
        positions_.setPosition(pos);
    }

    /**
     * @param move to make
     * @return false if the move is illegal
     */
    @Override
    public boolean makeMove(M move) {
        boolean legal = makeInternalMove(move);
        getMoveList().add(move);
        return legal;
    }

    /**
     * undo the last move made.
     *
     * @return the move that got undone
     */
    @Override
    public M undoMove() {
        if (!getMoveList().isEmpty()) {
            M move = getMoveList().removeLast();
            undoInternalMove(move);
            return move;
        }
        return null;
    }

    /**
     * Two boards are considered equal if all the pieces are in the same spot and have like ownership.
     *
     * @param b the board to compare to.
     * @return true if all the pieces in board b in the same spot and have like ownership as this.
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof Board)) return false;
        Board board = (Board) b;
        return (board.positions_.equals(positions_));
    }

    @Override
    public int hashCode() {
        return positions_.hashCode();
    }

    /**
     * @param move the move to make
     * @return false if the move is illegal (true if legal)
     */
    protected abstract boolean makeInternalMove(M move);

    /**
     * Allow reverting a move so we can step backwards in time.
     * Board is returned to the exact state it was in before the last move was made.
     */
    protected abstract void undoInternalMove(M move);

    /**
     * @return true if the specified position is within the bounds of the board
     */
    @Override
    public final boolean inBounds(int r, int c) {
        return positions_.inBounds(r, c);
    }

    @Override
    public String toString() {
        return positions_.toString();
    }

    /**
     * Check the 4 corners
     *
     * @param position position to see if in corner of board.
     * @return true if the specified BoardPosition is on the corner of the board
     */
    public boolean isInCorner(BoardPosition position) {
        return positions_.isInCorner(position);
    }

    /**
     * Corner points are also on the edge.
     *
     * @param position position to see if on edge of board.
     * @return true if the specified BoardPosition is on the edge of the board
     */
    public boolean isOnEdge(BoardPosition position) {
        return positions_.isOnEdge(position);
    }
}
