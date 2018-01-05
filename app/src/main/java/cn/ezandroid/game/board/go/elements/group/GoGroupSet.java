/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.group;

import java.util.Iterator;
import java.util.LinkedHashSet;

import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 棋群集合
 * <p>
 * 使用LinkedHashSet可以支持按插入顺序遍历
 *
 * @author Barry Becker
 */
public class GoGroupSet extends LinkedHashSet<IGoGroup> {

    public GoGroupSet() {}

    /**
     * 检查是否指定点位的棋子在棋群中
     *
     * @param pos
     * @return
     */
    public boolean containsPosition(GoBoardPosition pos) {
        // if there is no stone in the position, then it cannot be part of a group
        if (!pos.isOccupied())
            return false;

        for (IGoGroup group : this) {
            if (group.isOwnedByPlayer1() == pos.getPiece().isOwnedByPlayer1() && group.containsStone(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对棋盘上的每个棋子，验证它只属于一个棋群
     */
    public void confirmAllStonesInUniqueGroups() {
        for (IGoGroup g : this) {
            confirmStonesInOneGroup(g);
        }
    }

    private void confirmStonesInOneGroup(IGoGroup group) {
        for (IGoString string : group.getMembers()) {
            for (IGoGroup g : this) {  // for each group on the board
                if (!g.equals(group)) {
                    for (IGoString s : g.getMembers()) {   // for each string in that group
                        if (string.equals(s)) {
                            assert false : "ERROR: " + s + " contained by 2 groups";
                        }
                        confirmStoneInStringAlsoInGroup(s, g);
                    }
                }
            }
        }
    }

    /**
     * 验证棋盘上没有空棋串
     */
    public void confirmNoEmptyStrings() {
        for (IGoGroup g : this) {
            for (IGoString s : g.getMembers()) {
                assert (s.size() > 0) : "There is an empty string in " + s.getGroup();
            }
        }
    }

    /**
     * 验证指定棋子所在的棋串和棋群不为空
     *
     * @param stone
     */
    public void confirmStoneInValidGroup(GoBoardPosition stone) {
        IGoString str = stone.getString();
        assert (str != null) : stone + " does not belong to any string!";
        IGoGroup g = str.getGroup();
        boolean valid = false;
        Iterator gIt = this.iterator();
        IGoGroup g1;
        while (!valid && gIt.hasNext()) {
            g1 = (IGoGroup) gIt.next();
            valid = g.equals(g1);
        }
        if (!valid) {
            assert false : "Error: This " + stone + " does not belong to a valid group: " +
                    g + " \nThe valid groups are:" + this;
        }
    }

    /**
     * 确保指定棋串中的所有棋子都属于指定棋群
     *
     * @param str
     * @param group
     */
    private void confirmStoneInStringAlsoInGroup(IGoString str, IGoGroup group) {
        for (GoBoardPosition pos : str.getMembers()) {
            if (pos.getGroup() != null && !group.equals(pos.getGroup())) {
                assert false : pos + " does not just belong to " + pos.getGroup()
                        + " as its ancestry indicates. It also belongs to " + group;
            }
        }
    }

    public String toString() {
        StringBuilder groupText = new StringBuilder("");
        StringBuilder blackGroupsText = new StringBuilder("The black groups are :\n");
        StringBuilder whiteGroupsText = new StringBuilder("\nThe white groups are :\n");
        for (Object group1 : this) {
            IGoGroup group = (IGoGroup) group1;
            if (group.isOwnedByPlayer1()) {
                blackGroupsText.append(group).append("\n");
            } else if (!group.isOwnedByPlayer1()) {
                whiteGroupsText.append(group).append("\n");
            }
        }
        groupText.append(blackGroupsText);
        groupText.append(whiteGroupsText);
        return groupText.toString();
    }
}