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

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;

import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * Represents a token that has a number.  The number is a float because
 * that's the general case for all numbers in an SGF file.
 * <p>
 * Typically, most numbers are integers, however since Komi usually ends in
 * 0.5, we have to make the number token hold a float by default.  Also,
 * sometimes seconds have tenths of a second to them, which are given after
 * the decimal.
 * <p>
 * Subclasses must implement getDefault().
 */
public abstract class NumberToken extends TextToken {

    private float mNumber = 0;

    public NumberToken() {
        setNumber(getDefault());
    }

    protected boolean parseContent(StreamTokenizer st)
            throws IOException, SGFException {
        if (!super.parseContent(st))
            return false;

        try {
            setNumber(Float.valueOf(getText()).floatValue());
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    protected abstract float getDefault();

    /**
     * Returns the number associated with this token.
     */
    public float getNumber() {
        return mNumber;
    }

    private void setNumber(float number) {
        mNumber = number;
    }
}

