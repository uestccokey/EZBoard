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

package cn.ezandroid.sgf.tokens;

/**
 * 游戏类别
 * <p>
 * 1      - Go
 * 6      - Backgammon
 * 9      - Lines of Action
 * 11     - Hex
 * 18     - Amazons
 */
public class GameTypeToken extends NumberToken implements InfoToken {

    public GameTypeToken() {}

    /**
     * 默认游戏类别为围棋
     */
    protected float getDefault() {
        return 1;
    }

    public int getType() {
        return (int) getNumber();
    }
}

