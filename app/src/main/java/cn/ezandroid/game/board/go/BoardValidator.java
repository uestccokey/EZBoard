/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go;

import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * Assert certain things are true about the board.
 * Helpful for debugging.
 *
 * @author Barry Becker
 */
public class BoardValidator {

    private GoBoard board_;

    public BoardValidator(GoBoard board) {
        board_ = board;
    }

    /**
     * verify that all the stones on the board are in the boards member list of groups.
     */
    public void confirmStonesInValidGroups() {
        GoGroupSet groups = board_.getGroups();
        for (int i = 1; i <= board_.getNumRows(); i++) {
            for (int j = 1; j <= board_.getNumCols(); j++) {
                GoBoardPosition space = (GoBoardPosition) board_.getPosition(i, j);
                if (space.isOccupied()) {
                    groups.confirmStoneInValidGroup(space);
                }
            }
        }
    }

    /**
     * verify that all the stones are marked unvisited.
     */
    public void confirmAllUnvisited() {
        GoBoardPosition stone = areAllUnvisited();
        if (stone != null)
            assert false : stone + " is marked visited";
    }

    /**
     * verify that all the stones are marked unvisited.
     *
     * @return position that is still marked visited if any, else null.
     */
    private GoBoardPosition areAllUnvisited() {
        for (int i = 1; i <= board_.getNumRows(); i++) {
            for (int j = 1; j <= board_.getNumCols(); j++) {
                GoBoardPosition stone = (GoBoardPosition) board_.getPosition(i, j);
                if (stone.isVisited())
                    return stone;
            }
        }
        return null;
    }
}
