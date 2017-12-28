/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.tree;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * This interface is implemented by classes that can show the game tree as it is searched.
 * I used to modify the game tree nodes directly during search, but found that I got a lot of intermittent
 * concurrent modification exceptions and null pointer exceptions while showing the game tree. Now events are thrown
 * to indicate changes to the tree should be made during search, and the handler should make the changes
 * to the tree in the eventDispatch thread.
 *
 * @author Barry Becker
 */
public interface IGameTreeViewable {

    /**
     * @return the root node of the search tree.
     */
    SearchTreeNode getRootNode();

    /**
     * Add a node to the viewable search tree.
     *
     * @param parent parent node
     * @param child  child node
     */
    void addNode(SearchTreeNode parent, SearchTreeNode child);

    /**
     * Add a set of pruned nodes to the viewable search tree with associated attributes.
     *
     * @param list       list of moves to add
     * @param parent     parent node
     * @param i          ith child
     * @param attributes name value pairs to show in ui.
     */
    void addPrunedNodes(MoveList list, SearchTreeNode parent,
                        int i, NodeAttributes attributes);

    /**
     * Clear out the visible search tree.
     *
     * @param evt event
     */
    void resetTree(TwoPlayerMove evt);

}
