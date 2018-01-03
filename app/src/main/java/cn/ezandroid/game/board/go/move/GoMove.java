/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.move;

import cn.ezandroid.game.board.common.TwoPlayerMove;
import cn.ezandroid.game.board.common.geometry.ByteLocation;
import cn.ezandroid.game.board.common.geometry.Location;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * 围棋落子
 *
 * @author Barry Becker
 */
public class GoMove extends TwoPlayerMove {

    /**
     * 落子后的提子
     */
    private GoCaptureList mCaptureList;

    public GoMove(Location destination, int val, GoStone stone) {
        super(destination, val, stone);
    }

    protected GoMove(GoMove move) {
        super(move);
        if (move.mCaptureList != null) {
            mCaptureList = move.mCaptureList.copy();
        }
    }

    @Override
    public GoMove copy() {
        return new GoMove(this);
    }

    public static GoMove createPassMove(int val, boolean player1) {
        GoMove m = new GoMove(new ByteLocation(1, 1), val, null);
        m.mIsPass = true;
        m.setPlayer1(player1);
        return m;
    }

    public static GoMove createResignationMove(boolean player1) {
        GoMove m = new GoMove(new ByteLocation(1, 1), 0, null);
        m.mIsResignation = true;
        m.setPlayer1(player1);
        return m;
    }

    /**
     * 检查该落子是否自杀着
     *
     * @param board
     * @return
     */
    public boolean isSuicidal(GoBoard board) {
        GoBoardPosition stone = (GoBoardPosition) board.getPosition(getToRow(), getToCol());

        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPositionSet nobiNbrs = na.getNobiNeighbors(stone, false, NeighborType.ANY);
        GoBoardPositionSet occupiedNbrs = new GoBoardPositionSet();
        for (GoBoardPosition pos : nobiNbrs) {
            if (pos.isOccupied()) {
                occupiedNbrs.add(pos);
            }
        }

        return !hasLiberties(occupiedNbrs, nobiNbrs) && partOfDeadString(occupiedNbrs, board);
    }

    /**
     * 如果还有一气，说明不是自杀着
     *
     * @return
     */
    private boolean hasLiberties(GoBoardPositionSet occupiedNbrs, GoBoardPositionSet nobiNbrs) {
        return (nobiNbrs.size() > occupiedNbrs.size());
    }

    /**
     * 如果新的落子提走了对方的棋串，返回false
     *
     * @param occupiedNbrs
     * @return
     */
    private boolean partOfDeadString(GoBoardPositionSet occupiedNbrs, GoBoard board) {
        for (GoBoardPosition nbr : occupiedNbrs) {
            if (nbr.getPiece().isOwnedByPlayer1() == this.isPlayer1()) {
                // friendly string
                if (nbr.getString().getNumLiberties(board) > 1) {
                    // can't be suicidal if a neighboring friendly string has > 1 liberty
                    return false;
                }
            } else {
                if (nbr.getString().getNumLiberties(board) == 1) {
                    // can't be suicidal if by playing we capture an opponent string.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查是否是劫
     * <p>
     * 1，它是单一棋子的棋串
     * 2，提走了一个敌方棋子
     * 3，现在还有一口气
     *
     * @param board
     * @return
     */
    public boolean isKo(GoBoard board) {
        if (getNumCaptures() == 1) {
            // GoBoardPosition capture = (GoBoardPosition) getCaptures().getFirst();
            GoBoardPosition pos = (GoBoardPosition) board.getPosition(getToLocation());

            NeighborAnalyzer nbrAnal = new NeighborAnalyzer(board);
            GoBoardPositionSet enemyNbrs = nbrAnal.getNobiNeighbors(pos, isPlayer1(), NeighborType.ENEMY);
            int numEnemyNbrs = enemyNbrs.size();

            if (numEnemyNbrs == 3
                    || board.isOnEdge(pos) && numEnemyNbrs == 2
                    || board.isInCorner(pos) && numEnemyNbrs == 1) {
                return true;
            }
        }
        return false;
    }

    public void setCaptures(GoCaptureList captures) {
        mCaptureList = captures.copy();
    }

    public GoCaptureList getCaptures() {
        return mCaptureList;
    }

    public int getNumCaptures() {
        if (mCaptureList != null) {
            return mCaptureList.size();
        } else {
            return 0;
        }
    }
}



