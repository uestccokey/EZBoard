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

import com.barrybecker4.ca.dj.jigo.sgf.Point;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A list of points.
 */
public class PlacementListToken extends PlacementToken implements MarkupToken {
    private List<Point> myPoints = new LinkedList<Point>();

    public PlacementListToken() { }

    protected boolean parseContent(StreamTokenizer st)
            throws IOException {
        do {
            // Read a point in the list of points (of which there must be at least one),
            // then add it to our internal list of points.
            // need to make a copy. otherwise we end up adding the same point to the list each time.
            if (parsePoint(st)) {

                addPoint(new Point(getPoint()));
            }
        }
        while (st.nextToken() == (int) '[');

        st.pushBack();

        return true;
    }

    private void addPoint(Point point) { myPoints.add(point); }

    public Iterator<Point> getPoints() { return myPoints.iterator(); }

    public List<Point> getPoints2() { return myPoints; }
}

