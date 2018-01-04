/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go;

import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;

/**
 * 棋盘验证器
 * <p>
 * 确保棋盘相关状态正确，用于Debug
 *
 * @author Barry Becker
 */
public class BoardValidator {

    private GoBoard mBoard;

    public BoardValidator(GoBoard board) {
        mBoard = board;
    }

    /**
     * 对棋盘上的每个棋子，验证它所在的棋串和棋群不为空
     */
    public void confirmStonesInValidGroups() {
        GoGroupSet groups = mBoard.getGroups();
        for (int i = 1; i <= mBoard.getNumRows(); i++) {
            for (int j = 1; j <= mBoard.getNumCols(); j++) {
                GoBoardPosition space = (GoBoardPosition) mBoard.getPosition(i, j);
                if (space.isOccupied()) {
                    groups.confirmStoneInValidGroup(space);
                }
            }
        }
    }
}
