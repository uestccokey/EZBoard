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
 * Responsible for updating a go board after making a move.
 *
 * @author Barry Becker
 */
public class PostMoveUpdater extends PostChangeUpdater {

    /**
     * Update the board information data after a stone has been played.
     *
     * @param board          board that changed.
     * @param captureCounter captureCounter added or removed during the change
     */
    PostMoveUpdater(GoBoard board, CaptureCounts captureCounter) {
        super(board, captureCounter);
    }

    /**
     * Update the board after move has been played.
     *
     * @param move the move that was just made.
     */
    @Override
    public void update(GoMove move) {
        clearEyes();
        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToLocation()));

        board_.adjustLiberties(stone);

        GoCaptureList captures = determineCaptures(stone);
        move.setCaptures(captures);

        updateStringsAfterMove(stone);
        captures.removeFromBoard(getBoard());

        assert (stone.getString().getNumLiberties(getBoard()) > 0) :
                "The placed stone " + stone + " has no liberties " + stone.getGroup() + "\n" + getBoard().toString();
        updateGroupsAfterMove(stone);
        captureCounter_.updateCaptures(move, true);
    }

    /**
     * Determine a list of enemy stones that are captured when this stone is played on the board.
     * In other words determine all opponent strings (at most 4) whose last liberty is at the new stone location.
     *
     * @param stone stone that was just placed and caused stones to be captured (if any)
     * @return list of captured stones.  Empty if no captures.
     */
    private GoCaptureList determineCaptures(GoBoardPosition stone) {
        assert (stone != null);
        GoBoardPositionSet nbrs = nbrAnalyzer_.getNobiNeighbors(stone, NeighborType.ENEMY);
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

    /**
     * Examine the neighbors of this added stone and determine how the strings have changed.
     * For strings: examine the strongly connected neighbors. If more than one string borders, then
     * we merge the strings. If only one borders, then we add this stone to that string. If no strings
     * touch the added stone, then we create a new string containing only this stone.
     *
     * @param stone the stone that was just placed on the board.
     */
    private void updateStringsAfterMove(GoBoardPosition stone) {

        GoBoardPositionSet nbrs = nbrAnalyzer_.getNobiNeighbors(stone, NeighborType.FRIEND);

        if (nbrs.size() == 0) {
            // there are no strongly connected neighbors, create a new string
            new GoString(stone, getBoard());  // stone points to the new string
        } else {
            updateNeighborStringsAfterMove(stone, nbrs);
        }
        cleanupGroups();
    }

    /**
     * There is at least one neighbor string, so we will join to it/them.
     *
     * @param stone position where we just placed a stone.
     * @param nbrs  neighbors
     */
    private void updateNeighborStringsAfterMove(GoBoardPosition stone, GoBoardPositionSet nbrs) {
        GoBoardPosition nbrStone = nbrs.getOneMember();
        GoString str = (GoString) nbrStone.getString();
        str.addMember(stone, getBoard());

        if (nbrs.size() > 1) {
            mergeStringsIfNeeded(str, nbrs);
        }
    }

    /**
     * Then we probably need to merge the strings.
     * We will not, for example, if we are completing a clump of four.
     */
    private void mergeStringsIfNeeded(GoString str, GoBoardPositionSet nbrs) {
        for (GoBoardPosition nbrStone : nbrs) {
            // if its the same string then there is nothing to merge
            IGoString nbrString = nbrStone.getString();
            if (str != nbrString) {
                str.merge(nbrString, getBoard());
            }
        }
    }

    /**
     * First remove all the groups on the board.
     * Then for each stone, find its group and add that new group to the board's group list.
     * Continue until all stone accounted for.
     *
     * @param pos the stone that was just placed on the board.
     */
    private void updateGroupsAfterMove(GoBoardPosition pos) {

        if (GameContext.getDebugMode() > 1) {
            getAllGroups().confirmAllStonesInUniqueGroups();
        }
        recreateGroupsAfterChange();

        // verify that the string to which we added the stone has at least one liberty
        assert (pos.getString().getNumLiberties(getBoard()) > 0) :
                "The placed stone " + pos + " has no liberties " + pos.getGroup();

        ////if ( GameContext.getDebugMode() > 1 )
        ////validator_.consistencyCheck(pos);

    }
}