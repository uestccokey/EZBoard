/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis;

import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * Creates a set of reasonable next moves for a given player.
 * In theory, all empties should be considered, but in practice, we keep
 * a shorter list of reasonable moves lest things get intractable.
 * Immutable.
 *
 * @author Barry Becker
 */
public class CandidateMoveAnalyzer {

    /** Distance from already played stones that we will splat a footprint of candidate stones to add. */
    private static final int CANDIDATE_MOVE_OFFSET = 2;

    private final GoBoard board_;

    private final int size_;

    /** this is an auxiliary structure to help determine candidate moves. */
    private final boolean[][] candidateMoves_;

    /**
     * Constructor.
     */
    public CandidateMoveAnalyzer(GoBoard board) {
        board_ = board;
        size_ = board.getNumRows();
        candidateMoves_ = new boolean[size_ + 1][size_ + 1];
        initialize();
    }

    /**
     * If there is any chance at all that this could be a reasonable move, true is returned.
     *
     * @return true if this position is a reasonable next move.
     */
    public final boolean isCandidateMove(int row, int col) {
        return candidateMoves_[row][col];
    }

    /**
     * @return number of candidate moves found.
     */
    public final int getNumCandidates() {
        int num = 0;
        for (int i = 1; i <= size_; i++) {
            for (int j = 1; j <= size_; j++) {
                if (isCandidateMove(i, j)) num++;
            }
        }
        return num;
    }

    /**
     * we start with a default list of good starting moves, and
     * add to it all moves within 2 spaces of those that have already been played.
     * <p>
     * Fill a 2 stone wide strip on the 3rd and 4rth lines of the board.
     * This includes the star points and many others as candidates to consider
     */
    private void initialize() {
        for (int i = 3; i <= size_ - 2; i++) {
            tryToAddCandidateMove(board_.getPosition(i, 3));
            tryToAddCandidateMove(board_.getPosition(i, 4));
            tryToAddCandidateMove(board_.getPosition(i, size_ - 2));
            tryToAddCandidateMove(board_.getPosition(i, size_ - 3));
        }
        for (int j = 5; j <= size_ - 4; j++) {
            tryToAddCandidateMove(board_.getPosition(3, j));
            tryToAddCandidateMove(board_.getPosition(4, j));
            tryToAddCandidateMove(board_.getPosition(size_ - 2, j));
            tryToAddCandidateMove(board_.getPosition(size_ - 3, j));
        }
        // also make the center space a candidate move
        tryToAddCandidateMove(board_.getPosition(((size_ + 1) >> 1), ((size_ + 1) >> 1)));

        determineAdjacentCandidates();
    }

    /**
     * this method splats a footprint of trues around the current moves.
     * later we look for empty spots that are true for candidate moves
     */
    private void determineAdjacentCandidates() {
        for (int i = 1; i <= size_; i++) {
            for (int j = 1; j <= size_; j++) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition(i, j);
                if (pos.isOccupied()) {
                    addCandidateMoves(pos);
                }
            }
        }
    }

    /**
     * This method splats a footprint of trues around the specified move.
     *
     * @param stone position to add candidate moves around.
     */
    private void addCandidateMoves(GoBoardPosition stone) {
        int startrow = Math.max(stone.getRow() - CANDIDATE_MOVE_OFFSET, 1);
        int stoprow = Math.min(stone.getRow() + CANDIDATE_MOVE_OFFSET, size_);
        int startcol = Math.max(stone.getCol() - CANDIDATE_MOVE_OFFSET, 1);
        int stopcol = Math.min(stone.getCol() + CANDIDATE_MOVE_OFFSET, size_);
        // set the footprint
        for (int i = startrow; i <= stoprow; i++) {
            for (int j = startcol; j <= stopcol; j++) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition(i, j);
                tryToAddCandidateMove(pos);
            }
        }
    }

    /**
     * Add only if unoccupied and not an unconditionally alive eye.
     * never add a stone from either side to an unconditionally alive eye. There is no advantage to it.
     *
     * @param position the position to try adding as a possible candidate move.
     */
    private void tryToAddCandidateMove(BoardPosition position) {
        GoBoardPosition pos = (GoBoardPosition) position;

        if (pos.isUnoccupied() && !(pos.getEye() != null && pos.getEye().isUnconditionallyAlive())) {
            candidateMoves_[pos.getRow()][pos.getCol()] = true;
        }
    }
}
