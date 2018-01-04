// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.go.analysis.territory;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * 形势更新器
 *
 * @author Barry Becker
 */
public class TerritoryUpdater {

    private GoBoard mBoard;

    /**
     * 两个玩家形势的差距
     * <p>
     * black-white = sum(health of stone i)
     */
    private float mTerritoryDelta = 0;

    private GroupAnalyzerMap mAnalyzerMap;

    public TerritoryUpdater(GoBoard board, GroupAnalyzerMap analyzerMap) {
        mBoard = board;
        mAnalyzerMap = analyzerMap;
    }

    public float getTerritoryDelta() {
        return mTerritoryDelta;
    }

    /**
     * 计算当前形势
     *
     * @param isEndOfGame 为true时会计算空点分数
     * @return 两个玩家形势的差距
     */
    public float updateTerritory(boolean isEndOfGame) {
        clearScores();

        mAnalyzerMap.clear();  /// need?

        calcAbsoluteHealth();
        float delta = calcRelativeHealth();
        if (isEndOfGame) {
            EmptyRegionUpdater emptyUpdater = new EmptyRegionUpdater(mBoard, mAnalyzerMap);
            delta += emptyUpdater.updateEmptyRegions();
        }

        mTerritoryDelta = delta;

        return delta;
    }

    /**
     * 先清除之前的分数和健康信息
     */
    private void clearScores() {
        for (int i = 1; i <= mBoard.getNumRows(); i++) {
            for (int j = 1; j <= mBoard.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) mBoard.getPosition(i, j);
                pos.setScore(0);

                if (pos.isOccupied()) {
                    GoStone stone = ((GoStone) pos.getPiece());
                    stone.setHealth(0);
                }
            }
        }
    }

    /**
     * 遍历所有棋群，计算绝对健康评分，为后面计算相对健康评分做准备
     */
    private void calcAbsoluteHealth() {
        for (IGoGroup g : mBoard.getGroups()) {
            mAnalyzerMap.getAnalyzer(g).calculateAbsoluteHealth(mBoard);
        }
    }

    /**
     * 遍历所有棋群，计算相对健康评分
     *
     * @return
     */
    private float calcRelativeHealth() {
        float delta = 0;
        for (IGoGroup g : mBoard.getGroups()) {
            float health = mAnalyzerMap.getAnalyzer(g).calculateRelativeHealth(mBoard);
            g.updateTerritory(health);
            delta += health * g.getNumStones();
        }
        return delta;
    }
}