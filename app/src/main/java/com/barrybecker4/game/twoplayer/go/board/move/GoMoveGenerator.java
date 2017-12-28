/** Copyright by Barry G. Becker, 2004-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.move;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.CaptureList;
import com.barrybecker4.game.twoplayer.common.BestMoveFinder;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoProfiler;
import com.barrybecker4.game.twoplayer.go.board.GoSearchable;
import com.barrybecker4.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
public final class GoMoveGenerator {

    private final GoSearchable searchable_;

    /**
     * Constructor.
     */
    public GoMoveGenerator(GoSearchable searchable) {
        searchable_ = searchable;
    }

    /**
     * @return all reasonably good next moves with statically evaluated scores.
     */
    public final MoveList<GoMove> generateEvaluatedMoves(GoMove lastMove, ParameterArray weights) {

        GoProfiler prof = GoProfiler.getInstance();
        prof.startGenerateMoves();

        MoveList<GoMove> moveList = generatePossibleMoves(lastMove);

        for (GoMove move : moveList) {
            setMoveValue(weights, move);
        }
        BestMoveFinder finder = new BestMoveFinder(searchable_.getSearchOptions().getBestMovesSearchOptions());
        moveList = finder.getBestMoves(moveList);

        addPassingMoveIfNeeded(lastMove, moveList);

        prof.stopGenerateMoves();
        return moveList;
    }

    /**
     * @return all possible reasonable next moves. We try to limit to reasonable moves as best we can, but that
     * is difficult without static evaluation. At least no illegal moves will be returned.
     */
    final MoveList<GoMove> generatePossibleMoves(GoMove lastMove) {

        GoBoard board = searchable_.getBoard();
        MoveList<GoMove> moveList = new MoveList<>();
        int nCols = board.getNumCols();
        int nRows = board.getNumRows();

        CandidateMoveAnalyzer candidateMoves = new CandidateMoveAnalyzer(board);

        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();
        int lastMoveValue = (lastMove == null) ? 0 : lastMove.getValue();

        for (int i = 1; i <= nCols; i++) {
            for (int j = 1; j <= nRows; j++) {
                // if its a candidate move and not an immediate take-back (which would break the rule of ko)
                if (candidateMoves.isCandidateMove(j, i) && !isTakeBack(j, i, lastMove, board)) {
                    GoMove m = new GoMove(new ByteLocation(j, i), lastMoveValue, new GoStone(player1));

                    if (m.isSuicidal(board)) {
                        GameContext.log(3, "The move was a suicide (can't add it to the list): " + m);
                    } else {
                        moveList.add(m);
                    }
                }
            }
        }
        return moveList;
    }

    /**
     * Make the generated move, determine its value, set it into the move, and undo the move on the board.
     */
    private void setMoveValue(ParameterArray weights, GoMove m) {
        GoProfiler prof = GoProfiler.getInstance();
        prof.stopGenerateMoves();
        searchable_.makeInternalMove(m);

        m.setValue(searchable_.worth(m, weights));

        // now revert the board
        searchable_.undoInternalMove(m);
        prof.startGenerateMoves();
    }

    /**
     * If we are well into the game, include a passing move.
     * if none of the generated moves have an inherited value better than the passing move
     * (which just uses the value of the current move) then we should pass.
     */
    private void addPassingMoveIfNeeded(TwoPlayerMove lastMove, MoveList<GoMove> moveList) {

        Board b = searchable_.getBoard();
        if (searchable_.getNumMoves() > (b.getNumCols() + b.getNumRows())) {
            GoMove passMove = GoMove.createPassMove(lastMove.getValue(), !lastMove.isPlayer1());
            moveList.add(moveList.size(), passMove);
        }
    }

    /**
     * It is a take-back move if the proposed move position (row,col) would immediately replace the last captured piece
     * and capture the stone that did the capturing.
     *
     * @return true of this is an immediate take-back (not allowed in go - see "rule of ko")
     */
    public static boolean isTakeBack(int row, int col, GoMove lastMove, GoBoard board) {

        if (lastMove == null) return false;

        CaptureList captures = lastMove.getCaptures();
        if (captures != null && captures.size() == 1) {
            GoBoardPosition capture = (GoBoardPosition) captures.getFirst();
            if (capture.getRow() == row && capture.getCol() == col) {
                GoBoardPosition lastStone =
                        (GoBoardPosition) board.getPosition(lastMove.getToRow(), lastMove.getToCol());
                if (lastStone.getNumLiberties(board) == 1 && lastStone.getString().getMembers().size() == 1) {
                    GameContext.log(2, "it is a takeback ");
                    return true;
                }
            }
        }
        return false;
    }
}






