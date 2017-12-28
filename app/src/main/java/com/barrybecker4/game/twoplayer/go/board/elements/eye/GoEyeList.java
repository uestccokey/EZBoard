/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.elements.eye;

import java.util.LinkedList;

/**
 * A list of GoEyes.
 *
 * @author Barry Becker
 */
public class GoEyeList extends LinkedList<IGoEye> {

    /**
     * Default constructor.
     */
    public GoEyeList() {}

    /**
     * Copy constructor.
     *
     * @param eyeList list to initialize with
     */
    public GoEyeList(GoEyeList eyeList) {
        super(eyeList);
    }

    /**
     * pretty print a list of all the current groups (and the strings they contain)
     *
     * @return string form of list of stones.
     */
    public String toString(String title) {
        StringBuilder buf = new StringBuilder(title);
        buf.append("\n  ");
        for (IGoEye eye : this) {
            buf.append(eye.toString()).append(", ");
        }
        return buf.substring(0, buf.length() - 2);
    }

    @Override
    public IGoEye getFirst() {
        return super.getFirst();
    }

    @Override
    public IGoEye get(int i) {
        return super.get(i);
    }
}