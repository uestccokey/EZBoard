/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.elements.position;

import java.util.LinkedList;

/**
 * A list of GoBoardPositionLists.
 *
 * @author Barry Becker
 */
public class GoBoardPositionLists extends LinkedList<GoBoardPositionList> {

    /**
     * Default constructor.
     */
    public GoBoardPositionLists() {}

    /**
     * copy constructor.
     *
     * @param positionList list to initialize with
     */
    public GoBoardPositionLists(GoBoardPositionLists positionList) {
        super(positionList);
    }

    @Override
    public GoBoardPositionList get(int i) {
        return super.get(i);
    }

    /**
     * Set the visited flag back to false for a list of lists of stones
     */
    public void unvisitPositionsInLists() {
        for (GoBoardPositionList list : this) {
            list.unvisitPositions();
        }
    }
}