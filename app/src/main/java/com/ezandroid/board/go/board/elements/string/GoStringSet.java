/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.elements.string;

import com.ezandroid.board.go.board.elements.position.GoBoardPosition;

import java.util.LinkedHashSet;

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