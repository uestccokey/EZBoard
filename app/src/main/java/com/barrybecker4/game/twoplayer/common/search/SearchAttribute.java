/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search;

/**
 * The different search strategies may have zero or more of the following attributes to
 * describe what options they support.
 *
 * @author Barry Becker
 */
public enum SearchAttribute {

    /** traditional strategies like minimax, negamax, and negascout use this. */
    BRUTE_FORCE,

    /** for strategies that use a transposition table to remember past results. */
    MEMORY,

    /** Narrow test window is used. Like aspiration, negascout, mtd strategies. */
    ASPIRATION,

    /** Stochastic in nature, like UCT. */
    MONTE_CARLO
}
