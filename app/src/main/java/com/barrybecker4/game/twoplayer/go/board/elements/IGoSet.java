/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.elements;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.Set;

/**
 * A collection of GoMembers of some type (e.g. strings, stones, groups, etc).
 *
 * @author Barry Becker
 */
public interface IGoSet extends IGoMember {

    /**
     * @return the Set containing the members
     */
    Set<? extends IGoMember> getMembers();

    /**
     * @return true if pos is our enemy.
     */
    boolean isEnemy(GoBoardPosition pos);

    /**
     * @return true if owned by player 1 (i.e. black).
     */
    boolean isOwnedByPlayer1();

    /**
     * Mark all members unvisited.
     *
     * @param visited whether or not the members should be marked visited or unvisited.
     */
    void setVisited(boolean visited);

    /**
     * @return the number of elements in the set
     */
    int size();

    /**
     * Get the number of liberties (open surrounding spaces)
     *
     * @param board go board
     * @return the liberties/positions for the set.
     */
    GoBoardPositionSet getLiberties(GoBoard board);

    /** @return the number of liberties. */
    int getNumLiberties(GoBoard board);
}