/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import java.util.LinkedList;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * 死子更新器
 * <p>
 * 负责决定和更新棋盘上的死子
 *
 * @author Barry Becker
 */
public final class DeadStoneUpdater {

    private GoBoard mBoard;

    private DeadStones mDeadStones;

    public DeadStoneUpdater(GoBoard board) {
        mBoard = board;
        mDeadStones = new DeadStones();
    }

    public int getNumDeadStonesOnBoard(boolean forPlayer1) {
        return mDeadStones.getNumberOnBoard(forPlayer1);
    }

    public LinkedList<GoBoardPosition> getDeadStonesOnBoard(boolean forPlayer1) {
        return mDeadStones.getDeadStonesOnBoard(forPlayer1);
    }

    /**
     * 根据棋盘上棋子的健康评分确定死子
     */
    public void determineDeadStones() {
        mDeadStones.clear();

        for (int row = 1; row <= mBoard.getNumRows(); row++) {
            for (int col = 1; col <= mBoard.getNumCols(); col++) {
                GoBoardPosition space = (GoBoardPosition) mBoard.getPosition(row, col);
                if (space.isOccupied()) {
                    GoStone stone = (GoStone) space.getPiece();
                    int side = (stone.isOwnedByPlayer1() ? 1 : -1);
                    GameContext.log(1, "life & death: " + space + " health=" + stone.getHealth());
                    if (side * stone.getHealth() < 0) {
                        // then the stone is more dead than alive, so mark it so
                        GameContext.log(0, "setting " + space + " to dead");
                        mDeadStones.increment(space, space.getPiece().isOwnedByPlayer1());
                    }
//                    if (stone.isOwnedByPlayer1() && stone.getHealth() <= 0.1 || !stone.isOwnedByPlayer1() && stone.getHealth() >= -0.1) {
//                        // then the stone is more dead than alive, so mark it so
//                        GameContext.log(0, "setting " + space + " to dead");
//                        mDeadStones.increment(space, space.getPiece().isOwnedByPlayer1());
//                    }
                }
            }
        }

        GameContext.log(0, mDeadStones.toString());
    }
}
