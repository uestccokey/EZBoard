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
 * The type of player (human vs. computer).
 */
public class SpeciesToken extends NumberToken {
    public SpeciesToken() { }

    /**
     * Presume a default species of 0 (human).
     */
    protected float getDefault() { return 0; }

    /**
     * Returns zero if a human is playing, or greater than one for a computer.
     */
    public int getSpecies() { return (int) getNumber(); }

    public String toString() {
        return (getSpecies() == 0) ? "Human" : "Computer";
    }
}

