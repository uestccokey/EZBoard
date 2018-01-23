/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.eye.EyeHealthEvaluator;
import cn.ezandroid.game.board.go.analysis.group.eye.GroupEyeCache;
import cn.ezandroid.game.board.go.elements.eye.GoEyeSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 棋群的绝对健康值分析器
 *
 * @author Barry Becker
 */
class AbsoluteHealthCalculator {

    private IGoGroup mGroup;

    /**
     * This is a number between -1 and 1 that indicates how likely the group is to live
     * independent of the health of the stones around it.
     * all kinds of factors can contribute to the health of a group.
     * Local search should be used to make this as accurate as possible.
     * If the health is 1.0 then the group has at least 2 eyes and is unconditionally alive.
     * If the health is -1.0 then there is no way to save the group even if you could
     * play 2 times in a row.
     * Unconditional life means the group cannot be killed no matter how many times the opponent plays.
     * A score of near 0 indicates it is very uncertain whether the group will live or die.
     */
    private float absoluteHealth_ = 0;

    /** Number of stones in the group. */
    private int cachedNumStonesInGroup_;

    /** Maintains cache of this groups eyes. */
    private GroupEyeCache mEyeCache;

    private GroupAnalyzerMap mAnalyzerMap;

    public AbsoluteHealthCalculator(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        mGroup = group;
        mAnalyzerMap = analyzerMap;
        mEyeCache = new GroupEyeCache(group, mAnalyzerMap);
    }

    /**
     * @return false if the group has changed (structurally) in any way.
     */
    public boolean isValid() {
        return mEyeCache.isValid();
    }

    /** for st the eyeCache to be cleared. */
    public void invalidate() {
        mEyeCache.invalidate();
    }

    /**
     * used only for test.
     *
     * @return eye potential
     */
    public float getEyePotential() {
        return mEyeCache.getEyePotential();
    }

    /**
     * Calculate the absolute health of a group.
     * All the stones in the group have the same health rating because the
     * group lives or dies as a unit.
     * (not entirely true - strings live or die as unit, but there is a relationship).
     * Good health of a black group is positive; white, negative.
     * The health is a function of the number of eyes (their type and status), liberties, and
     * the health of surrounding groups. If the health of an opponent bordering group
     * is in worse shape than our own then we get a boost since we can probably
     * kill that group first. See RelativeHealthCalculator.calculateRelativeHealth.
     * A perfect 1 (or -1) indicates unconditional life (or death).
     * This means that the group cannot be killed (or given life) no matter
     * how many times the opponent plays (see Dave Benson 1977).
     * http://senseis.xmp.net/?BensonsAlgorithm
     *
     * @return the overall health of the group independent of nbr groups.
     */
    public float calculateAbsoluteHealth(GoBoard board) {
        if (mEyeCache.isValid()) {
            GameContext.log(1, "cache valid. Returning health=" + absoluteHealth_);
            return absoluteHealth_;
        }

        int numLiberties = mGroup.getNumLiberties(board);

        // we multiply by a +/- sign depending on the side
        float side = mGroup.isOwnedByPlayer1() ? 1.0f : -1.0f;

        // first come up with some approximation for the health so update eyes can be done more accurately.
        float numEyes = mEyeCache.calcNumEyes();
        int numStones = mGroup.getNumStones();
        LifeAnalyzer lifeAnalyzer = new LifeAnalyzer(mGroup, board, mAnalyzerMap);
        EyeHealthEvaluator eyeEvaluator = new EyeHealthEvaluator(lifeAnalyzer);

        absoluteHealth_ = eyeEvaluator.determineHealth(side, numEyes, numLiberties, numStones);

        mEyeCache.updateEyes(board);  // expensive

        float eyePotential = mEyeCache.getEyePotential();
        float revisedNumEyes = mEyeCache.calcNumEyes();
        numEyes = Math.max(eyePotential, revisedNumEyes);

        // health based on eye shape - the most significant factor
        float health = eyeEvaluator.determineHealth(side, numEyes, numLiberties, numStones);

        // No bonus at all for false eyes
        absoluteHealth_ = health;
        if (Math.abs(absoluteHealth_) > 1.0) {
            GameContext.log(0, "Warning: health exceeded 1.0: " + " health=" + health + " numEyes=" + numEyes);
            absoluteHealth_ = side;
        }

        return absoluteHealth_;
    }

    /**
     * @return set of eyes currently identified for this group.
     */
    public GoEyeSet getEyes(GoBoard board) {
        if (!mEyeCache.isValid()) {
            calculateAbsoluteHealth(board);
        }
        return mEyeCache.getEyes(board);
    }

    /**
     * Calculate the number of stones in the group.
     *
     * @return number of stones in the group.
     */
    public int getNumStones() {
        if (mEyeCache.isValid()) {
            return cachedNumStonesInGroup_;
        }
        int numStones = 0;
        for (IGoString str : mGroup.getMembers()) {
            numStones += str.size();
        }
        cachedNumStonesInGroup_ = numStones;
        return numStones;
    }
}
