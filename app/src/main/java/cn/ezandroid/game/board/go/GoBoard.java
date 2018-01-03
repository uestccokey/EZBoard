/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go;

import java.util.List;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.common.TwoPlayerBoard;
import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.go.analysis.CornerChecker;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.string.GoString;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;
import cn.ezandroid.game.board.go.move.GoMove;
import cn.ezandroid.game.board.go.update.BoardUpdater;
import cn.ezandroid.game.board.go.update.CaptureCounts;

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
        GoBoard b = new GoBoard(this);
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

    public List getHandicapPositions() {
        return handicap_.getStarPoints();
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

    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move, and updates groups,
     * removes captures, and counts territory.
     *
     * @return false if the move is somehow invalid
     */
    @Override
    protected boolean makeInternalMove(GoMove move) {
        // if its a passing move, there is nothing to do
        if (move.isPassOrResignation()) {
            GameContext.log(2, move.isPassingMove() ? "Making passing move" : "Resigning");   // NON-NLS
            return true;
        }

        boolean valid = super.makeInternalMove(move);
        boardUpdater_.updateAfterMove(move);
        return valid;
    }

    /**
     * for Go, undoing a move means changing that space back to a blank, restoring captures, and updating groups.
     *
     * @param move the move to undo.
     */
    @Override
    protected void undoInternalMove(GoMove move) {
        // there is nothing to do if it is a pass
        if (move.isPassingMove()) {
            return;
        }

        boardUpdater_.updateAfterRemove(move);
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
