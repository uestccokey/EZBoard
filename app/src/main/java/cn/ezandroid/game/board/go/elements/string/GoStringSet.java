/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.string;

import java.util.LinkedHashSet;

import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * 棋串集合
 * <p>
 * 使用LinkedHashSet可以支持按插入顺序遍历
 *
 * @author Barry Becker
 */
public class GoStringSet extends LinkedHashSet<IGoString> {

    public GoStringSet() {}

    public GoStringSet(GoStringSet set) {
        super(set);
    }

    /**
     * 获取包含指定点位的棋串
     *
     * @param pos
     * @return
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