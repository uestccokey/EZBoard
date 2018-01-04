/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;

/**
 * 静态评估棋盘的形势
 *
 * @author Barry Becker
 */
public final class BoardEvaluator {

    private WorthCalculator mWorthCalculator;

    public BoardEvaluator(GoBoard board) {
        mWorthCalculator = new WorthCalculator(board, new GroupAnalyzerMap());
    }

    /**
     * 获取指定玩家的形势
     *
     * @param forPlayer1
     * @param isEndOfGame
     * @return
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        return mWorthCalculator.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * 更新双方形势，并获取形势的差距
     *
     * @return
     */
    public float updateTerritoryAtEndOfGame() {
        return mWorthCalculator.updateTerritoryAtEndOfGame();
    }
}
