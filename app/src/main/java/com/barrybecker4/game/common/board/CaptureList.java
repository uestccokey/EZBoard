/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.board;

import java.util.LinkedList;

/**
 * This class represents a linked list of captured pieces.
 * It provides convenience methods for removing and restoring those
 * pieces to a game board.
 *
 * @author Barry Becker
 * @see Board
 */
public class CaptureList extends LinkedList<BoardPosition> {

    public CaptureList() { }

    /**
     * copy constructor
     */
    public CaptureList(CaptureList captureList) {

        for (BoardPosition capture : captureList) {
            add(capture.copy());
        }
    }

    /**
     * @return a deep copy of the capture list.
     */
    public CaptureList copy() {

        return new CaptureList(this);
    }

    /**
     * remove the captured pieces from the board.
     */
    public void removeFromBoard(Board board) {
        modifyCaptures(board, true);
    }

    /**
     * restore the captured pieces on the board.
     */
    public void restoreOnBoard(Board board) {
        modifyCaptures(board, false);
    }

    /**
     * Either take the pieces off the board, or put them back on based on the value of remove.
     *
     * @param board  the game board.
     * @param remove if true then remove the pieces, else restore them
     */
    protected void modifyCaptures(Board board, boolean remove) {
        for (BoardPosition capture : this) {
            BoardPosition pos = board.getPosition(capture.getRow(), capture.getCol());
            assert pos != null : "Captured position was null " + capture;
            if (remove)
                pos.setPiece(null);
            else {
                pos.setPiece(capture.getPiece().copy());
            }
        }
    }

    /**
     * @return true if the piece was already captured
     */
    public boolean alreadyCaptured(BoardPosition p) {
        for (BoardPosition capture : this) {
            if (capture.getRow() == p.getRow() &&
                    capture.getCol() == p.getCol() &&
                    capture.getPiece().getType() == p.getPiece().getType())
                return true;
        }
        return false;
    }

    /**
     * Produces a string representation of the list of captured pieces.
     */
    @Override
    public String toString() {
        String s = " These piece(s) were captured by this move:\n";
        for (BoardPosition p : this) {
            s += '(' + p.toString() + "),";
        }
        return s;
    }
}

