/*
 * Copyright (C) 2001 by Dave Jarvis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

package cn.ezandroid.sgf;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Contains an entire game tree.  Game trees are typically read in from an SGF
 * file, but this isn't a requirement -- they could be generated from a
 * game that is being played.
 */
public final class SGFTree {

    private LinkedList<SGFLeaf> mLeaves = new LinkedList<>();
    private LinkedList<SGFTree> mVariations = new LinkedList<>();

    /**
     * Empty constructor.  No leaves, no variations.  Rather useless by itself.
     */
    public SGFTree() { }

    /**
     * Adds a leaf to the end of this tree's list of leaves.  Remember that
     * an SGFLeaf contains moves, comments, board markup, etc.
     *
     * @param leaf - The leaf to add to the end of this tree's leaves list.
     */
    public void addLeaf(SGFLeaf leaf) {
        // Have the 'mLeaves' dynamic array increase for lots more moves
        // if it gets filled up.  Typically, there aren't a lot of moves in a
        // variation -- so if there ARE a lot of moves, chances are it isn't
        // a variation, thus the array/vector should jump significantly in size.
        // This makes the code more efficient in terms of both speed and memory
        // consumption.
        //
        if (leaf != null)
            mLeaves.add(leaf);
    }

    /**
     * Returns the list of leaves at this level of the game tree.
     *
     * @return A list of leaves at this variation level, or null if none.
     */
    public Iterator<SGFLeaf> getLeaves() {
        return mLeaves.iterator();
    }

    /**
     * Returns the number of leaves at this level of the game tree.  This
     * is not the same as the total number of leaves in the game, which
     * can only be acquired by enumerating through all the trees in the game
     * and tallying each of their own leaf counts.
     * <p>
     * <p>
     * However, most SGF files rarely show variations, and when they do they
     * are few and far between.  (This applies chiefly to professional-level
     * games.)
     *
     * @return The number of leaves at this level of the game tree.
     */
    public int getLeafCount() {
        return mLeaves.size();
    }

    /**
     * Returns the number of variations at this level of the game tree.  This
     * is not the same as the total number of variations in the game.
     *
     * @return The variations at this level of the game tree.
     */
    public int getVariationCount() {
        return mVariations.size();
    }

    /**
     * Appends a sub-tree to this tree's list of variations.
     *
     * @param tree - The tree to add to the end of this tree's variation list.
     */
    public void addTree(SGFTree tree) {
        if (tree != null)
            mVariations.add(tree);
    }

    /**
     * Returns the list of sub-trees at this level of the game tree.
     *
     * @return A list of sub-trees at this variation level, or null if none.
     */
    public Iterator<SGFTree> getTrees() {
        return mVariations.iterator();
    }
}

