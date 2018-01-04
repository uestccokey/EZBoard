/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.eye;

import java.util.LinkedList;

/**
 * 眼位列表
 *
 * @author Barry Becker
 */
public class GoEyeList extends LinkedList<IGoEye> {

    public GoEyeList() {}

    public GoEyeList(GoEyeList eyeList) {
        super(eyeList);
    }

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