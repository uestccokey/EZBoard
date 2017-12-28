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
 * The print mode to use for numbering the moves:
 * <PRE>
 * 0 - Don't print move numbers.
 * 1 - Print move numbers normally (1-200+)
 * 2 - Print move numbers "modulo 100"; for example:
 * 32-78   ->  32-78
 * 102-177  ->   2-77
 * 67-117  ->  67-117
 * 142-213  ->  42-113
 * </PRE>
 */
public class PrintModeToken extends NumberToken implements InfoToken {

    public PrintModeToken() {}

    protected float getDefault() {
        return 1;
    }

    public int getPrintMode() {
        return (int) getNumber();
    }
}

