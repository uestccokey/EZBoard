/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.analysis.territory.TerritoryAnalyzer;
import cn.ezandroid.game.board.go.analysis.territory.TerritoryUpdater;

/**
 * 静态评估棋盘的形势
 *
 * @author Barry Becker
 */
public class WorthCalculator {

    private TerritoryAnalyzer mTerritoryAnalyzer;
    private TerritoryUpdater mTerritoryUpdater;

    public WorthCalculator(GoBoard board, GroupAnalyzerMap analyzerMap) {
        mTerritoryAnalyzer = new TerritoryAnalyzer(board, analyzerMap);
        mTerritoryUpdater = new TerritoryUpdater(board, analyzerMap);
    }

    /**
     * 获取指定玩家的形势
     *
     * @param forPlayer1
     * @param isEndOfGame
     * @return
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        return mTerritoryAnalyzer.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * 更新双方形势，并获取形势的差距
     *
     * @return
     */
    public float updateTerritoryAtEndOfGame() {
        return mTerritoryUpdater.updateTerritory(true);
    }
}