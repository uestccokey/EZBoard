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

        // first make sure that there are no references to obsolete groups.
        clearEyes();

        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToLocation()));

        IGoString stringThatItBelongedTo = stone.getString();
        // clearing a stone may cause a string to split into smaller strings
        stone.clear(getBoard());
        board_.adjustLiberties(stone);

        updateStringsAfterRemove(stone, stringThatItBelongedTo);
        restoreCaptures(move.getCaptures());

        recreateGroupsAfterChange();

        captureCounter_.updateCaptures(move, false);
    }

    /**
     * update the strings after a stone has been removed.
     * Some friendly strings may have been split by the removal.
     *
     * @param stone  that was removed.
     * @param string that the stone belonged to.
     */
    private void updateStringsAfterRemove(GoBoardPosition stone, IGoString string) {
        // avoid error when calling from treeDlg
        if (string == null) return;

        splitStringsIfNeeded(stone, string);

        if (GameContext.getDebugMode() > 1) {
            getAllGroups().confirmNoEmptyStrings();
            validator_.confirmStonesInValidGroups();
        }
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