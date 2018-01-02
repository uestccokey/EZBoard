/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.analysis.group;

import com.ezandroid.board.common.GameContext;
import com.ezandroid.board.go.board.GoBoard;
import com.ezandroid.board.go.board.GoProfiler;
import com.ezandroid.board.go.board.analysis.group.eye.EyeHealthEvaluator;
import com.ezandroid.board.go.board.analysis.group.eye.GroupEyeCache;
import com.ezandroid.board.go.board.elements.eye.GoEyeSet;
import com.ezandroid.board.go.board.elements.group.IGoGroup;
import com.ezandroid.board.go.board.elements.string.IGoString;

/**
 * Determine the absolute health of a group independent of the health of neighboring groups.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
class AbsoluteHealthCalculator {

    /** The group of go stones that we are analyzing. */
    private IGoGroup group_;

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
    private GroupEyeCache eyeCache_;

    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor
     *
     * @param group the group to analyze
     */
    public AbsoluteHealthCalculator(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        group_ = group;
        analyzerMap_ = analyzerMap;
        eyeCache_ = new GroupEyeCache(group, analyzerMap_);
    }

    /**
     * @return false if the group has changed (structurally) in any way.
     */
    public boolean isValid() {
        return eyeCache_.isValid();
    }

    /** for st the eyeCache to be cleared. */
    public void invalidate() {
        eyeCache_.invalidate();
    }

    /**
     * used only for test.
     *
     * @return eye potential
     */
    public float getEyePotential() {
        return eyeCache_.getEyePotential();
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
        if (eyeCache_.isValid()) {
            GameContext.log(1, "cache valid. Returning health=" + absoluteHealth_);
            return absoluteHealth_;
        }

        int numLiberties = group_.getNumLiberties(board);

        // we multiply by a +/- sign depending on the side
        float side = group_.isOwnedByPlayer1() ? 1.0f : -1.0f;

        // first come up with some approximation for the health so update eyes can be done more accurately.
        float numEyes = eyeCache_.calcNumEyes();
        int numStones = group_.getNumStones();
        LifeAnalyzer lifeAnalyzer = new LifeAnalyzer(group_, board, analyzerMap_);
        EyeHealthEvaluator eyeEvaluator = new EyeHealthEvaluator(lifeAnalyzer);

        absoluteHealth_ = eyeEvaluator.determineHealth(side, numEyes, numLiberties, numStones);

        GoProfiler.getInstance().startUpdateEyes();
        eyeCache_.updateEyes(board);  // expensive
        GoProfiler.getInstance().stopUpdateEyes();

        float eyePotential = eyeCache_.getEyePotential();
        float revisedNumEyes = eyeCache_.calcNumEyes();
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
        if (!eyeCache_.isValid()) {
            calculateAbsoluteHealth(board);
        }
        return eyeCache_.getEyes(board);
    }

    /**
     * Calculate the number of stones in the group.
     *
     * @return number of stones in the group.
     */
    public int getNumStones() {
        if (eyeCache_.isValid()) {
            return cachedNumStonesInGroup_;
        }
        int numStones = 0;
        for (IGoString str : group_.getMembers()) {
            numStones += str.size();
        }
        cachedNumStonesInGroup_ = numStones;
        return numStones;
    }
}
