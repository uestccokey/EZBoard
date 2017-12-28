// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.move;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;

import java.util.Iterator;

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
public final class UrgentMoveGenerator {

    /**
     * Size of group that needs to be in atari before we consider a group urgent.
     * Perhaps this should be one.
     */
    private static final int CRITICAL_GROUP_SIZE = 3;

    private final GoBoard board_;

    /**
     * Constructor.
     */
    public UrgentMoveGenerator(GoBoard board) {
        board_ = board;
    }

    /**
     * @param moves    all possible moves from the current board state for player to move next.
     * @param lastMove last stone placed on board
     * @return urgent moves from this position.
     */
    public final MoveList<GoMove> generateUrgentMoves(MoveList<GoMove> moves, TwoPlayerMove lastMove) {

        GoMove lastMovePlayed = (GoMove) lastMove;

        // just keep the moves that take captures
        Iterator<GoMove> it = moves.iterator();
        while (it.hasNext()) {
            GoMove move = it.next();

            // urgent if we capture or atari other stones.
            boolean isUrgent = move.getNumCaptures() > 0 || putsGroupInAtari(lastMovePlayed);
            if (isUrgent) {
                move.setUrgent(true);
            } else {
                it.remove();
            }
        }
        return moves;
    }

    /**
     * @return true if move is in danger (jeopardy).
     */
    public static boolean inJeopardy(GoMove move, GoBoard board) {
        return (move.numStonesAtaried(board) >= CRITICAL_GROUP_SIZE);
    }

    /**
     * Determine if the last move caused atari on another group (without putting ourselves in atari).
     *
     * @param lastMovePlayed last position just played.
     * @return true if the lastMovePlayed puts the lastPositions string in atari.
     */
    private boolean putsGroupInAtari(GoMove lastMovePlayed) {
        GoBoardPosition lastPos = (GoBoardPosition) board_.getPosition(lastMovePlayed.getToLocation());
        return (lastMovePlayed.numStonesAtaried(board_) >= CRITICAL_GROUP_SIZE
                && lastPos.getString().getNumLiberties(board_) > 1);
    }
}






