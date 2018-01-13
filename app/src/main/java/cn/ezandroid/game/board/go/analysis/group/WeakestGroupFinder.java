/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import java.util.Set;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.elements.group.GoGroup;
import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 最弱棋群查找器
 * <p>
 * 查找棋盘上相对于其他棋群最弱的一个棋群
 *
 * @author Barry Becker
 */
class WeakestGroupFinder {

    private GoBoard mBoard;
    private GroupAnalyzerMap mAnalyzerMap;

    public WeakestGroupFinder(GoBoard board, GroupAnalyzerMap analyzerMap) {
        this.mBoard = board;
        this.mAnalyzerMap = analyzerMap;
    }

    /**
     * 给定一组同色的棋子点位集合，找出与之接近的最弱敌方棋群
     *
     * @param groupStones
     * @return
     */
    public GoGroup findWeakestGroup(GoBoardPositionSet groupStones) {
        boolean isPlayer1 = groupStones.getOneMember().getPiece().isOwnedByPlayer1();
        Set enemyNbrGroups = getEnemyGroupNeighbors(groupStones, isPlayer1);

        // we multiply by a +/- sign depending on the side
        float side = isPlayer1 ? 1.0f : -1.0f;

        // initialize to strongest value, then anything will be weaker.
        double weakestHealth = -side;

        GoGroup weakestGroup = null;
        for (Object egroup : enemyNbrGroups) {
            GoGroup enemyGroup = (GoGroup) egroup;
            double h = mAnalyzerMap.getAnalyzer(enemyGroup).getAbsoluteHealth();
            if ((side * h) > (side * weakestHealth)) {
                weakestHealth = h;
                weakestGroup = enemyGroup;
            }
        }
        return weakestGroup;
    }

    /**
     * 获取指定同色棋子点位集合周围的敌方棋群集合
     * <p>
     * TODO 目前效率较低，n^2的复杂度
     *
     * @param groupStones
     * @param isPlayer1
     * @return
     */
    private Set getEnemyGroupNeighbors(GoBoardPositionSet groupStones, boolean isPlayer1) {
        GoGroupSet enemyNbrs = new GoGroupSet();
        NeighborAnalyzer nbrAnalyzer = new NeighborAnalyzer(mBoard);

        // for every stone in the group.
        for (GoBoardPosition stone : groupStones) {
            GoBoardPositionSet nbrs = nbrAnalyzer.findGroupNeighbors(stone, false);
            addEnemyNeighborsForStone(enemyNbrs, stone, nbrs, isPlayer1);
        }
        return enemyNbrs;
    }

    private void addEnemyNeighborsForStone(GoGroupSet enemyNbrs, GoBoardPosition stone,
                                           GoBoardPositionSet nbrs, boolean isPlayer1) {
        for (GoBoardPosition possibleEnemy : nbrs) {
            if (possibleEnemy.getPiece() != null
                    && possibleEnemy.getPiece().isOwnedByPlayer1() != isPlayer1
                    && !possibleEnemy.isInEye()) {
                // setting visited to true to indicate there is an enemy nbr within group distance.
                stone.setVisited(true);
                // if the group is already there, it does not get added again.
                assert (possibleEnemy.getGroup() != null) : "Possible enemy, "
                        + possibleEnemy + ", had no group associated with it!";
                enemyNbrs.add(possibleEnemy.getGroup());
            }
        }
    }
}