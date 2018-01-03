/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.move.GoMove;

/**
 * 棋盘更新器
 * <p>
 * 负责更新落子或者悔棋后的棋盘
 *
 * @author Barry Becker
 */
public class BoardUpdater {

    private PostMoveUpdater mPostMoveUpdater;
    private PostRemoveUpdater mPostRemoveUpdater;
    private CaptureCounts mCaptureCounts;

    public BoardUpdater(GoBoard board, CaptureCounts capCounts) {
        mCaptureCounts = capCounts;
        initialize(board);
    }

    public CaptureCounts getCaptureCounts() {
        return mCaptureCounts.copy();
    }

    private void initialize(GoBoard board) {
        mPostMoveUpdater = new PostMoveUpdater(board, mCaptureCounts);
        mPostRemoveUpdater = new PostRemoveUpdater(board, mCaptureCounts);
    }

    /**
     * 获取指定玩家的被提子数
     *
     * @param player1StonesCaptured
     * @return
     */
    public int getNumCaptures(boolean player1StonesCaptured) {
        return mCaptureCounts.getNumCaptures(player1StonesCaptured);
    }

    /**
     * 落子后更新棋盘
     *
     * @param move
     */
    public void updateAfterMove(GoMove move) {
        mPostMoveUpdater.update(move);
    }

    /**
     * 悔棋后更新棋盘
     *
     * @param move
     */
    public void updateAfterRemove(GoMove move) {
        mPostRemoveUpdater.update(move);
    }
}
