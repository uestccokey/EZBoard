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

/**
 * Represents a point on a Goban.  The x and y values are directly
 * accessible due to their common, and frequent use.
 */
public final class Point {

    public int x = 0, y = 0;

    /**
     * Constructs a new point at (0, 0).
     */
    public Point() { }

    /**
     * Constructs a new point at the given coordinates.
     *
     * @param newX - The x portion of this point's coordinate.
     * @param newY - The y portion of this point's coordinate.
     */
    public Point(int newX, int newY) {
        x = newX;
        y = newY;
    }

    /**
     * Copy constructor
     */
    public Point(Point pt) {
        this(pt.x, pt.y);
    }
}

