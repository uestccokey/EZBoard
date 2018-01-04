/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionLists;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoString;
import cn.ezandroid.game.board.go.elements.string.IGoString;
import cn.ezandroid.game.board.go.move.GoCaptureList;
import cn.ezandroid.game.board.go.move.GoMove;

/**
 * 悔棋后的棋盘更新器
 *
 * @author Barry Becker
 */
public class PostRemoveUpdater extends PostChangeUpdater {

    PostRemoveUpdater(GoBoard board, CaptureCounts captures) {
        super(board, captures);
    }

    @Override
    public void update(GoMove move) {
        // first make sure that there are no references to obsolete groups.
        clearEyes();

        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToLocation()));

        IGoString stringThatItBelongedTo = stone.getString();
        // clearing a stone may cause a string to split into smaller strings
        stone.clear(getBoard());
        mBoard.adjustLiberties(stone);

        updateStringsAfterRemove(stone, stringThatItBelongedTo);
        restoreCaptures(move.getCaptures());

        recreateGroupsAfterChange();

        mCaptureCounts.updateCaptures(move, false);
    }

    private void updateStringsAfterRemove(GoBoardPosition stone, IGoString string) {
        // avoid error when calling from treeDlg
        if (string == null) return;

        splitStringsIfNeeded(stone, string);

        if (GameContext.getDebugMode() > 1) {
            getAllGroups().confirmNoEmptyStrings();
            mValidator.confirmStonesInValidGroups();
        }
    }

    private void splitStringsIfNeeded(GoBoardPosition stone, IGoString string) {
        IGoGroup group = string.getGroup();
        GoBoardPositionSet nbrs =
                mNeighborAnalyzer.getNobiNeighbors(stone, group.isOwnedByPlayer1(), NeighborType.FRIEND);

        if (nbrs.size() > 1) {
            GoBoardPositionLists lists = new GoBoardPositionLists();
            GoBoardPosition firstNbr = nbrs.getOneMember();
            GoBoardPositionList stones = mNeighborAnalyzer.findStringFromInitialPosition(firstNbr, false);
            lists.add(stones);
            for (GoBoardPosition nbrStone : nbrs) {
                if (!nbrStone.isVisited()) {
                    GoBoardPositionList stones1 = mNeighborAnalyzer.findStringFromInitialPosition(nbrStone, false);
                    IGoString newString = new GoString(stones1, getBoard());
                    group.addMember(newString);
                    lists.add(stones1);
                }
            }
            lists.unvisitPositionsInLists();
        }
    }

    /**
     * 恢复被提到的棋子列表
     *
     * @param captures
     */
    private void restoreCaptures(GoCaptureList captures) {
        if (captures == null || captures.isEmpty()) return;

        captures.restoreOnBoard(mBoard);
        if (GameContext.getDebugMode() > 1) {
            mValidator.confirmStonesInValidGroups();
            getAllGroups().confirmAllStonesInUniqueGroups();
            GameContext.log(3, "GoBoard: undoInternalMove: " + getBoard() + "  groups after restoring captures:");
        }
    }
}