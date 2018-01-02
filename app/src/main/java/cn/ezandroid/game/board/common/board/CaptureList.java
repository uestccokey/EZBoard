/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.board;

import java.util.LinkedList;

/**
 * 捕获的棋子列表
 *
 * @author Barry Becker
 */
public class CaptureList extends LinkedList<BoardPosition> {

    public CaptureList() {}

    public CaptureList(CaptureList captureList) {
        for (BoardPosition capture : captureList) {
            add(capture.copy());
        }
    }

    public CaptureList copy() {
        return new CaptureList(this);
    }

    /**
     * 从棋盘上移除
     */
    public void removeFromBoard(Board board) {
        modifyCaptures(board, true);
    }

    /**
     * 恢复到棋盘上
     */
    public void restoreOnBoard(Board board) {
        modifyCaptures(board, false);
    }

    /**
     * 要么把棋子从棋盘上移除，要么把移除的棋子放回原处
     *
     * @param board
     * @param remove
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
     * 获取是否当前位置棋子已被捕获
     *
     * @param p
     * @return
     */
    public boolean isAlreadyCaptured(BoardPosition p) {
        for (BoardPosition capture : this) {
            if (capture.getRow() == p.getRow() &&
                    capture.getCol() == p.getCol() &&
                    capture.getPiece().getType() == p.getPiece().getType())
                return true;
        }
        return false;
    }
}

