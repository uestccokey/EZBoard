/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.CornerChecker;
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroupSet;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoStringSet;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.board.update.BoardUpdater;
import com.barrybecker4.game.twoplayer.go.board.update.CaptureCounts;

import java.util.List;

/**
 * Representation of a Go Game Board
 * There are a lot of data structures to organize the state of the pieces.
 * For example, we update strings, and groups (and eventually armies) after each move.
 * After updating we can use these structures to estimate territory for each side.
 *
 * @author Barry Becker
 */
public final class GoBoard extends TwoPlayerBoard<GoMove> {

    /** This is a set of active groups. Groups are composed of strings. */
    private GoGroupSet groups_;

    /** Handicap stones are on the star points, unless the board is very small */
    private HandicapStones handicap_;

    /** Updates the board's structure after making or undoing moves */
    private BoardUpdater boardUpdater_;

    /**
     * Constructor.
     *
     * @param size              The dimension (i.e. the number rows and columns). Must be square.
     * @param numHandicapStones number of black handicap stones to initialize with.
     */
    public GoBoard(int size, int numHandicapStones) {
        setSize(size, size);
        setHandicap(numHandicapStones);

        init(new CaptureCounts());
    }

    /**
     * Copy constructor
     */
    public GoBoard(GoBoard board) {
        super(board);

        handicap_ = board.handicap_;
        NeighborAnalyzer analyzer = new NeighborAnalyzer(this);
        analyzer.determineAllStringsOnBoard();
        groups_ = analyzer.findAllGroupsOnBoard();

        init(board.boardUpdater_.getCaptureCounts());
    }

    @Override
    public synchronized GoBoard copy() {
        getProfiler().startCopyBoard();
        GoBoard b = new GoBoard(this);
        getProfiler().stopCopyBoard();
        return b;
    }

    void setPosition(GoBoardPosition pos) {
        super.setPosition(pos);
    }

    /**
     * Start over from the beginning and reinitialize everything.
     * The first time through we need to initialize the star-point positions.
     */
    @Override
    public void reset() {
        super.reset();
        groups_ = new GoGroupSet();

        setHandicap(getHandicap());
        init(new CaptureCounts());
    }

    private void init(CaptureCounts capCounts) {
        boardUpdater_ = new BoardUpdater(this, capCounts);
    }

    @Override
    protected BoardPosition getPositionPrototype() {
        return new GoBoardPosition(1, 1, null, null);
    }

    public void setHandicap(int numHandicapStones) {
        handicap_ = new HandicapStones(numHandicapStones, getNumRows());
        makeMoves(handicap_.getHandicapMoves());
    }

    /**
     * get the number of handicap stones used in this game.
     *
     * @return number of handicap stones
     */
    public int getHandicap() {
        if (handicap_ == null) {
            return 0;
        }
        return handicap_.getNumber();
    }

    /**
     * in go there is not really a theoretical limit to the number of moves,
     * but practically if we exceed this then we award the game to whoever is ahead.
     *
     * @return the maximum number of moves ever expected for this game.
     */
    @Override
    public int getMaxNumMoves() {
        return 2 * positions_.getNumBoardSpaces();
    }

    /**
     * Num different states.
     * This is used primarily for the Zobrist hash. You do not need to override if you do not use it.
     * The states are player1, player2, or empty (we may want to add ko).
     *
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return 3;
    }

    public List getHandicapPositions() {
        return handicap_.getStarPoints();
    }

    /**
     * @return typical number of moves in a go game.
     */
    @Override
    public int getTypicalNumMoves() {
        return positions_.getNumBoardSpaces() - getNumRows();
    }

    /**
     * get the current set of active groups. Should be read only. Do not modify.
     *
     * @return all the valid groups on the board (for both sides)
     */
    public GoGroupSet getGroups() {
        return groups_;
    }

    public void setGroups(GoGroupSet groups) {
        groups_ = groups;
    }

    /**
     * Adjust the liberties on the strings (both black and white) that we touch.
     *
     * @param liberty either occupied or not depending on if we are placing the stone or removing it.
     */
    public void adjustLiberties(GoBoardPosition liberty) {
        NeighborAnalyzer na = new NeighborAnalyzer(this);
        GoStringSet stringNbrs = na.findStringNeighbors(liberty);
        for (IGoString sn : stringNbrs) {
            ((GoString) sn).changedLiberty(liberty);
        }
    }

    /**
     * Make sure that all the positions on the board are reset to the unvisited state.
     */
    public void unvisitAll() {
        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) getPosition(i, j);
                pos.setVisited(false);
            }
        }
    }

    private GoProfiler getProfiler() {
        return GoProfiler.getInstance();
    }

    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move, and updates groups,
     * removes captures, and counts territory.
     *
     * @return false if the move is somehow invalid
     */
    @Override
    protected boolean makeInternalMove(GoMove move) {
        getProfiler().startMakeMove();

        // if its a passing move, there is nothing to do
        if (move.isPassOrResignation()) {
            GameContext.log(2, move.isPassingMove() ? "Making passing move" : "Resigning");   // NON-NLS
            getProfiler().stopMakeMove();
            return true;
        }

        boolean valid = super.makeInternalMove(move);
        boardUpdater_.updateAfterMove(move);

        getProfiler().stopMakeMove();
        return valid;
    }

    /**
     * for Go, undoing a move means changing that space back to a blank, restoring captures, and updating groups.
     *
     * @param move the move to undo.
     */
    @Override
    protected void undoInternalMove(GoMove move) {
        getProfiler().startUndoMove();

        // there is nothing to do if it is a pass
        if (move.isPassingMove()) {
            getProfiler().stopUndoMove();
            return;
        }

        boardUpdater_.updateAfterRemove(move);
        getProfiler().stopUndoMove();
    }

    public int getNumCaptures(boolean player1StonesCaptured) {
        return boardUpdater_.getNumCaptures(player1StonesCaptured);
    }

    /**
     * Corner triples are the 3 points closest to a corner
     *
     * @param position position to see if in corner of board.
     * @return true if the specified BoardPosition is on the order of the board
     */
    public boolean isCornerTriple(BoardPosition position) {
        return new CornerChecker(getNumRows(), getNumCols()).isCornerTriple(position);
    }

    /**
     * @return either the number of black or white stones.
     */
    public int getNumStones(boolean forPlayer1) {
        int numStones = 0;

        // we should be able to just sum all the position scores now.
        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) getPosition(i, j);
                if (pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == forPlayer1) {
                    numStones++;
                }
            }
        }
        return numStones;
    }

    @Override
    public String toString() {
        int rows = getNumRows();
        int cols = getNumCols();
        StringBuilder buf = new StringBuilder((rows + 2) * (cols + 2));

        buf.append("   ");
        for (int j = 1; j <= rows; j++) {
            buf.append(j % 10);
        }
        buf.append(' ');
        buf.append("\n  ");
        for (int j = 1; j <= cols + 2; j++) {
            buf.append('-');
        }
        buf.append('\n');

        for (int i = 1; i <= rows; i++) {
            buf.append(i / 10);
            buf.append(i % 10);
            buf.append('|');
            for (int j = 1; j <= cols; j++) {
                GoBoardPosition space = (GoBoardPosition) getPosition(i, j);
                if (space.isOccupied()) {
                    buf.append(space.getPiece().isOwnedByPlayer1() ? 'X' : 'O');
                } else {
                    buf.append(' ');
                }
            }
            buf.append('|');
            buf.append('\n');
        }
        return buf.toString();
    }
}
