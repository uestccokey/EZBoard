/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.elements.eye;

import java.util.LinkedHashSet;

/**
 * A set of GoEyes.
 *
 * @author Barry Becker
 */
public class GoEyeSet extends LinkedHashSet<IGoEye> {

    public GoEyeSet() {}

    /**
     * Copy constructor.
     *
     * @param set eye set
     */
    public GoEyeSet(GoEyeSet set) {
        super(set);
    }
}