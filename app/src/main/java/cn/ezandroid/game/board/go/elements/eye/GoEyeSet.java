/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.eye;

import java.util.LinkedHashSet;

/**
 * 眼位集合
 * <p>
 * 使用LinkedHashSet可以支持按插入顺序遍历
 *
 * @author Barry Becker
 */
public class GoEyeSet extends LinkedHashSet<IGoEye> {

    public GoEyeSet() {}

    public GoEyeSet(GoEyeSet set) {
        super(set);
    }
}