/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

/**
 * Determine the absolute health of a group independent of the health of neighboring groups.
 *
 * @author Barry Becker
 */
class RelativeHealthCalculator {

    /** The group of go stones that we are analyzing. */
    private IGoGroup group_;

    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor
     *
     * @param group the group to analyze
     */
    public RelativeHealthCalculator(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        group_ = group;
        analyzerMap_ = analyzerMap;
    }

    /**
     * Calculate the relative health of a group.
     * This method must be called only after calculateAbsoluteHealth has be done for all groups.
     * Good health is positive for a black group.
     * This measure of the group's health should be much more accurate than the absolute health
     * because it takes into account the relative health of neighboring groups.
     * If the health of an opponent bordering group is in worse shape
     * than our own then we get a boost since we can probably kill that group first.
     *
     * @return the overall health of the group.
     */
    public float calculateRelativeHealth(GoBoard board, float absoluteHealth) {
        return boostRelativeHealthBasedOnWeakNbr(board, absoluteHealth);
    }

    /**
     * If there is a weakest group, then boost ourselves relative to it.
     * it may be a positive or negative boost to our health depending on its relative strength.
     *
     * @return relative health
     */
    private float boostRelativeHealthBasedOnWeakNbr(GoBoard board, float absoluteHealth) {

        // the default if there is no weakest group.
        float relativeHealth = absoluteHealth;
        GoBoardPositionSet groupStones = group_.getStones();
        WeakestGroupFinder finder = new WeakestGroupFinder(board, analyzerMap_);
        GoGroup weakestGroup = finder.findWeakestGroup(groupStones);

        if (weakestGroup != null) {
            double proportionWithEnemyNbrs = findProportionWithEnemyNbrs(groupStones);

            double diff = absoluteHealth + analyzerMap_.getAnalyzer(weakestGroup).getAbsoluteHealth();
            // @@ should use a weight to help determine how much to give a boost.

            // must be bounded by -1 and 1
            relativeHealth =
                    (float) (Math.min(1.0, Math.max(-1.0, absoluteHealth + diff * proportionWithEnemyNbrs)));
        }
        groupStones.unvisitPositions();
        return relativeHealth;
    }

    /**
     * What proportion of the groups stones are close to enemy groups?
     * this gives us an indication of how surrounded we are.
     * If we are very surrounded then we give a big boost for being stronger or weaker than a nbr.
     * If we are not very surrounded then we don't give much of a boost because there are other
     * ways to make life (i.e. run out/away).
     *
     * @return proportion of our group stones with enemy neighbors.
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