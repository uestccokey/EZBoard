/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.position;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A list of GoBoardPositions.
 *
 * @author Barry Becker
 */
public class GoBoardPositionList extends LinkedList<GoBoardPosition> {

    /**
     * Default constructor.
     */
    public GoBoardPositionList() {}

    /**
     * copy constructor.
     *
     * @param positionList list to initialize with
     */
    public GoBoardPositionList(GoBoardPositionList positionList) {
        super(positionList);
    }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     *
     * @return string form of list of stones.
     */
    public String toString(String title) {
        StringBuilder buf = new StringBuilder(title);
        buf.append("\n  ");
        for (GoBoardPosition stone : this) {
            buf.append(stone.toString()).append(", ");
        }
        return buf.substring(0, buf.length() - 2);
    }

    @Override
    public GoBoardPosition getFirst() {
        return super.getFirst();
    }

    @Override
    public GoBoardPosition get(int i) {
        return super.get(i);
    }

    /**
     * Set the visited flag back to false for a set of stones.
     * Returns the whole list of stones to unvisited state.
     */
    public void unvisitPositions() {
        for (GoBoardPosition position : this) {
            position.setVisited(false);
        }
    }

    // --- internal consistency checks ----

    /**
     * Verify all stones in this list are marked unvisited.
     */
    public void confirmUnvisited() {
        for (GoBoardPosition pos : this) {
            assert !pos.isVisited() : pos + " in " + this + " was visited";
        }
    }

    /**
     * Verify no duplicate positions in this list
     *
     * @param seed stone to start checking from .
     */
    public void confirmNoDupes(GoBoardPosition seed) {
        Object[] stoneArray = this.toArray();

        for (int i = 0; i < stoneArray.length; i++) {
            GoBoardPosition st = (GoBoardPosition) stoneArray[i];
            // make sure that this stone is not a dupe of another in the list
            for (int j = i + 1; j < stoneArray.length; j++) {
                assert (!st.equals(stoneArray[j])) : "found a dupe=" + st + " in " + this + "]n the seed = " + seed;
            }
        }
    }

    /**
     * Confirm that this list contains some smaller list
     *
     * @param smallerGroup smaller group
     * @return true if larger group contains smaller group.
     */
    public boolean confirmStoneListContains(GoBoardPositionList smallerGroup) {
        for (GoBoardPosition smallPos : smallerGroup) {
            boolean found = false;
            Iterator largeIt = this.iterator();
            while (largeIt.hasNext() && !found) {
                GoBoardPosition largePos = (GoBoardPosition) largeIt.next();
                if (largePos.getRow() == smallPos.getRow() && largePos.getCol() == smallPos.getCol())
                    found = true;
            }
            if (!found)
                return false;
        }
        return true;
    }
}