/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common;

import java.util.List;

import cn.ezandroid.game.board.common.board.Board;
import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.common.board.GamePiece;

/**
 * 两人游戏棋盘模型
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerBoard<M extends TwoPlayerMove> extends Board<M> {

    public TwoPlayerBoard() {}

    public TwoPlayerBoard(TwoPlayerBoard<M> board) {
        super(board);
    }

    public abstract TwoPlayerBoard<M> copy();

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

    protected void makeMoves(List<M> moves) {
        for (M move : moves) {
            makeMove(move);
        }
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