/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor;

/**
 * Enum for the different possible Neighbor types.
 * These constants represent the types of possible neighbors that can be searched for.
 *
 * @author Barry Becker
 */
public enum NeighborType {

    /** Has a stone in the space */
    OCCUPIED,

    /** No stone at the nbr position. */
    UNOCCUPIED,

    /** nbr stone same color as current stone. */
    FRIEND,

    /** nbr stone enemy of current stone. */
    ENEMY,

    /** Enemy or unoccupied. */
    NOT_FRIEND,

    /** any kind of nbr. */
    ANY
}

