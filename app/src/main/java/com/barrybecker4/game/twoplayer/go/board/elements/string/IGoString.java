/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.elements.string;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.IGoSet;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

/**
 * Makes some unit tests much simpler if we create the tests to use this interface instead
 * of the full-blown GoString or GoEye class.
 *
 * @author Barry Becker
 */
public interface IGoString extends IGoSet {

    /**
     * @return set of member positions.
     */
    @Override
    GoBoardPositionSet getMembers();

    /**
     * @return the group that this string belongs to.
     */
    IGoGroup getGroup();

    /**
     * @param pos position to test for inclusion in this string
     * @return true if the specified position is a member of this string.
     */
    boolean contains(GoBoardPosition pos);

    /**
     * @return true if this string is unconditionally alive according to Benson's algorithm.
     */
    boolean isUnconditionallyAlive();

    void setUnconditionallyAlive(boolean unconditionallyAlive);

    void remove(GoBoardPosition stone, GoBoard board);

    void updateTerritory(float health);

    void setGroup(IGoGroup group);
}
