/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor;

import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoProfiler;
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroupSet;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

/**
 * Performs static analysis of a go board to determine groups.
 *
 * @author Barry Becker
 */
public class GroupNeighborAnalyzer {

    private GoBoard board_;
    private StringNeighborAnalyzer stringAnalyzer_;

    /**
     * Constructor
     */
    GroupNeighborAnalyzer(GoBoard board) {
        board_ = board;
        stringAnalyzer_ = new StringNeighborAnalyzer(board);
    }

    /**
     * determine a set of stones that have group connections to the specified stone.
     * This set of stones constitutes a group, but since stones cannot belong to more than
     * one group (or string) we must return a List.
     * Group connections include nobi, ikken tobi, and kogeima.
     *
     * @param stone                  the stone to search from for group neighbors.
     * @param returnToUnvisitedState if true, then mark everything unvisited when done.
     * @return the list of stones in the group that was found.
     */
    GoBoardPositionList findGroupFromInitialPosition(GoBoardPosition stone,
                                                     boolean returnToUnvisitedState) {
        GoBoardPositionList stones = new GoBoardPositionList();
        // perform a breadth first search  until all found.
        // use the visited flag to indicate that a stone has been added to the group
        GoBoardPositionList stack = new GoBoardPositionList();
        stack.add(0, stone);
        while (!stack.isEmpty()) {
            GoBoardPosition s = stack.remove(stack.size() - 1);
            if (!s.isVisited()) {
                s.setVisited(true);
                assert (s.getPiece().isOwnedByPlayer1() == stone.getPiece().isOwnedByPlayer1()) :
                        s + " does not have same ownership as " + stone;
                stones.add(s);
                pushGroupNeighbors(s, s.getPiece().isOwnedByPlayer1(), stack);
            }
        }
        if (returnToUnvisitedState) {
            stones.unvisitPositions();
        }
        return stones;
    }


    /**
     * @return all the groups on the board for both sides.
     */
    GoGroupSet findAllGroups() {
        GoGroupSet groups = new GoGroupSet();

        for (int i = 1; i <= board_.getNumRows(); i++) {
            for (int j = 1; j <= board_.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition(i, j);
                if (pos.isOccupied() && !groups.containsPosition(pos)) {
                    // would this run faster if  second param was false?
                    groups.add(new GoGroup(findGroupFromInitialPosition(pos, true)));
                }
            }
        }
        return groups;
    }

    /**
     * return a set of stones which are loosely connected to this stone.
     * Check the 16 purely group neighbors and 4 string neighbors
     * ***
     * **S**
     * *SXS*
     * **S**
     * ***
     *
     * @param stone          (not necessarily occupied)
     * @param friendPlayer1  typically stone.isOwnedByPlayer1 value of stone unless it is blank.
     * @param samePlayerOnly if true then find group nbrs that are have same ownership as friendPlayer1
     * @return group neighbors for specified stone.
     */
    GoBoardPositionSet findGroupNeighbors(GoBoardPosition stone,
                                          boolean friendPlayer1, boolean samePlayerOnly) {
        GoBoardPositionList stack = new GoBoardPositionList();

        pushGroupNeighbors(stone, friendPlayer1, stack, samePlayerOnly);
        GoBoardPositionSet nbrStones = new GoBoardPositionSet();
        nbrStones.addAll(stack);

        return nbrStones;
    }

    /**
     * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
     * Make sure diagonals are not cut nor 1-space jumps peeped.
     *
     * @param s             the position containing a stone of which to check the neighbors of.
     * @param friendPlayer1 side to find groups stones for.
     * @param stack         the stack to add unvisited neighbors.
     * @return number of stones added to the stack.
     */
    private int pushGroupNeighbors(GoBoardPosition s, boolean friendPlayer1, GoBoardPositionList stack) {
        return pushGroupNeighbors(s, friendPlayer1, stack, true);
    }

    /**
     * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
     * Make sure diagonals and 1-space jumps are not cut.
     * We currently push group neighbors even  if they are part of a string that is in atari.
     *
     * @param s             the position of a stone of which to check the neighbors of.
     * @param friendPlayer1 side to find group stones for.
     * @param stack         the stack on which we add unvisited neighbors.
     * @return number of stones added to the stack.
     */
    private int pushGroupNeighbors(GoBoardPosition s, boolean friendPlayer1, GoBoardPositionList stack,
                                   boolean samePlayerOnly) {
        GoProfiler.getInstance().startGetGroupNeightbors();
        // start with the nobi string nbrs
        int numPushed = stringAnalyzer_.pushStringNeighbors(s, friendPlayer1, stack, samePlayerOnly);

        // now push the non-nobi group neighbors
        if (!samePlayerOnly)
            numPushed += pushEnemyDiagonalNeighbors(s, friendPlayer1, stack);

        // we only find pure group neighbors of the same color
        numPushed += pushPureGroupNeighbors(s, friendPlayer1, true, stack);
        GoProfiler.getInstance().stopGetGroupNeighbors();
        return numPushed;
    }

    /**
     * Check all diagonal neighbors (at most 4).
     *
     * @param s     the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @return number of stones added to the stack
     */
    private int pushEnemyDiagonalNeighbors(GoBoardPosition s, boolean friendPlayer1,
                                           GoBoardPositionList stack) {
        int r = s.getRow();
        int c = s.getCol();
        return checkDiagonalNeighbors(r, c, !friendPlayer1, true, stack);
    }

    /**
     * Check all non-nobi group neighbors.
     *
     * @param pos          the stone of which to check the neighbors of
     * @param stack        the stack to add unvisited neighbors
     * @param sameSideOnly if true push pure group nbrs of the same side only.
     * @return number of stones added to the stack
     */
    private int pushPureGroupNeighbors(GoBoardPosition pos, boolean friendPlayer1, boolean sameSideOnly,
                                       GoBoardPositionList stack) {
        int r = pos.getRow();
        int c = pos.getCol();
        int numPushed = 0;

        // if the stone of which we are checking nbrs is in atari, then there are no pure group nbrs. no
        // if (pos.isInAtari(board_))  return 0;

        numPushed += checkDiagonalNeighbors(r, c, friendPlayer1, sameSideOnly, stack);
        numPushed += checkOneSpaceNeighbors(r, c, friendPlayer1, sameSideOnly, stack);
        numPushed += checkKogeimaNeighbors(r, c, friendPlayer1, sameSideOnly, stack);

        return numPushed;
    }

    /**
     * @return diagonal neighbors.
     */
    private int checkDiagonalNeighbors(int r, int c, boolean friendPlayer1, boolean sameSideOnly,
                                       GoBoardPositionList stack) {

        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        int numPushed = 0;

        if (r > 1 && c > 1)
            numPushed += checkDiagonalNeighbor(r, c, -1, -1, friendPlayer1, sameSideOnly, stack);
        if (r > 1 && c + 1 <= numCols)
            numPushed += checkDiagonalNeighbor(r, c, -1, 1, friendPlayer1, sameSideOnly, stack);
        if (r + 1 <= numRows && c + 1 <= numCols)
            numPushed += checkDiagonalNeighbor(r, c, 1, 1, friendPlayer1, sameSideOnly, stack);
        if (r + 1 <= numRows && c > 1)
            numPushed += checkDiagonalNeighbor(r, c, 1, -1, friendPlayer1, sameSideOnly, stack);
        return numPushed;
    }

    /**
     * @return the 1-space jumps from r,c
     */
    private int checkOneSpaceNeighbors(int r, int c, boolean friendPlayer1, boolean sameSideOnly,
                                       GoBoardPositionList stack) {
        // now check the diagonals

        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        int numPushed = 0;

        if (r > 2)
            numPushed += checkOneSpaceNeighbor(r, c, -2, 0, friendPlayer1, sameSideOnly, stack);
        if (c > 2)
            numPushed += checkOneSpaceNeighbor(r, c, 0, -2, friendPlayer1, sameSideOnly, stack);
        if (r + 2 <= numRows)
            numPushed += checkOneSpaceNeighbor(r, c, 2, 0, friendPlayer1, sameSideOnly, stack);
        if (c + 2 <= numCols)
            numPushed += checkOneSpaceNeighbor(r, c, 0, 2, friendPlayer1, sameSideOnly, stack);
        return numPushed;
    }

    /**
     * @return the diagonal moves from r,c
     */
    private int checkKogeimaNeighbors(int r, int c, boolean friendPlayer1, boolean sameSideOnly,
                                      GoBoardPositionList stack) {

        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        int numPushed = 0;

        if ((r > 2) && (c > 1))
            numPushed += checkKogeimaNeighbor(r, c, -2, -1, friendPlayer1, sameSideOnly, stack);
        if ((r > 2) && (c + 1 <= numCols))
            numPushed += checkKogeimaNeighbor(r, c, -2, 1, friendPlayer1, sameSideOnly, stack);

        if ((r + 2 <= numRows) && (c > 1))
            numPushed += checkKogeimaNeighbor(r, c, 2, -1, friendPlayer1, sameSideOnly, stack);
        if ((r + 2 <= numRows) && (c + 1 <= numCols))
            numPushed += checkKogeimaNeighbor(r, c, 2, 1, friendPlayer1, sameSideOnly, stack);

        if ((r > 1) && (c > 2))
            numPushed += checkKogeimaNeighbor(r, c, -1, -2, friendPlayer1, sameSideOnly, stack);
        if ((r + 1 <= numRows) && (c > 2))
            numPushed += checkKogeimaNeighbor(r, c, 1, -2, friendPlayer1, sameSideOnly, stack);

        if ((r > 1) && (c + 2 <= numCols))
            numPushed += checkKogeimaNeighbor(r, c, -1, 2, friendPlayer1, sameSideOnly, stack);
        if ((r + 1 <= numRows) && (c + 2 <= numCols))
            numPushed += checkKogeimaNeighbor(r, c, 1, 2, friendPlayer1, sameSideOnly, stack);
        return numPushed;
    }


    /**
     * We allow these connections as long as the diagonal has not been fully cut.
     * i.e. not an opponent stone on both sides of the cut (or the diag stone is not in atari).
     *
     * @param sameSideOnly if true then push nbrs on the same side, else push enemy nbrs
     * @return o or 1 depending on if diagonal neighbor
     */
    private int checkDiagonalNeighbor(int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean sameSideOnly,
                                      GoBoardPositionList stack) {
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        if (nbr.isUnoccupied()) {
            return 0;
        }
        // determine the side we are checking for (one or the other)
        boolean sideTest = sameSideOnly ? friendPlayer1 : !friendPlayer1;
        if ((nbr.getPiece().isOwnedByPlayer1() == sideTest) && !nbr.isVisited()) {
            BoardPosition diag1 = board_.getPosition(r + rowOffset, c);
            BoardPosition diag2 = board_.getPosition(r, c + colOffset);
            if (!isDiagonalCut(diag1, diag2, sideTest)) {
                stack.add(0, nbr);
                return 1;
            }
        }
        return 0;
    }

    private boolean isDiagonalCut(BoardPosition diag1, BoardPosition diag2, boolean sideTest) {
        return ((diag1.isOccupied() && diag1.getPiece().isOwnedByPlayer1() != sideTest) &&
                (diag2.isOccupied() && diag2.getPiece().isOwnedByPlayer1() != sideTest));
    }

    /**
     * Connected only add if not completely cut (there's no enemy stone in the middle).
     *
     * @return return 1 or 0 depending on if there si a onespace neighbor
     */
    private int checkOneSpaceNeighbor(int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean samePlayerOnly,
                                      GoBoardPositionList stack) {
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        // don't add it if it is in atari
        //if (nbr.isInAtari(board_))
        //    return 0;
        if (nbr.isOccupied() &&
                (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited()) {
            BoardPosition oneSpacePt;
            if (rowOffset == 0) {
                int col = c + (colOffset >> 1);
                oneSpacePt = board_.getPosition(r, col);
            } else {
                int row = r + (rowOffset >> 1);
                oneSpacePt = board_.getPosition(row, c);
            }
            if (!isOneSpaceCut(friendPlayer1, oneSpacePt)) {
                stack.add(0, nbr);
                return 1;
            }
        }
        return 0;
    }

    /**
     * We consider the link cut if there is an opponent piece between the 2 stones
     * eg:          *|*
     *
     * @return true if cut by eenemy stone.
     */
    private boolean isOneSpaceCut(boolean friendPlayer1, BoardPosition oneSpacePt) {
        if (oneSpacePt.isUnoccupied()) {
            return false;
        }
        return oneSpacePt.getPiece().isOwnedByPlayer1() != friendPlayer1;
    }

    /**
     * For the knight's move (kogeima) we consider it cut if there is an enemy stone at the base.
     *
     * @param stack kogeima neighbors, if found, are added to this stack.
     * @return number of kogeima neighbors added.
     */
    private int checkKogeimaNeighbor(int r, int c, int rowOffset, int colOffset,
                                     boolean friendPlayer1, boolean sameSideOnly,
                                     GoBoardPositionList stack) {
        if (!board_.inBounds(r + rowOffset, c + colOffset)) {
            return 0;
        }
        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        // don't add it if it is in atari
        //if (nbr.isInAtari(board_)) {
        //    return 0;
        //}

        if (nbr.isOccupied() &&
                (!sameSideOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited()) {

            BoardPosition intermediate1, intermediate2;
            if (Math.abs(rowOffset) == 2) {
                int rr = r + (rowOffset >> 1);
                intermediate1 = board_.getPosition(rr, c);
                intermediate2 = board_.getPosition(rr, c + colOffset);
            } else {
                int cc = c + (colOffset >> 1);
                intermediate1 = board_.getPosition(r, cc);
                intermediate2 = board_.getPosition(r + rowOffset, cc);
            }
            if (!isKogeimaCut(friendPlayer1, intermediate1, intermediate2)) {
                stack.add(0, nbr);
                return 1;
            }
        }
        return 0;
    }

    /**
     * Consider the knights move it cut if there is an opponent stone in one of the 2 spaces between.
     *
     * @return true if cut by one or more enemy sontes.
     */
    private boolean isKogeimaCut(boolean friendPlayer1, BoardPosition intermediate1, BoardPosition intermediate2) {
        return (intermediate1.isOccupied()
                && (intermediate1.getPiece().isOwnedByPlayer1() != friendPlayer1)) ||
                (intermediate2.isOccupied()
                        && (intermediate2.getPiece().isOwnedByPlayer1() != friendPlayer1));
    }
}