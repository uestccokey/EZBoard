/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential.EyePotentialAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;

/**
 * Keep a cache of the eyes in a group because its expensive to recompute them.
 *
 * @author Barry Becker
 */
public class GroupEyeCache {

    /**
     * need 2 true eyes to be unconditionally alive.
     * This is a set of GoEyes which give the spaces in the eye.
     * It includes eyes of all types including false eyes.
     * false-eye: any string of spaces or dead enemy stones for which one is a false eye.
     */
    private GoEyeSet eyes_;

    /** measure of how easily the group can make 2 eyes. */
    private float eyePotential_;

    private EyeSpaceAnalyzer eyeAnalyzer_;
    private EyePotentialAnalyzer potentialAnalyzer_;

    /**
     * Set this to true when the eyes need to be recalculated.
     * It must be set to true if the group has changed in any way.
     */
    private boolean isValid_ = false;

    /**
     * Constructor
     */
    public GroupEyeCache(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        eyes_ = new GoEyeSet();
        isValid_ = false;
        eyeAnalyzer_ = new EyeSpaceAnalyzer(group, analyzerMap);
        potentialAnalyzer_ = new EyePotentialAnalyzer(group, analyzerMap);
    }

    /**
     * @return set of eyes currently identified for this group.
     */
    public GoEyeSet getEyes(GoBoard board) {
        updateEyes(board);
        return eyes_;
    }

    /**
     * Compute how many eyes (connected internal blank areas) this group has.
     * The eyes are either false eyes or true (or big or territorial) eyes.
     * Also update eyePotential (a measure of how good the groups ability to make 2 eyes.
     * This method is expensive. That is why the 2 things it computes (eyes and eyePotential) are cached
     * on a per group basis. After this method runs, the cache is valid until something about the group changes.
     *
     * @param board the game board. If null, we just use what is in the cache.
     */
    public void updateEyes(GoBoard board) {
        if (isValid_ || board == null) {
            return;
        }

        eyeAnalyzer_.setBoard(board);
        potentialAnalyzer_.setBoard(board);
        eyes_ = eyeAnalyzer_.determineEyes();
        eyePotential_ = potentialAnalyzer_.calculateEyePotential();
        isValid_ = true;
    }

    public void invalidate() {
        clearEyes();
    }

    /**
     * @return true if the group has changed (structurally) in any way.
     */
    public boolean isValid() {
        return isValid_;
    }

    /**
     * used only for test.
     *
     * @return eye potential value
     */
    public float getEyePotential() {
        // assert isValid_;  causes tests to fail.
        return eyePotential_;
    }

    /**
     * Determine approximately how many eyes the group has.
     * This is purposely a little vague, but if more than 2.0, then must be unconditionally alive.
     * The value that we count for each type of eye could be optimized.
     *
     * @return approximation to the number of eyes in group.
     */
    public float calcNumEyes() {
        float numEyes = 0;
        for (IGoEye eye : eyes_) {
            numEyes += eye.getStatus().getScore();
        }
        return numEyes;
    }

    /**
     * clear the current eyes for the group (in preparation for recomputing them).
     */
    private void clearEyes() {
        if (eyes_.isEmpty()) return;

        for (IGoEye eye : eyes_) {
            eye.clear();
        }
        eyes_.clear();
        isValid_ = false;
    }
}