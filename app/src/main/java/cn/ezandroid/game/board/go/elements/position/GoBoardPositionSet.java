/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.position;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 围棋位置点集合
 * <p>
 * 使用LinkedHashSet可以支持按插入顺序遍历
 *
 * @author Barry Becker
 */
public class GoBoardPositionSet extends LinkedHashSet<GoBoardPosition> {

    public GoBoardPositionSet() {}

    public GoBoardPositionSet(Set<GoBoardPosition> set) {
        super(set);
    }

    /**
     * 注意，没有顺序的概念.
     *
     * @return
     */
    public GoBoardPosition getOneMember() {
        return iterator().next();
    }

    public void unvisitPositions() {
        for (GoBoardPosition position : this) {
            position.setVisited(false);
        }
    }
}