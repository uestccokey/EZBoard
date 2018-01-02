/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.elements.group;

import com.ezandroid.board.go.board.elements.IGoSet;
import com.ezandroid.board.go.board.elements.position.GoBoardPosition;
import com.ezandroid.board.go.board.elements.position.GoBoardPositionSet;
import com.ezandroid.board.go.board.elements.string.GoStringSet;
import com.ezandroid.board.go.board.elements.string.IGoString;
import com.ezandroid.common.geometry.Box;

/**
 * Makes some unit tests much simpler if we create the tests to use this interface instead
 * of the full-blown GoGroup class.
 *
 * @author Barry Becker
 */
public interface IGoGroup extends IGoSet {

    void addMember(IGoString string);

    @Override
    GoStringSet getMembers();

    @Override
    boolean isOwnedByPlayer1();

    void addChangeListener(GroupChangeListener listener);

    int getNumStones();

    boolean containsStone(GoBoardPosition stone);

    void remove(IGoString string);

    GoBoardPositionSet getStones();

    void updateTerritory(float health);

    Box findBoundingBox();

    String toHtml();
}