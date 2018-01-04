// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.go.analysis.territory;

import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionLists;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 空点归属更新器
 *
 * @author Barry Becker
 */
public class EmptyRegionUpdater {

    // 眼位的默认分数
    private static final float STONE_IN_EYE_CONTRIB = 0.1f;

    private GoBoard mBoard;
    private NeighborAnalyzer mNeighborAnalyzer;
    private GroupAnalyzerMap mAnalyzerMap;

    public EmptyRegionUpdater(GoBoard board, GroupAnalyzerMap analyzerMap) {
        mBoard = board;
        mNeighborAnalyzer = new NeighborAnalyzer(board);
        mAnalyzerMap = analyzerMap;
    }

    /**
     * 遍历所有空点，计算它的得分
     *
     * @return
     */
    public float updateEmptyRegions() {
        float diffScore = 0;

        if (mBoard.getMoveList().getNumMoves() <= 2 * mBoard.getNumRows()) {
            return diffScore;
        }
        // later in the game we can take the analysis all the way to the edge.
        //float ratio = (float)mBoard.getMoveList().getNumMoves() / mBoard.getTypicalNumMoves();

        int min = 1;
        int rMax = mBoard.getNumRows();
        int cMax = mBoard.getNumCols();
        Box box = new Box(min, min, rMax, cMax);

        GoBoardPositionLists emptyLists = new GoBoardPositionLists();

        for (int i = min; i <= rMax; i++) {
            for (int j = min; j <= cMax; j++) {
                GoBoardPosition pos = (GoBoardPosition) mBoard.getPosition(i, j);
                diffScore += updateEmptyRegionFromSeed(box, emptyLists, pos);
            }
        }

        emptyLists.unvisitPositionsInLists();
        return diffScore;
    }

    /**
     * 如果一个空点是眼位，则直接设置其相应分数
     * 如果不是眼位，则首先找出该空点所属的最大空点空间，及其空间周围的棋子集合
     * 并遍历该棋子集合，计算出平均分数，设置给该空点空间的所有空点
     *
     * @param box
     * @param emptyLists
     * @param pos
     * @return
     */
    private float updateEmptyRegionFromSeed(Box box, GoBoardPositionLists emptyLists,
                                            GoBoardPosition pos) {
        float diffScore = 0;
        if (pos.getString() == null && !pos.isInEye()) {
            assert pos.isUnoccupied();
            if (!pos.isVisited()) {
                // don't go all the way to the borders (until the end of the game),
                // since otherwise we will likely get only one big empty region.
                GoBoardPositionList empties =
                        mNeighborAnalyzer.findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED, box);
                emptyLists.add(empties);

                GoBoardPositionSet nbrs = mNeighborAnalyzer.findOccupiedNobiNeighbors(empties);
                float avg = calcAverageScore(nbrs);

                float score = avg * (float) nbrs.size() / Math.max(1, Math.max(nbrs.size(), empties.size()));
                assert (score <= 1.0 && score >= -1.0) : "score=" + score + " avg=" + avg;

                for (GoBoardPosition space : empties) {
                    space.setScore(score);
                    diffScore += score;
                }
            }
        } else if (pos.isInEye()) {
            pos.setScore(pos.getGroup().isOwnedByPlayer1() ? STONE_IN_EYE_CONTRIB : -STONE_IN_EYE_CONTRIB);
        }
        return diffScore;
    }

    private float calcAverageScore(GoBoardPositionSet stones) {
        float totalScore = 0;
        for (GoBoardPosition stone : stones) {
            IGoGroup group = stone.getString().getGroup();
            boolean useCached = false;
            totalScore += mAnalyzerMap.getAnalyzer(group).getRelativeHealth(mBoard, useCached);
        }
        return totalScore / stones.size();
    }
}