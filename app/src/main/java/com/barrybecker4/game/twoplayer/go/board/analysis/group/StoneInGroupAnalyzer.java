/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;

/**
 * Analyze the strength of stone relative to the group that it is in.
 *
 * @author Barry Becker
 */
public class StoneInGroupAnalyzer {

    /**
     * an opponent stone must be at least this much more unhealthy to be considered part of an eye.
     * if its not that much weaker then we don't really have an eye.
     *
     * @@ make this a game parameter .6 - 1.8 that can be optimized.
     */
    private static final float DIFFERENCE_THRESHOLD = 0.6f;

    /** used to determine if a stone is dead or alive. */
    private static final float MIN_LIFE_THRESH = 0.2f;

    /** The group of go stones that we are analyzing. */
    private IGoGroup group_;

    /**
     * Constructor.
     *
     * @param group group to analyze.
     */
    public StoneInGroupAnalyzer(IGoGroup group) {
        group_ = group;
    }

    /**
     * @return true if the stone is much weaker than the group
     */
    public boolean isStoneMuchWeakerThanGroup(GoStone stone, float absoluteHealth) {
        return isStoneWeakerThanGroup(stone, DIFFERENCE_THRESHOLD, absoluteHealth);
    }

    /**
     * @return return true of the stone is greater than threshold weaker than the group.
     */
    private boolean isStoneWeakerThanGroup(GoStone stone, float threshold, float groupHealth) {
        float constrainedGroupHealth = getConstrainedGroupHealth(groupHealth);

        float stoneHealth = stone.getHealth();
        boolean muchWeaker;
        if (stone.isOwnedByPlayer1()) {
            assert (!group_.isOwnedByPlayer1());

            muchWeaker = (-constrainedGroupHealth - stoneHealth > threshold);
        } else {
            assert (group_.isOwnedByPlayer1());
            muchWeaker = (constrainedGroupHealth + stoneHealth > threshold);
        }

        return muchWeaker;
    }

    /**
     * for purposes of determining relative weakness. Don't allow the outer group to go out of its living range.
     *
     * @param rawGroupHealth original group health value
     * @return group health constrained to between +/- MIN_LIFE_THRESH
     */
    private float getConstrainedGroupHealth(float rawGroupHealth) {
        float health = rawGroupHealth;
        if (group_.isOwnedByPlayer1() && rawGroupHealth < MIN_LIFE_THRESH) {
            health = MIN_LIFE_THRESH;
        } else if (!group_.isOwnedByPlayer1() && rawGroupHealth > -MIN_LIFE_THRESH) {
            health = -MIN_LIFE_THRESH;
        }
        return health;
    }
}
