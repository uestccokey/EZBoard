/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.group;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.GoSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.position.GoStone;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;
import cn.ezandroid.game.common.geometry.Box;

/**
 * A GoGroup is composed of a loosely connected set of one or more same color strings.
 * A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 * Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi.
 *
 * @author Barry Becker
 */

public final class GoGroup extends GoSet
        implements IGoGroup {

    /** a set of same color strings that are in the group. */
    private GoStringSet members_;

    /**
     * This is the cached number of liberties.
     * It updates whenever something has changed.
     */
    private GoBoardPositionSet cachedLiberties_;

    /** listeners to notify in the event that we change. */
    private List<GroupChangeListener> changeListeners;

    /**
     * Constructor. Create a new group containing the specified string.
     *
     * @param string make the group from this string.
     */
    public GoGroup(IGoString string) {
        ownedByPlayer1_ = string.isOwnedByPlayer1();

        getMembers().add(string);
        string.setGroup(this);
        commonInit();
    }

    /**
     * Constructor.
     * Create a new group containing the specified list of stones
     * Every stone in the list passed in must say that it is owned by this new group,
     * and every string must be wholly owned by this new group.
     *
     * @param stones list of stones to create a group from.
     */
    public GoGroup(GoBoardPositionList stones) {
        ownedByPlayer1_ = (stones.getFirst()).getPiece().isOwnedByPlayer1();
        for (GoBoardPosition stone : stones) {
            assimilateStone(stones, stone);
        }
        commonInit();
    }

    private void commonInit() {
        changeListeners = new LinkedList<>();
    }

    @Override
    public void addChangeListener(GroupChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(GroupChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * @return true if the piece is an enemy of the set owner.
     * If the difference in health between the stones is great, then they are not really enemies
     * because one of them is dead.
     */
    @Override
    public boolean isEnemy(GoBoardPosition pos) {
        assert (pos.isOccupied());
        GoStone stone = (GoStone) pos.getPiece();
        return stone.isOwnedByPlayer1() != isOwnedByPlayer1(); // && !muchWeaker);
    }

    /**
     * @param stones stones to assimilate
     * @param stone  the new stone to add to the group.
     */
    private void assimilateStone(GoBoardPositionList stones, GoBoardPosition stone) {
        assert stone.getPiece().isOwnedByPlayer1() == ownedByPlayer1_ :
                "Stones in group must all be owned by the same player. stones=" + stones;
        // actually this is ok - sometimes happens legitimately
        // assert isFalse(stone.isVisited(), stone+" is marked visited in "+stones+" when it should not be.");
        IGoString string = stone.getString();
        assert (string != null) : "There is no owning string for " + stone;
        if (!getMembers().contains(string)) {
            assert (ownedByPlayer1_ == string.isOwnedByPlayer1()) : string + "ownership not the same as " + this;
            //string.confirmOwnedByOnlyOnePlayer();
            getMembers().add(string);
        }
        string.setGroup(this);
    }

    /**
     * Must be ordered (i.e. LinkedHashSet
     */
    @Override
    protected void initializeMembers() {
        members_ = new GoStringSet();
    }

    /**
     * @return the hashSet containing the members
     */
    @Override
    public GoStringSet getMembers() {
        return members_;
    }

    /**
     * make sure all the stones in the string are unvisited or visited, as specified
     */
    @Override
    public void setVisited(boolean visited) {
        for (IGoString str : getMembers()) {
            str.setVisited(visited);
        }
    }

    /**
     * add a string to the group.
     *
     * @param string the string to add
     */
    @Override
    public void addMember(IGoString string) {
        assert (string.isOwnedByPlayer1() == ownedByPlayer1_) :
                "strings added to a group must have like ownership. String=" + string
                        + ". Group we are trying to add it to: " + this;
        if (getMembers().contains(string)) {
            assert (string.getGroup() == this) :
                    "The " + this + " already contains the string, but the " + string
                            + " says its owning group is " + string.getGroup();
            return;
        }
        // remove it from the old group
        IGoGroup oldGroup = string.getGroup();
        if (oldGroup != null && oldGroup != this) {
            oldGroup.remove(string);
        }
        string.setGroup(this);
        getMembers().add(string);
        broadcastChange();
    }

    /**
     * remove a string from this group
     *
     * @param string the string to remove from the group
     */
    @Override
    public void remove(IGoString string) {
        if (string == null) {
            GameContext.log(2, "attempting to remove " + string + " string from group. " + this);
            return;
        }
        if (getMembers().isEmpty()) {
            GameContext.log(2, "attempting to remove " + string + " from already empty group.");
            return;
        }
        getMembers().remove(string);
        broadcastChange();
    }

    /**
     * Get the number of liberties that the group has.
     *
     * @return the number of liberties that the group has
     */
    @Override
    public GoBoardPositionSet getLiberties(GoBoard board) {
        if (board == null) {
            return cachedLiberties_;
        }

        GoBoardPositionSet liberties = new GoBoardPositionSet();
        for (IGoString str : getMembers()) {
            liberties.addAll(str.getLiberties(board));
        }
        cachedLiberties_ = liberties;
        return liberties;
    }

    /**
     * Get number of liberties for our groups. If nothing cached, this may not be accurate.
     *
     * @param board if null, then the number of liberties returned is just what is in the cache and may not be accurate.
     * @return number of cached liberties if board is null, else exact number of liberties for group.
     */
    @Override
    public int getNumLiberties(GoBoard board) {
        return getLiberties(board).size();
    }

    /**
     * @return a list of the stones in this group.
     */
    @Override
    public GoBoardPositionSet getStones() {
        GoBoardPositionSet stones = new GoBoardPositionSet();
        for (IGoString string : getMembers()) {
            stones.addAll(string.getMembers());
        }
        return stones;
    }

    /**
     * Calculate the number of stones in the group.
     *
     * @return number of stones in the group.
     */
    @Override
    public int getNumStones() {
        return getStones().size();
    }

    /**
     * Set the health of strings in this group
     *
     * @param health the health of the group
     */
    @Override
    public void updateTerritory(float health) {
        for (IGoString string : getMembers()) {
            if (string.isUnconditionallyAlive()) {
                string.updateTerritory(ownedByPlayer1_ ? 1.0f : -1.0f);
            } else {
                string.updateTerritory(health);
            }
        }
    }

    /**
     * returns true if this group contains the specified stone
     *
     * @param stone the stone to check for containment of
     * @return true if the stone is in this group
     */
    @Override
    public boolean containsStone(GoBoardPosition stone) {
        for (IGoString string : getMembers()) {
            if (string.getMembers().contains(stone))
                return true;
        }
        return false;
    }

    /**
     * @return bounding box of set of stones/positions passed in
     */
    @Override
    public Box findBoundingBox() {
        int rMin = 10000; // something huge ( more than max rows)
        int rMax = 0;
        int cMin = 10000; // something huge ( more than max cols)
        int cMax = 0;

        // first determine a bounding rectangle for the group.
        for (IGoString string : this.getMembers()) {

            for (GoBoardPosition stone : string.getMembers()) {
                int row = stone.getRow();
                int col = stone.getCol();
                if (row < rMin) rMin = row;
                if (row > rMax) rMax = row;
                if (col < cMin) cMin = col;
                if (col > cMax) cMax = col;
            }
        }
        return (rMin > rMax) ? new Box(0, 0, 0, 0) : new Box(rMin, cMin, rMax, cMax);
    }

    /**
     * get the textual representation of the group.
     *
     * @return string form
     */
    @Override
    public String toString() {
        return toString("\n");
    }

    /**
     * get the html representation of the group.
     *
     * @return html form
     */
    @Override
    public String toHtml() {
        return toString("<br>");
    }

    /**
     * @param newline string to use for the newline - eg "\n" or "<br>".
     * @return string form.
     */
    private String toString(String newline) {

        StringBuilder sb = new StringBuilder(" GROUP {" + newline);
        Iterator it = getMembers().iterator();
        // print the member strings
        if (it.hasNext()) {
            IGoString p = (IGoString) it.next();
            sb.append("    ").append(p.toString());
        }
        while (it.hasNext()) {
            IGoString p = (IGoString) it.next();
            sb.append(',').append(newline).append("    ").append(p.toString());
        }
        sb.append(newline).append('}');
        return sb.toString();
    }

    /**
     * Notify our listeners (if any) that we have changed
     */
    private void broadcastChange() {
        for (GroupChangeListener listener : changeListeners) {
            listener.groupChanged();
        }
    }
}
