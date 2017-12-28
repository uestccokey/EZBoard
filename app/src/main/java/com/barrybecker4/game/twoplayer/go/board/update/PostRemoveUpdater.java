/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.update;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoProfiler;
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionLists;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;
import com.barrybecker4.game.twoplayer.go.board.move.GoCaptureList;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;

/**
 * Responsible for updating a go board after undoing a move.
 *
 * @author Barry Becker
 */
public class PostRemoveUpdater extends PostChangeUpdater {

    PostRemoveUpdater(GoBoard board, CaptureCounts captures) {
        super(board, captures);
    }

    /**
     * Update strings and groups after a move was undone.
     *
     * @param move move that was just removed.
     */
    @Override
    public void update(GoMove move) {

        profiler_.startUpdateGroupsAfterRemove();

        // first make sure that there are no references to obsolete groups.
        clearEyes();

        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToLocation()));

        IGoString stringThatItBelongedTo = stone.getString();
        // clearing a stone may cause a string to split into smaller strings
        stone.clear(getBoard());
        board_.adjustLiberties(stone);

        updateStringsAfterRemove(stone, stringThatItBelongedTo);
        restoreCaptures(move.getCaptures());

        profiler_.startRecreateGroupsAfterRemove();
        recreateGroupsAfterChange();
        profiler_.stopRecreateGroupsAfterRemove();

        captureCounter_.updateCaptures(move, false);
        profiler_.stopUpdateGroupsAfterRemove();
    }

    /**
     * update the strings after a stone has been removed.
     * Some friendly strings may have been split by the removal.
     *
     * @param stone  that was removed.
     * @param string that the stone belonged to.
     */
    private void updateStringsAfterRemove(GoBoardPosition stone, IGoString string) {
        GoProfiler profiler = GoProfiler.getInstance();
        profiler.startUpdateStringsAfterRemove();

        // avoid error when calling from treeDlg
        if (string == null) return;

        splitStringsIfNeeded(stone, string);

        if (GameContext.getDebugMode() > 1) {
            getAllGroups().confirmNoEmptyStrings();
            validator_.confirmStonesInValidGroups();
        }
        profiler.stopUpdateStringsAfterRemove();
    }

    /**
     * Make new string(s) if removing the stone has caused a larger string to be split.
     *
     * @param stone  that was removed.
     * @param string that the stone belonged to.
     */
    private void splitStringsIfNeeded(GoBoardPosition stone, IGoString string) {

        IGoGroup group = string.getGroup();
        GoBoardPositionSet nbrs =
                nbrAnalyzer_.getNobiNeighbors(stone, group.isOwnedByPlayer1(), NeighborType.FRIEND);

        if (nbrs.size() > 1) {
            GoBoardPositionLists lists = new GoBoardPositionLists();
            GoBoardPosition firstNbr = nbrs.getOneMember();
            GoBoardPositionList stones = nbrAnalyzer_.findStringFromInitialPosition(firstNbr, false);
            lists.add(stones);
            for (GoBoardPosition nbrStone : nbrs) {
                if (!nbrStone.isVisited()) {
                    GoBoardPositionList stones1 = nbrAnalyzer_.findStringFromInitialPosition(nbrStone, false);
                    IGoString newString = new GoString(stones1, getBoard());
                    group.addMember(newString);
                    lists.add(stones1);
                }
            }
            lists.unvisitPositionsInLists();
        }
    }

    /**
     * restore this moves captures stones on the board
     *
     * @param captures list of captures to remove.
     */
    private void restoreCaptures(GoCaptureList captures) {

        if (captures == null || captures.isEmpty()) return;

        captures.restoreOnBoard(board_);
        if (GameContext.getDebugMode() > 1) {
            validator_.confirmStonesInValidGroups();
            getAllGroups().confirmAllStonesInUniqueGroups();
            GameContext.log(3, "GoBoard: undoInternalMove: " + getBoard() + "  groups after restoring captures:");
        }
    }
}