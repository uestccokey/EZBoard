/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go;

import java.util.List;

import cn.ezandroid.game.board.common.TwoPlayerBoard;
import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.go.analysis.CornerChecker;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.string.GoString;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;
import cn.ezandroid.game.board.go.move.GoMove;
import cn.ezandroid.game.board.go.update.BoardUpdater;
import cn.ezandroid.game.board.go.update.CaptureCounts;

/**
 * 围棋棋盘模型
 *
 * @author Barry Becker
 */
public final class GoBoard extends TwoPlayerBoard<GoMove> {

    // 棋群集合 棋群由棋串组成
    private GoGroupSet mGroups;

    // 让子
    private HandicapStones mHandicap;

    // 棋盘更新器
    private BoardUpdater mBoardUpdater;

    public GoBoard(int size, int numHandicapStones) {
        setSize(size, size);
        setHandicap(numHandicapStones);

        init(new CaptureCounts());
    }

    public GoBoard(GoBoard board) {
        super(board);

        mHandicap = board.mHandicap;
        NeighborAnalyzer analyzer = new NeighborAnalyzer(this);
        analyzer.determineAllStringsOnBoard();
        mGroups = analyzer.findAllGroupsOnBoard();

        init(board.mBoardUpdater.getCaptureCounts());
    }

    @Override
    public synchronized GoBoard copy() {
        GoBoard b = new GoBoard(this);
        return b;
    }

    void setPosition(GoBoardPosition pos) {
        super.setPosition(pos);
    }

    /**
     * 从头开始，重新初始化一切
     */
    @Override
    public void reset() {
        super.reset();
        mGroups = new GoGroupSet();

        setHandicap(getHandicap());
        init(new CaptureCounts());
    }

    private void init(CaptureCounts capCounts) {
        mBoardUpdater = new BoardUpdater(this, capCounts);
    }

    @Override
    protected BoardPosition getPositionPrototype() {
        return new GoBoardPosition(1, 1, null, null);
    }

    public void setHandicap(int numHandicapStones) {
        mHandicap = new HandicapStones(numHandicapStones, getNumRows());
        makeMoves(mHandicap.getHandicapMoves());
    }

    /**
     * 获取让子数
     *
     * @return
     */
    public int getHandicap() {
        if (mHandicap == null) {
            return 0;
        }
        return mHandicap.getNumber();
    }

    /**
     * 获取让子位置
     *
     * @return
     */
    public List getHandicapPositions() {
        return mHandicap.getStarPoints();
    }

    public void setGroups(GoGroupSet groups) {
        mGroups = groups;
    }

    /**
     * 获取当前存活的棋群集合
     *
     * @return
     */
    public GoGroupSet getGroups() {
        return mGroups;
    }

    /**
     * 更新气点周围棋串的气
     *
     * @param liberty
     */
    public void adjustLiberties(GoBoardPosition liberty) {
        NeighborAnalyzer na = new NeighborAnalyzer(this);
        GoStringSet stringNbrs = na.findStringNeighbors(liberty);
        for (IGoString sn : stringNbrs) {
            ((GoString) sn).changedLiberty(liberty);
        }
    }

    /**
     * 确保棋盘上所有点都重置为未被访问状态
     */
    public void unvisitAll() {
        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) getPosition(i, j);
                pos.setVisited(false);
            }
        }
    }

    @Override
    protected boolean makeInternalMove(GoMove move) {
        // if its a passing move, there is nothing to do
        if (move.isPassOrResignation()) {
            return true;
        }

        boolean valid = super.makeInternalMove(move);
        mBoardUpdater.updateAfterMove(move);
        return valid;
    }

    @Override
    protected void undoInternalMove(GoMove move) {
        // there is nothing to do if it is a pass
        if (move.isPassingMove()) {
            return;
        }

        mBoardUpdater.updateAfterRemove(move);
    }

    /**
     * 获取指定玩家的被提子数
     *
     * @param player1StonesCaptured
     * @return
     */
    public int getNumCaptures(boolean player1StonesCaptured) {
        return mBoardUpdater.getNumCaptures(player1StonesCaptured);
    }

    /**
     * 检查指定点是否属于角落3角点
     *
     * @param position
     * @return
     */
    public boolean isCornerTriple(BoardPosition position) {
        return new CornerChecker(getNumRows(), getNumCols()).isCornerTriple(position);
    }

    /**
     * 获取指定玩家在棋盘上的棋子数
     *
     * @return
     */
    public int getNumStones(boolean forPlayer1) {
        int numStones = 0;

        // we should be able to just sum all the position scores now.
        for (int i = 1; i <= getNumRows(); i++) {
            for (int j = 1; j <= getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) getPosition(i, j);
                if (pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == forPlayer1) {
                    numStones++;
                }
            }
        }
        return numStones;
    }

    @Override
    public String toString() {
        int rows = getNumRows();
        int cols = getNumCols();
        StringBuilder buf = new StringBuilder((rows + 2) * (cols + 2));

        buf.append("   ");
        for (int j = 1; j <= rows; j++) {
            buf.append(j % 10);
        }
        buf.append(' ');
        buf.append("\n  ");
        for (int j = 1; j <= cols + 2; j++) {
            buf.append('-');
        }
        buf.append('\n');

        for (int i = 1; i <= rows; i++) {
            buf.append(i / 10);
            buf.append(i % 10);
            buf.append('|');
            for (int j = 1; j <= cols; j++) {
                GoBoardPosition space = (GoBoardPosition) getPosition(i, j);
                if (space.isOccupied()) {
                    buf.append(space.getPiece().isOwnedByPlayer1() ? 'X' : 'O');
                } else {
                    buf.append(' ');
                }
            }
            buf.append('|');
            buf.append('\n');
        }
        return buf.toString();
    }
}
