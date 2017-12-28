/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

/**
 * Scores for various sorts of prototypical eye types.
 * 0 means dead. 1 means unconditionally alive.
 *
 * @author Barry Becker
 */
final class EyeShapeScores {
    /** any shape that have a false eye point. */
    public static final float FALSE_EYE = 0.19f;

    /** one or two points, or clump. */
    public static final float SINGLE_EYE = 1.0f;

    /** Can be made one eye if opponent plays first. */
    public static final float BIG_EYE = 1.2f;

    /** We are guaranteed to have 2 eyes. */
    public static final float GUARANTEED_TWO_EYES = 2.0f;


    private EyeShapeScores() {}
}