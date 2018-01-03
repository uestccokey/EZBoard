/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.position;

import java.util.LinkedList;

/**
 * 围棋位置点列表
 *
 * @author Barry Becker
 */
public class GoBoardPositionList extends LinkedList<GoBoardPosition> {

    public GoBoardPositionList() {}

    public GoBoardPositionList(GoBoardPositionList positionList) {
        super(positionList);
    }

    @Override
    public GoBoardPosition getFirst() {
        return super.getFirst();
    }

    @Override
    public GoBoardPosition get(int i) {
        return super.get(i);
    }

    public void unvisitPositions() {
        for (GoBoardPosition position : this) {
            position.setVisited(false);
        }
    }
}