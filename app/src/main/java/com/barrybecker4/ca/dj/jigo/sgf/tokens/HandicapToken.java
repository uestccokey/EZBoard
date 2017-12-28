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
 * The number of handicap stones given to black.
 * There are certain standard positions for all the handicaps.
 * See http://senseis.xmp.net/?HandicapPlacement
 */
public class HandicapToken extends NumberToken implements InfoToken {
    public HandicapToken() { }

    /**
     * Presume a default komi of 0.5 points.
     */
    protected float getDefault() { return (float) 2.0; }

    public float getHandicap() { return getNumber(); }
}

