/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.GoEyeSet;
import cn.ezandroid.game.board.go.elements.group.GoGroupChangeListener;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * Analyzes a group to determine how alive it is, and also find other properties like eyes and liberties.
 *
 * @author Barry Becker
 */
public class GroupAnalyzer implements GoGroupChangeListener {

    /** The group of go stones that we are analyzing. */
    private IGoGroup group_;

    private StoneInGroupAnalyzer stoneInGroupAnalyzer_;

    /**
     * This measure of health is also between -1 and 1 but it should be more
     * accurate because it takes into account the health of neighboring enemy groups as well.
     * it uses the absolute health as a base and exaggerates it base on the relative strength of the
     * weakest enemy nbr group.
     */
    private float relativeHealth_;

    private AbsoluteHealthCalculator absHealthCalculator_;

    /** cached absolute health to avoid needless recalculation. */
    private float absoluteHealth_;

    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor.
     *
     * @param group group to analyze.
     */
    public GroupAnalyzer(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        group_ = group;
        analyzerMap_ = analyzerMap;
        absHealthCalculator_ = new AbsoluteHealthCalculator(group, analyzerMap_);
        stoneInGroupAnalyzer_ = new StoneInGroupAnalyzer(group);
        group.addChangeListener(this);
    }

    public IGoGroup getGroup() {
        return group_;
    }

    /**
     * Called when the group we are maintaining info about changes.
     * It changes by having stones added or removed.
     */
    public void onGoGroupChanged() {
        invalidate();
    }

    /**
     * @return health score independent of neighboring groups.
     */
    public float getAbsoluteHealth() {
        return absoluteHealth_;
    }

    /**
     * We try to use the cached relative health value if we can.
     *
     * @param board          needed to calculate new value if not cached
     * @param useCachedValue if true, just return the cached value instead of checking for validity.
     *                       Only do this if you are sure the value returned does not have to be perfectly accurate.
     * @return relative health
     */
    public float getRelativeHealth(GoBoard board, boolean useCachedValue) {
        if (isValid() || useCachedValue) {
            if (!isValid())
                GameContext.log(3, "using cached relative health when not valid");
            return getRelativeHealth();
        }
        GameContext.log(0, "stale abs health. recalculating relative health");
        return calculateRelativeHealth(board);
    }

    /**
     * @return health score dependent on strength of neighboring groups.
     */
    float getRelativeHealth() {
        return relativeHealth_;
    }

    public void invalidate() {
        absHealthCalculator_.invalidate();
    }

    /**
     * @return true if the group has changed (structurally) in any way.
     */
    public boolean isValid() {
        return absHealthCalculator_.isValid();
    }

    /**
     * If nothing cached, this may not be accurate.
     *
     * @return number of cached liberties.
     */
    public int getNumLiberties(GoBoard board) {
        return group_.getNumLiberties(board);
    }

    /**
     * @return set of eyes currently identified for this group.
     */
    public GoEyeSet getEyes(GoBoard board) {
        return absHealthCalculator_.getEyes(board);
    }

    public float calculateAbsoluteHealth(GoBoard board) {
        absoluteHealth_ = absHealthCalculator_.calculateAbsoluteHealth(board);
        return absoluteHealth_;
    }

    /**
     * @return true if the piece is an enemy of the set owner.
     * If the difference in health between the stones is great, then they are not really enemies
     * because one of them is dead.
     */
    public boolean isTrueEnemy(GoBoardPosition pos) {
        assert (pos.isOccupied());
        GoStone stone = (GoStone) pos.getPiece();
        boolean muchWeaker = isStoneMuchWeakerThanGroup(stone);

        return (stone.isOwnedByPlayer1() != group_.isOwnedByPlayer1() && !muchWeaker);
    }

    /**
     * used only for test. Remove when tested through AbsoluteGroupHealthCalc
     *
     * @return eye potential
     */
    public float getEyePotential() {
        return absHealthCalculator_.getEyePotential();
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
    public float calculateRelativeHealth(GoBoard board) {
        if (!isValid()) {
            calculateAbsoluteHealth(board);
        }

        RelativeHealthCalculator relativeCalculator = new RelativeHealthCalculator(group_, analyzerMap_);
        relativeHealth_ = relativeCalculator.calculateRelativeHealth(board, absoluteHealth_);

        return relativeHealth_;
    }

    /**
     * @return a deep copy of this GroupAnalyzer
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        ((GroupAnalyzer) clone).absHealthCalculator_ = new AbsoluteHealthCalculator(group_, analyzerMap_);
        return clone;
    }

    /**
     * @return true if the stone is much weaker than the group
     */
    private boolean isStoneMuchWeakerThanGroup(GoStone stone) {
        return stoneInGroupAnalyzer_.isStoneMuchWeakerThanGroup(stone, getAbsoluteHealth());
    }
}
