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

package com.barrybecker4.ca.dj.jigo.sgf;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Contains an entire game tree.  Game trees are typically read in from an SGF
 * file, but this isn't a requirement -- they could be generated from a
 * game that is being played.
 */
public final class SGFTree {
    /**
     * How many variations are typically present in an SGF file?  At the moment
     * these constants are educated guesses -- eventually games should be
     * analyzed to find optimum values.
     */
    private final static int AVERAGE_VARIATIONS = 2;

    /**
     * How many leaves (i.e., moves) are in an SGF variation?  This is only 5
     * because variations don't tend to have a lot of moves.  If more than 5
     * moves are present, then the container for leaves is increased to hold
     * much more.
     */
    private final static int AVERAGE_LEAVES = 5;

    /**
     * This is set to a largish number due to the fact that if a variation
     * contains more than the average number of leaves, it's probably not
     * a true sub-variation, rather the actual moves in a game, which has
     * a practical upper limit of 400 moves (professional games average around
     * 200 moves).
     */
    private final static int AVERAGE_LEAF_INCR = 50;

    private Vector<SGFLeaf> mLeaves;
    private Vector<SGFTree> mVariations;

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
        if (mLeaves == null)
            mLeaves = new Vector<>(AVERAGE_LEAVES, AVERAGE_LEAF_INCR);

        if (leaf != null)
            mLeaves.addElement(leaf);
    }

    /**
     * Returns the list of leaves at this level of the game tree.
     *
     * @return A list of leaves at this variation level, or null if none.
     */
    public Enumeration getLeaves() {
        return (mLeaves == null) ? null : mLeaves.elements();
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
        return (mLeaves == null) ? 0 : mLeaves.size();
    }

    /**
     * Returns the number of variations at this level of the game tree.  This
     * is not the same as the total number of variations in the game.
     *
     * @return The variations at this level of the game tree.
     */
    public int getVariationCount() {
        return (mVariations == null) ? 0 : mVariations.size();
    }

    /**
     * Appends a sub-tree to this tree's list of variations.
     *
     * @param tree - The tree to add to the end of this tree's variation list.
     */
    public void addTree(SGFTree tree) {
        if (mVariations == null)
            mVariations = new Vector<>(AVERAGE_VARIATIONS);

        mVariations.addElement(tree);
    }

    /**
     * Returns the list of sub-trees at this level of the game tree.
     *
     * @return A list of sub-trees at this variation level, or null if none.
     */
    public Enumeration getTrees() {
        return (mVariations == null) ? null : mVariations.elements();
    }
}

