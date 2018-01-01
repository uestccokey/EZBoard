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

import cn.ezandroid.sgf.tokens.SGFToken;

/**
 * Contains all tokens for a particular node; it's an end-point for a game
 * tree.  Most nodes only have one token: the move they represent.  However,
 * since a move can have comments, board markup, and board settings, a class
 * was required to put everything associated with a move under one roof (but
 * this does not mean a move must always be associated with a leaf, that's
 * just what happens more often than not).
 */
public final class SGFLeaf {

    private LinkedList<SGFToken> mTokens = new LinkedList<>();

    /**
     * Creates a new SGFLeaf; use with caution.  All SGFLeaves must have
     * at least one token.  So if you add this leaf to a tree without having
     * any tokens inside, you can potentially hose the system.  That's why
     * it's private.
     */
    private SGFLeaf() {
    }

    /**
     * Creates a new SGFLeaf, with at least one token.
     *
     * @param token - The first token in this leaf.
     */
    public SGFLeaf(SGFToken token) {
        addToken(token);
    }

    /**
     * Returns the tokens housed by this leaf.
     *
     * @return An enumeration of this leaf's tokens.
     */
    public Iterator<SGFToken> getTokens() {
        return mTokens.iterator();
    }

    /**
     * Adds a token to this leaf; usually this will be a move that was played
     * in the game.
     *
     * @param token - Information that must be added to the leaf.
     */
    public void addToken(SGFToken token) {
        if (token != null)
            mTokens.add(token);
    }

    /**
     * Removes a token from this leaf; useful for deleting comments and board
     * mark-up.
     *
     * @param token - Information that must be removed from the leaf.
     */
    public void removeToken(SGFToken token) {
        if (token != null)
            mTokens.remove(token);
    }
}

