/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.common;

import com.ezandroid.board.common.board.Board;
import com.ezandroid.board.common.board.BoardPosition;
import com.ezandroid.board.common.board.GamePiece;

import java.util.List;

/**
 * Defines the structure of the board and the pieces on it.
 * Each BoardPosition can contain a piece.
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerBoard<M extends TwoPlayerMove> extends Board<M> {

    /** default constructor */
    public TwoPlayerBoard() {}

    /** copy constructor */
    public TwoPlayerBoard(TwoPlayerBoard<M> board) {
        super(board);
    }

    public abstract TwoPlayerBoard<M> copy();

    /**
     * given a move specification, execute it on the board
     * This places the players symbol at the position specified by move.
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    @Override
    protected boolean makeInternalMove(M move) {
        if (!move.isPassOrResignation()) {
            BoardPosition pos = getPosition(move.getToLocation());
            assert (move.getPiece() != null) : "move's piece was null :" + move;
            pos.setPiece(move.getPiece());
            GamePiece piece = pos.getPiece();

            if (GameContext.getDebugMode() > 0) {
                piece.setAnnotation(Integer.toString(getMoveList().getNumMoves()));
            }
        }
        return true;
    }

    /**
     * @param moves list of moves to make all at once.
     */
    protected void makeMoves(List<M> moves) {
        for (M move : moves) {
            makeMove(move);
        }
    }

    /**
     * Num different states. E.g. black queen.
     * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
     *
     * @return number of different states this position can have.
     */
    public abstract int getNumPositionStates();


    /**
     * The index of the state for this position.
     * Perhaps this would be better abstract.
     *
     * @return The index of the state for this position.
     */
    public int getStateIndex(BoardPosition pos) {
        if (!pos.isOccupied()) {
            throw new IllegalArgumentException("this should only be called on occupied positions. Position = " + pos);
        }
        return pos.getPiece().isOwnedByPlayer1() ? 1 : 2;
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(1000);
        bldr.append("\n");
        int nRows = getNumRows();
        int nCols = getNumCols();
        TwoPlayerMove lastMove = getMoveList().getLastMove();

        for (int i = 1; i <= nRows; i++) {
            boolean followingLastMove = false;
            for (int j = 1; j <= nCols; j++) {
                BoardPosition pos = this.getPosition(i, j);
                if (pos.isOccupied()) {
                    if (lastMove != null && pos.getLocation().equals(lastMove.getToLocation())) {
                        bldr.append("[").append(pos.getPiece()).append("]");
                        followingLastMove = true;
                    } else {
                        bldr.append(followingLastMove ? "" : " ").append(pos.getPiece());
                        followingLastMove = false;
                    }
                } else {
                    bldr.append(followingLastMove ? "" : " ").append("_");
                    followingLastMove = false;
                }
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }
}