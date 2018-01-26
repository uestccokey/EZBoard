/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.group.GoGroup;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 棋群的相对健康值分析器
 *
 * @author Barry Becker
 */
class RelativeHealthCalculator {

    private IGoGroup mGroup;

    private GroupAnalyzerMap mAnalyzerMap;

    public RelativeHealthCalculator(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        mGroup = group;
        mAnalyzerMap = analyzerMap;
    }

    /**
     * 计算棋群的相对健康值
     * <p>
     * 这个方法调用前，需要确保所有棋群已经计算过绝对健康值
     * 相对健康值会考虑棋群周围敌方棋群的相对强度，如果敌方棋群比己方棋群更弱，则己方棋群分数会得到提升
     * <p>
     * http://senseis.xmp.net/?BensonsAlgorithm
     *
     * @return the overall health of the group.
     */
    public float calculateRelativeHealth(GoBoard board, float absoluteHealth) {
        return boostRelativeHealthBasedOnWeakNbr(board, absoluteHealth);
    }

    private float boostRelativeHealthBasedOnWeakNbr(GoBoard board, float absoluteHealth) {
        // the default if there is no weakest group.
        float relativeHealth = absoluteHealth;
        GoBoardPositionSet groupStones = mGroup.getStones();
        WeakestGroupFinder finder = new WeakestGroupFinder(board, mAnalyzerMap);
        GoGroup weakestGroup = finder.findWeakestGroup(groupStones);

        if (weakestGroup != null) {
            double proportionWithEnemyNbrs = findProportionWithEnemyNbrs(groupStones);

            double diff = absoluteHealth + mAnalyzerMap.getAnalyzer(weakestGroup).getAbsoluteHealth();
            // @@ should use a weight to help determine how much to give a boost.

            // must be bounded by -1 and 1
            relativeHealth =
                    (float) (Math.min(1.0, Math.max(-1.0, absoluteHealth + diff * proportionWithEnemyNbrs)));
        }
        groupStones.unvisitPositions();
        return relativeHealth;
    }

    /**
     * 查找棋群棋子接近敌对棋群的比例，这将告诉我们棋群是否已经被包围
     * <p>
     * 如果被包围，将会得到一个大的提升
     * 如果没被包围，将会得到一个小的提升，因为被包围的棋子可以通过其他方式进行逃跑
     *
     * @param groupStones
     * @return
     */
    private double findProportionWithEnemyNbrs(GoBoardPositionSet groupStones) {
        int numWithEnemyNbrs = 0;
        for (Object p : groupStones) {
            GoBoardPosition stone = (GoBoardPosition) p;
            if (stone.isVisited()) {
                numWithEnemyNbrs++;
                stone.setVisited(false); // clear the visited state.
            }
        }
        return (double) numWithEnemyNbrs / ((double) groupStones.size() + 2);
    }
}