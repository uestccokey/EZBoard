/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy;

/**
 * Enum for how we want to evaluate moves.
 *
 * @author Barry Becker
 */
public enum EvaluationPerspective {

    /**
     * Always evaluate from player ones point of view.
     * This is done for minimax for example.
     */
    ALWAYS_PLAYER1,

    /**
     * At a given level, evaluate the potential moves according to the player who's turn it is.
     */
    CURRENT_PLAYER
}
