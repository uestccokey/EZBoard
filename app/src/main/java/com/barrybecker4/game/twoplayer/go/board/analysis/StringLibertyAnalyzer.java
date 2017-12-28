/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString;

/**
 * Determines number of liberties on a string.
 *
 * @author Barry Becker
 */
public class StringLibertyAnalyzer {

    /** Keep track of number of liberties instead of computing each time (for performance). */
    private GoBoardPositionSet liberties_;

    private GoBoard board;
    private GoString string;
    private boolean cacheValid;

    /**
     * Constructor.
     *
     * @param board  game board
     * @param string the initial set of liberties.
     */
    public StringLibertyAnalyzer(GoBoard board, GoString string) {
        this.board = board;
        this.string = string;
        cacheValid = false;
    }

    /**
     * @return number of liberties that the string has
     */
    public final GoBoardPositionSet getLiberties() {
        if (!cacheValid) {
            initializeLiberties();
        }
        return liberties_;
    }

    public void invalidate() {
        cacheValid = false;
    }

    private void initializeLiberties() {
        liberties_ = new GoBoardPositionSet();
        GoBoardPositionSet members = string.getMembers();

        for (GoBoardPosition stone : members) {
            addLiberties(stone, board);
        }
        cacheValid = true;
    }

    /**
     * only add liberties for this stone if they are not already in the set
     */
    private void addLiberties(GoBoardPosition stone, GoBoard board) {
        int r = stone.getRow();
        int c = stone.getCol();
        if (r > 1) {
            addLiberty(board.getPosition(r - 1, c));
        }
        if (r < board.getNumRows()) {
            addLiberty(board.getPosition(r + 1, c));
        }
        if (c > 1) {
            addLiberty(board.getPosition(r, c - 1));
        }
        if (c < board.getNumCols()) {
            addLiberty(board.getPosition(r, c + 1));
        }
    }

    /**
     * This assumes a HashSet will not allow you to add the same object twice (no dupes)
     *
     * @param libertySpace the position of the liberty to add.
     */
    private void addLiberty(BoardPosition libertySpace) {

        if (libertySpace.isUnoccupied())
            liberties_.add((GoBoardPosition) libertySpace);
    }
}