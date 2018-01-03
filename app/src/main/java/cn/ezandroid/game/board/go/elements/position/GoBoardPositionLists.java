/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.position;

import java.util.LinkedList;

/**
 * 围棋位置点列表的列表
 *
 * @author Barry Becker
 */
public class GoBoardPositionLists extends LinkedList<GoBoardPositionList> {

    public GoBoardPositionLists() {}

    public GoBoardPositionLists(GoBoardPositionLists positionList) {
        super(positionList);
    }

    @Override
    public GoBoardPositionList get(int i) {
        return super.get(i);
    }

    public void unvisitPositionsInLists() {
        for (GoBoardPositionList list : this) {
            list.unvisitPositions();
        }
    }
}