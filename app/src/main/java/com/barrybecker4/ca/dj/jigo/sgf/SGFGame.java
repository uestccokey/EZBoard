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

import com.barrybecker4.ca.dj.jigo.sgf.tokens.InfoToken;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Represents a full Go game; typically after having been parsed from
 * an InputStream conforming to the SGF (Smart-Game Format) file standard.
 * <p>
 * An SGFGame contains an SGFTree which houses every node (that is, a leaf)
 * of the game.  Leaves contain information such as moves, markup, and comments.
 * <p>
 * All tokens related to information about the game are also contained
 * by this class.  This makes the tokens easy to find and use (rather than
 * making them part of the first leaf of the first sub-tree of the game).
 * <p>
 * Eventually this class will wrap the functionality for saving an SGF
 * game, so the idea of allowing anyone add InfoTokens is valid (rather
 * than keeping it protected in the family, so to speak).
 */
public final class SGFGame {
    private SGFTree myTree = null;
    private Vector myInfoTokens = new Vector(10);

    /**
     * Creates a new SGFGame given a tree which comprises the game's nodes
     * (and sub-trees representing variations).
     *
     * @param tree - The root tree for this SGFGame.
     */
    public SGFGame(SGFTree tree) {
        setTree(tree);
    }

    /**
     * Returns the game tree, at its zeroth variation (i.e., root level).
     *
     * @return An SGFTree which contains all moves played in the game.
     */
    public SGFTree getTree() { return myTree; }

    /**
     * Used to set this game's game tree.
     *
     * @param gameTree - The game's game tree.
     */
    private void setTree(SGFTree gameTree) { myTree = gameTree; }

    public void addInfoToken(InfoToken it) { myInfoTokens.addElement(it); }

    /**
     * Returns the list of tokens that contain information about the SGF
     * game in question.
     *
     * @return An Enumeration filled with type com.barrybecker4.ca.dj.jigo.sgf.tokens.InfoToken.
     */
    public Enumeration getInfoTokens() { return myInfoTokens.elements(); }
}

