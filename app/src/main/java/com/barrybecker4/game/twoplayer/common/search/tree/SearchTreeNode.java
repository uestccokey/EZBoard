/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.tree;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

import java.util.Enumeration;
import java.util.List;

import cn.ezandroid.sgf.Point;

/**
 * Represents a move/node in the game tree.
 * Each SearchTreeNode contains a Move as its userObject.
 * When showing the game tree graphically, these variables can take a lot of space
 * since they are in every node in the tree. Still it is better to have them here than
 * in the move structure so that when we are not in debug mode the space is not used.
 *
 * @author Barry Becker
 */
public class SearchTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    public NodeAttributes attributes;

    /**
     * Used to layout the tree. Roughly based on the num descendants.
     * initialized by GameTreeViewer.
     */
    private int spaceAllocation_ = 0;

    /**
     * location in the board viewer
     */
    private Point position_;

    /**
     * Default Constructor
     *
     * @param move a twoplayer board move.
     */
    public SearchTreeNode(TwoPlayerMove move) {
        setUserObject(move);
        this.attributes = new NodeAttributes();
    }

    /**
     * Default Constructor
     *
     * @param move       a twoplayer board move.
     * @param attributes set of name value pairs describing the node.
     */
    public SearchTreeNode(TwoPlayerMove move, final NodeAttributes attributes) {
        setUserObject(move);
        this.attributes = attributes;
    }

    public TwoPlayerMove[] getChildMoves() {

        if (children == null) return null;
        TwoPlayerMove[] moves = new TwoPlayerMove[children.size()];
        Enumeration enumeration = children();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode) enumeration.nextElement();
            moves[i++] = (TwoPlayerMove) node.getUserObject();
        }
        return moves;
    }

    /**
     * Show nodes corresponding to pruned branches in the game tree (if one is used).
     *
     * @param list       list of moves that resulted in pruned branches.
     * @param i          th child.
     * @param attributes list of name values to show.
     */
    public void addPrunedChildNodes(List list, int i, NodeAttributes attributes) {
        int index = i;
        while (!list.isEmpty()) {
            TwoPlayerMove theMove = (TwoPlayerMove) (list.remove(0));
            SearchTreeNode child = new SearchTreeNode(theMove, attributes);
            this.insert(child, index);
            index++;
        }
    }

    /**
     * @return the move that the computer expects will be played next
     */
    public SearchTreeNode getExpectedNextNode() {

        if (children == null) return null;
        Enumeration enumeration = children();

        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode) enumeration.nextElement();
            TwoPlayerMove m = (TwoPlayerMove) node.getUserObject();
            if (m.isSelected())
                return node;
        }
        return null;
    }

    /**
     * One of our existing children bust be the next node.
     * It must never be null, but it may not be there yet because they are added in a different thread
     * So we block until there.
     *
     * @param move the move whose child moves we will search.
     * @return the next child node to use as parent. Must never be null.
     */
    public SearchTreeNode findChild(TwoPlayerMove move) {
        Enumeration enumeration = this.children();
        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode) enumeration.nextElement();
            if (move.equals(node.getUserObject())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Serialize all the children.
     *
     * @return children represented in string form
     */
    private String childrenAsString() {
        StringBuilder bld = new StringBuilder();
        Enumeration enumeration = this.children();
        while (enumeration.hasMoreElements()) {
            SearchTreeNode node = (SearchTreeNode) enumeration.nextElement();
            bld.append(node);
        }
        return bld.toString();
    }

    public boolean isPruned() {
        return attributes.pruned;
    }

    @Override
    public String toString() {
        Object m = getUserObject();
        if (m == null) return null;

        StringBuilder s = new StringBuilder(m.toString());
        s.append(attributes.toString());

        return s.toString();
    }

    public int getSpaceAllocation() {
        return spaceAllocation_;
    }

    public void setSpaceAllocation(int spaceAllocation) {
        this.spaceAllocation_ = spaceAllocation;
    }

    public Point getPosition() {
        return position_;
    }

    public void setLocation(int x, int y) {
        position_ = new Point(x, y);
    }
}
