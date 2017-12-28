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
 * The number of points given to White to compensate for Black having the
 * first move.  This is typically 5.5 points, but defaults to 0.5 points
 * (white wins tie games).
 */
public class KomiToken extends NumberToken implements InfoToken {

    public KomiToken() {}

    /**
     * Presume a default komi of 0.5 points.
     */
    protected float getDefault() {
        return (float) 0.5;
    }

    public float getKomi() {
        return getNumber();
    }
}

