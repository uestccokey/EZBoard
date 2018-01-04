/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import cn.ezandroid.game.board.go.BoardValidator;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.elements.group.GoGroup;
import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.move.GoMove;

/**
 * 落子或者悔棋的棋盘更新器基类
 *
 * @author Barry Becker
 */
public abstract class PostChangeUpdater {

    GoBoard mBoard;
    CaptureCounts mCaptureCounts;
    NeighborAnalyzer mNeighborAnalyzer;
    BoardValidator mValidator;

    PostChangeUpdater(GoBoard board, CaptureCounts captureCounter) {
        mBoard = board;
        mCaptureCounts = captureCounter;
        mNeighborAnalyzer = new NeighborAnalyzer(board);
        mValidator = new BoardValidator(board);
    }

    /**
     * 落子或悔棋后更新棋盘上的棋串和棋群
     *
     * @param move
     */
    public abstract void update(GoMove move);

    GoBoard getBoard() {
        return mBoard;
    }

    GoGroupSet getAllGroups() {
        return mBoard.getGroups();
    }

    void recreateGroupsAfterChange() {
        GoGroupSet groups = new GoGroupSet();

        addFluxGroups(groups);

        mBoard.setGroups(groups);
        mBoard.unvisitAll();
    }

    private void addFluxGroups(GoGroupSet groups) {
        for (int i = 1; i <= getBoard().getNumRows(); i++) {
            for (int j = 1; j <= getBoard().getNumCols(); j++) {
                GoBoardPosition seed = (GoBoardPosition) getBoard().getPosition(i, j);
                if (seed.isOccupied() && !seed.isVisited()) {
                    GoBoardPositionList newGroup = mNeighborAnalyzer.findGroupFromInitialPosition(seed, false);
                    GoGroup g = new GoGroup(newGroup);
                    groups.add(g);
                }
            }
        }
    }

    /**
     * 删除没有棋子的棋群
     */
    void cleanupGroups() {
        GoGroupSet newGroups = new GoGroupSet();

        for (IGoGroup group : getAllGroups()) {
            if (group.getNumStones() > 0) {
                newGroups.add(group);
            }
        }
        mBoard.setGroups(newGroups);
    }

    /**
     * 清空棋盘上的眼位
     */
    protected void clearEyes() {
        for (int i = 1; i <= mBoard.getNumRows(); i++) {
            for (int j = 1; j <= mBoard.getNumCols(); j++) {
                GoBoardPosition space = (GoBoardPosition) mBoard.getPosition(i, j);
                if (space.isInEye()) {
                    // remove reference to the owning group so it can be garbage collected.
                    space.getEye().clear();
                    space.setEye(null);
                }
            }
        }
    }
}