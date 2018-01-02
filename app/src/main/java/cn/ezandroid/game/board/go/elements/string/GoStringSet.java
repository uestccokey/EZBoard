/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.string;

import java.util.LinkedHashSet;

import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * A set of GoStrings.
 *
 * @author Barry Becker
 */
public class GoStringSet extends LinkedHashSet<IGoString> {

    public GoStringSet() {}

    /**
     * Copy constructor.
     *
     * @param set set of strings to initialize with
     */
    public GoStringSet(GoStringSet set) {
        super(set);
    }

    /**
     * @param pos position
     * @return the string that contains pos if any. Null if none.
     */
    public IGoString findStringContainingPosition(GoBoardPosition pos) {
        for (IGoString str : this) {
            if (str.contains(pos)) {
                return str;
            }
        }
        return null;
    }
}