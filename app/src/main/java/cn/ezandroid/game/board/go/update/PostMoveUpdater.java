/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoString;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;
import cn.ezandroid.game.board.go.move.GoCaptureList;
import cn.ezandroid.game.board.go.move.GoMove;

/**
 * 落子后的棋盘更新器
 *
 * @author Barry Becker
 */
public class PostMoveUpdater extends PostChangeUpdater {

    PostMoveUpdater(GoBoard board, CaptureCounts captureCounter) {
        super(board, captureCounter);
    }

    @Override
    public void update(GoMove move) {
        clearEyes();
        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToLocation()));

        mBoard.adjustLiberties(stone);

        GoCaptureList captures = determineCaptures(stone);
        move.setCaptures(captures);

        updateStringsAfterMove(stone);
        captures.removeFromBoard(getBoard());

        assert (stone.getString().getNumLiberties(getBoard()) > 0) :
                "The placed stone " + stone + " has no liberties " + stone.getGroup() + "\n" + getBoard().toString();
        updateGroupsAfterMove(stone);
        mCaptureCounts.updateCaptures(move, true);
    }

    /**
     * 确定落子后的提子列表
     *
     * @param stone
     * @return
     */
    private GoCaptureList determineCaptures(GoBoardPosition stone) {
        assert (stone != null);
        GoBoardPositionSet nbrs = mNeighborAnalyzer.getNobiNeighbors(stone, NeighborType.ENEMY);
        GoCaptureList captureList = new GoCaptureList();
        // keep track of the strings captured so we don't capture the same one twice
        GoStringSet capturedStrings = new GoStringSet();

        for (GoBoardPosition enbr : nbrs) {
            assert (enbr.isOccupied()) : "enbr=" + enbr;

            IGoString str = enbr.getString();
            assert (str.isOwnedByPlayer1() != stone.getPiece().isOwnedByPlayer1()) :
                    "The " + str + " is not an enemy of " + stone;
            assert (str.size() > 0) : "Sting has 0 stones:" + str;

            if (str.getNumLiberties(getBoard()) == 0 && !capturedStrings.contains(str)) {
                capturedStrings.add(str);
                // we need to add copies so that when the original stones on the board are
                // changed we don't change the captures.
                captureList.addCaptures(str.getMembers());
            }
        }
        return captureList;
    }

    private void updateStringsAfterMove(GoBoardPosition stone) {
        GoBoardPositionSet nbrs = mNeighborAnalyzer.getNobiNeighbors(stone, NeighborType.FRIEND);

        if (nbrs.size() == 0) {
            // there are no strongly connected neighbors, create a new string
            new GoString(stone, getBoard());  // stone points to the new string
        } else {
            updateNeighborStringsAfterMove(stone, nbrs);
        }
        cleanupGroups();
    }

    private void updateNeighborStringsAfterMove(GoBoardPosition stone, GoBoardPositionSet nbrs) {
        GoBoardPosition nbrStone = nbrs.getOneMember();
        GoString str = (GoString) nbrStone.getString();
        str.addMember(stone, getBoard());

        if (nbrs.size() > 1) {
            mergeStringsIfNeeded(str, nbrs);
        }
    }

    private void mergeStringsIfNeeded(GoString str, GoBoardPositionSet nbrs) {
        for (GoBoardPosition nbrStone : nbrs) {
            // if its the same string then there is nothing to merge
            IGoString nbrString = nbrStone.getString();
            if (str != nbrString) {
                str.merge(nbrString, getBoard());
            }
        }
    }

    private void updateGroupsAfterMove(GoBoardPosition pos) {
        if (GameContext.getDebugMode() > 1) {
            getAllGroups().confirmAllStonesInUniqueGroups();
        }
        recreateGroupsAfterChange();

        // verify that the string to which we added the stone has at least one liberty
        assert (pos.getString().getNumLiberties(getBoard()) > 0) :
                "The placed stone " + pos + " has no liberties " + pos.getGroup();
    }
}