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

package com.barrybecker4.ca.dj.jigo.sgf.tokens;

/**
 * The type of game in this SGF stream.  Normally, a program won't continue if
 * it doesn't read a 1 in this token which indicates a game of Go:
 * <p>
 * <PRE>
 * 1      - Go
 * 6      - Backgammon
 * 9      - Lines of Action
 * 11     - Hex
 * 18     - Amazons
 * </PRE>
 */
public class GameTypeToken extends NumberToken implements InfoToken {
    public GameTypeToken() { }

    /**
     * Presume the SGF file is a game of Go, by default.
     */
    protected float getDefault() { return 1; }

    public int getType() { return (int) getNumber(); }
}

