/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import java.util.LinkedList;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * Responsible for determining and updating the dead stones on the board.
 *
 * @author Barry Becker
 */
public final class DeadStoneUpdater {

    private GoBoard board_;

    /** keeps track of dead stones. */
    private DeadStones deadStones_;

    /**
     * Construct the Go game controller.
     */
    public DeadStoneUpdater(GoBoard board) {
        board_ = board;
        deadStones_ = new DeadStones();
    }

    public int getNumDeadStonesOnBoard(boolean forPlayer1) {
        return deadStones_.getNumberOnBoard(forPlayer1);
    }

    public LinkedList<GoBoardPosition> getDeadStonesOnBoard(boolean forPlayer1) {
        return deadStones_.getDeadStonesOnBoard(forPlayer1);
    }

    /**
     * Update the final life and death status of all the stones still on the board.
     * This method must only be called once at the end of the game or stones will get prematurely marked as dead.
     * The first can update the health of groups and perhaps remove obviously dead stones.
     */
    public void determineDeadStones() {
        deadStones_.clear();

        for (int row = 1; row <= board_.getNumRows(); row++) {
            for (int col = 1; col <= board_.getNumCols(); col++) {
                GoBoardPosition space = (GoBoardPosition) board_.getPosition(row, col);
                if (space.isOccupied()) {
                    GoStone stone = (GoStone) space.getPiece();
                    int side = (stone.isOwnedByPlayer1() ? 1 : -1);
                    GameContext.log(1, "life & death: " + space + " health=" + stone.getHealth());
                    if (side * stone.getHealth() < 0) {
                        // then the stone is more dead than alive, so mark it so
                        GameContext.log(0, "setting " + space + " to dead");
                        stone.setDead(true);
                        deadStones_.increment(space, space.getPiece().isOwnedByPlayer1());
                    }
//                    if (stone.isOwnedByPlayer1() && stone.getHealth() <= 0.1 || !stone.isOwnedByPlayer1() && stone.getHealth() >= -0.1) {
//                        // then the stone is more dead than alive, so mark it so
//                        GameContext.log(0, "setting " + space + " to dead");
//                        stone.setDead(true);
//                        deadStones_.increment(space.getPiece().isOwnedByPlayer1());
//                    }
                }
            }
        }
        GameContext.log(0, deadStones_.toString());
    }
}
