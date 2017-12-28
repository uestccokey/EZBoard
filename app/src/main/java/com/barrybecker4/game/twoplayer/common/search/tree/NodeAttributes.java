/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.tree;


import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;

import java.util.HashMap;


/**
 * Represents a move/node in the game tree.
 * Each SearchTreeNode contains a Move as its userObject.
 * When showing the game tree graphically, these variables can take a lot of space
 * since they are in every node in the tree. Still it is better to have them here than
 * in the move structure so that when we are not in debug mode the space is not used.
 *
 * @author Barry Becker
 */
public class NodeAttributes extends HashMap<String, String> {

    public boolean pruned = false;

    /**
     * Default Constructor
     */
    public NodeAttributes() {}

    /**
     * Factory method to create a pruned node.
     */
    public static NodeAttributes createPrunedNode(int selectedValue, SearchWindow window) {
        NodeAttributes attributes = new NodeAttributes();
        attributes.put("value", FormatUtil.formatNumber(selectedValue));
        attributes.put("window", window.toString());
        attributes.pruned = true;
        attributes.put("pruned", "(value outside window)");
        return attributes;
    }

    /**
     * Factory method to create an inner node.
     */
    public static NodeAttributes createInnerNode(TwoPlayerMove theMove, SearchWindow window) {
        NodeAttributes attributes = new NodeAttributes();
        attributes.put("value", FormatUtil.formatNumber(theMove.getValue()));
        attributes.put("inhVal", FormatUtil.formatNumber(theMove.getInheritedValue()));
        attributes.put("window", window.toString());
        return attributes;
    }

}
