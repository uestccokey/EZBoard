// Copyright by Barry G. Becker, 2013-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.common.board;

import cn.ezandroid.game.board.common.Move;

/**
 * 棋盘接口
 *
 * @param <M>
 */
public interface IBoard<M extends Move> {

    /**
     * 重置
     */
    void reset();

    /**
     * 深克隆
     *
     * @return
     */
    IBoard copy();

    /**
     * 落子
     *
     * @param move
     * @return
     */
    boolean makeMove(M move);

    /**
     * 悔棋
     *
     * @return
     */
    M undoMove();
}
