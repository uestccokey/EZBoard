/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.territory;

import cn.ezandroid.game.board.common.board.GamePiece;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * 形势分析器
 *
 * @author Barry Becker
 */
public class TerritoryAnalyzer {

    private GoBoard mBoard;
    private GroupAnalyzerMap mAnalyzerMap;

    public TerritoryAnalyzer(GoBoard board, GroupAnalyzerMap analyzerMap) {
        mBoard = board;
        mAnalyzerMap = analyzerMap;
    }

    /**
     * 获取指定玩家的当前形势
     *
     * @param forPlayer1
     * @param isEndOfGame
     * @return
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        float territoryEstimate = 0;
        // we should be able to just sum all the position scores now.
        for (int i = 1; i <= mBoard.getNumRows(); i++) {
            for (int j = 1; j <= mBoard.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) mBoard.getPosition(i, j);
                double val = getTerritoryEstimateForPosition(pos, forPlayer1, isEndOfGame);
                territoryEstimate += val;
            }
        }
        return (int) territoryEstimate;
    }

    private float getTerritoryEstimateForPosition(GoBoardPosition pos, boolean forPlayer1,
                                                  boolean isEndOfGame) {
        double val = isEndOfGame ? (forPlayer1 ? 1.0 : -1.0) : pos.getScore();
        float territoryEstimate = 0;

        // the territory estimate will always be positive for both sides.
        if (pos.isUnoccupied()) {
            if (forPlayer1 && pos.getScore() > 0) {
                territoryEstimate += val;
            } else if (!forPlayer1 && pos.getScore() < 0) {
                territoryEstimate -= val;  // will be positive
            }
            // TODO 如果眼位所属的棋群是死棋，则计算到该棋串的对手的分数中
        } else { // occupied
            GamePiece piece = pos.getPiece();
            IGoGroup group = pos.getGroup();
            assert (piece != null);
            if (group != null) {
                // add credit for probable captured stones.
                double relHealth = mAnalyzerMap.getAnalyzer(group).getRelativeHealth(mBoard, isEndOfGame);
                if (forPlayer1 && !piece.isOwnedByPlayer1() && relHealth >= 0) {
                    territoryEstimate += val;
                } else if (!forPlayer1 && piece.isOwnedByPlayer1() && relHealth <= 0) {
                    territoryEstimate -= val;
                }
            }
        }
        return territoryEstimate;
    }
}