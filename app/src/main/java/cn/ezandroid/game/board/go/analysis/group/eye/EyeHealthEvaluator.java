/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group.eye;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.analysis.group.LifeAnalyzer;

/**
 * Evaluate the health of a group based on the status of its eye(s).
 *
 * @author Barry Becker
 */
public class EyeHealthEvaluator {

    private LifeAnalyzer lifeAnalyzer_;

    private static final float BEST_TWO_EYED_HEALTH = 1.0f;
    private static final float BEST_ALMOST_TWO_EYED_HEALTH = 0.94f;
    private static final float BEST_ONE_EYED_HEALTH = 0.89f;

    /**
     * Constructor
     */
    public EyeHealthEvaluator(LifeAnalyzer lifeAnalyzer) {
        lifeAnalyzer_ = lifeAnalyzer;
    }

    /**
     * @return the health of the group based on the number of eyes and the number of liberties.
     */
    public float determineHealth(float side, float numEyes, int numLiberties, int numStones) {
        float health;

        if (numEyes >= 2.0) {
            health = calcTwoEyedHealth(side);
        } else if (numEyes >= 1.5) {
            health = calcAlmostTwoEyedHealth(side, numLiberties);
        } else if (numEyes >= 1.0) {
            health = calcOneEyedHealth(side, numLiberties);
        } else {
            health = calcNoEyeHealth(side, numLiberties, numStones);
        }
        return health;
    }

    /**
     * @return the health of a group that has 2 eyes.
     */
    private float calcTwoEyedHealth(float side) {
        float health;

        if (lifeAnalyzer_.isUnconditionallyAlive()) {
            // in addition to this, the individual strings will get a score of side (ie +/- 1).
            health = BEST_TWO_EYED_HEALTH * side;
        } else {
            // its probably alive
            // may not be alive if the opponent has a lot of kos and gets to play lots of times in a row
            health = BEST_ALMOST_TWO_EYED_HEALTH * side;
        }
        return health;
    }

    /**
     * @return the health of a group that has only one eye.
     */
    private float calcAlmostTwoEyedHealth(float side, int numLiberties) {
        float health = 0;
        if (numLiberties > 6) {
            health = side * Math.min(BEST_ALMOST_TWO_EYED_HEALTH, (1.15f - 20.0f / (numLiberties + 23.0f)));
        } else {  // numLiberties<=5. Very unlikely to occur
            switch (numLiberties) {
                case 0:
                case 1:
                    // assert false: "can't have almost 2 eyes and only 1 or fewer liberties! " + this.toString();
                    // but apparently it can (seen on 5x5 game):
                    //  OOOOX
                    //      XOX
                    break;
                case 2:
                    health = side * 0.02f;
                    // this actually happens quite often.
                    GameContext.log(1, "We have almost 2 eyes but only 2 Liberties. How can that be? " + this.toString());
                    break;
                case 3:
                    health = side * 0.05f;
                    break;
                case 4:
                    health = side * 0.1f;
                    break;
                case 5:
                    health = side * 0.19f;
                    break;
                case 6:
                    health = side * 0.29f;
                    break;
                default:
                    assert false;
            }
        }
        return health;
    }

    /**
     * @return the health of a group that has only one eye.
     */
    private static float calcOneEyedHealth(float side, int numLiberties) {
        float health = 0;
        if (numLiberties > 6) {
            health = side * Math.min(BEST_ONE_EYED_HEALTH, (1.03f - 20.0f / (numLiberties + 20.0f)));
        } else {  // numLiberties<=5
            switch (numLiberties) {
                case 0:
                    // this can't happen because the stone should already be captured.
                    assert false : "can't have 1 eye and no liberties!";
                    break;
                case 1:
                    // @@ we need to consider a seki here.
                    // what if the neighboring enemy group also has one or zero eyes?
                    // one eye beats no eyes.
                    health = -side * 0.8f;
                    break;
                case 2:
                    health = -side * 0.3f;
                    break;
                case 3:
                    health = -side * 0.1f;
                    break;
                case 4:
                    health = side * 0.01f;
                    break;
                case 5:
                    health = side * 0.05f;
                    break;
                case 6:
                    health = side * 0.19f;
                    break;
                default:
                    assert false;
            }
        }
        return health;
    }

    /**
     * @return the health of a group that has no eyes.
     */
    private float calcNoEyeHealth(float side, int numLiberties, int numStones) {

        if (numLiberties > 5) {
            return side * Math.min(0.8f, (1.2f - 46.0f / (numLiberties + 40.0f)));
        } else if (numStones == 1) {
            return calcSingleStoneHealth(side, numLiberties);
        } else {
            return calcMultiStoneHealth(side, numLiberties);
        }
    }

    /**
     * @return health of a single stone based on number of liberties it has.
     */
    private float calcSingleStoneHealth(float side, int numLiberties) {
        float health = 0;
        switch (numLiberties) { // numEyes == 0
            case 0:
                // this can't happen because the stone should already be captured.
                assert false : "can't have no liberties and still be on the board! " + this;
                health = -side;
                break;
            case 1:
                health = -side * 0.7f;
                break;
            case 2:
                // @@ consider seki situations where the adjacent enemy group also has no eyes.
                //      XXXXXXX     example of seki here.
                //    XXooooooX
                //    Xo.XXX.oX
                //    XooooooXX
                //    XXXXXXX
                health = side * 0.02f;
                break;
            case 3:
                health = side * 0.1f;
                break;
            case 4:
                health = side * 0.1f;
                break;
            default:
                assert false : "there were too many liberties for a single stone: " + numLiberties;
        }
        return health;
    }

    /*
     * @return health for multi-stone group with less than 5 liberties.
     */
    private float calcMultiStoneHealth(float side, int numLiberties) {
        float health = 0;
        switch (numLiberties) { // numEyes == 0
            case 0:
                // this can't happen because the stone should already be captured.
                //assert false : "can't have no liberties and still be on the board! "+ this;
                health = -side;
                break;
            case 1:
                health = -side * 0.6f;
                break;
            case 2:
                // @@ consider seki situations where the adjacent enemy group also has no eyes.
                //      XXXXXXX     example of seki here.
                //    XXooooooX
                //    Xo.XXX.oX
                //    XooooooXX
                //    XXXXXXX
                health = -side * 0.3f;
                break;
            case 3:
                health = side * 0.02f;
                break;
            case 4:
                health = side * 0.05f;
                break;
            case 5:
                health = side * 0.1f;
                break;
            default:
                assert false : "We should have already covered the case of >5 liberties";
        }
        return health;
    }
}