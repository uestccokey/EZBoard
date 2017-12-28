/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor;

import com.barrybecker4.common.geometry.Box;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.go.board.BoardValidator;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoStringSet;

/**
 * Performs static analysis of a go board to determine strings.
 *
 * @author Barry Becker
 */
class StringNeighborAnalyzer {

    private GoBoard board_;
    private BoardValidator validator_;

    /**
     * Constructor
     *
     * @param board the board
     */
    StringNeighborAnalyzer(GoBoard board) {
        board_ = board;
        validator_ = new BoardValidator(board);
    }

    /**
     * Determines a string connected from a seed stone within a specified bounding area
     * Perform a breadth first search until all neighbors found.
     * Use the visited flag to indicate that a stone has been added to the string.
     *
     * @return string from seed stone
     */
    GoBoardPositionList findStringFromInitialPosition(GoBoardPosition stone, boolean friendOwnedByP1,
                                                      boolean returnToUnvisitedState, NeighborType type,
                                                      Box box) {
        GoBoardPositionList stones = new GoBoardPositionList();

        GoBoardPositionList stack = new GoBoardPositionList();
        assert box.contains(stone.getLocation()) : "stone " + stone + " not in " + box;

        assert (!stone.isVisited()) : "stone=" + stone;
        stack.add(0, stone);
        while (!stack.isEmpty()) {
            GoBoardPosition s = stack.pop();
            if (!s.isVisited()) {
                s.setVisited(true);
                stones.add(s);
                pushStringNeighbors(s, friendOwnedByP1, stack, true, type, box);
            }
        }
        if (returnToUnvisitedState) {
            stones.unvisitPositions();
            if (GameContext.getDebugMode() > 1)
                validator_.confirmAllUnvisited();
        }

        return stones;
    }

    /**
     * @param stone stone to find string neighbors for.
     * @return list of string neighbors of specified position.
     */
    GoStringSet findStringNeighbors(GoBoardPosition stone) {
        GoStringSet stringNbrs = new GoStringSet();
        GoBoardPositionList nobiNbrs = new GoBoardPositionList();

        pushStringNeighbors(stone, true, nobiNbrs, false);

        // add strings only once
        for (GoBoardPosition nbr : nobiNbrs) {
            stringNbrs.add(nbr.getString());
        }
        return stringNbrs;
    }

    /**
     * @return all string neighbors of specified position.
     */
    int pushStringNeighbors(GoBoardPosition s, boolean friendIsPlayer1, GoBoardPositionList stack,
                            boolean samePlayerOnly) {

        return pushStringNeighbors(s, friendIsPlayer1, stack, samePlayerOnly, NeighborType.OCCUPIED,
                new Box(1, 1, board_.getNumRows(), board_.getNumCols()));
    }

    /**
     * Check all nobi neighbors (at most 4).
     *
     * @param s     the stone of which to check the neighbors of
     * @param stack the stack to add unvisited neighbors
     * @return number of stones added to the stack
     */
    private int pushStringNeighbors(GoBoardPosition s, boolean friendPlayer1,
                                    GoBoardPositionList stack, boolean samePlayerOnly,
                                    NeighborType type, Box bbox) {
        int r = s.getRow();
        int c = s.getCol();
        int numPushed = 0;

        if (r > 1)
            numPushed += checkNeighbor(r, c, -1, 0, friendPlayer1, stack, samePlayerOnly, type, bbox);
        if (c > 1)
            numPushed += checkNeighbor(r, c, 0, -1, friendPlayer1, stack, samePlayerOnly, type, bbox);
        if (r + 1 <= bbox.getMaxRow())
            numPushed += checkNeighbor(r, c, 1, 0, friendPlayer1, stack, samePlayerOnly, type, bbox);
        if (c + 1 <= bbox.getMaxCol())
            numPushed += checkNeighbor(r, c, 0, 1, friendPlayer1, stack, samePlayerOnly, type, bbox);

        return numPushed;
    }

    /**
     * return 1 if this is a valid neighbor according to specification.
     * These are the immediately adjacent (nobi) nbrs within the specified rectangular bounds
     *
     * @return number of neighbors added (0 or 1).
     */
    private int checkNeighbor(int r, int c, int rowOffset, int colOffset,
                              boolean friendOwnedByPlayer1, GoBoardPositionList stack,
                              boolean samePlayerOnly, NeighborType type,
                              Box bbox) {

        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        if (bbox.contains(nbr.getLocation())) {
            return checkNeighbor(r, c, rowOffset, colOffset, friendOwnedByPlayer1, stack, samePlayerOnly, type);
        } else {
            return 0;
        }
    }

    /**
     * Check an immediately adjacent (nobi) nbr.
     *
     * @param r                    row
     * @param c                    column
     * @param rowOffset            offset from row indicating position of ngbor to check
     * @param colOffset            offset from column indicating position of ngbor to check
     * @param friendOwnedByPlayer1 need to specify this when the position checked, s,
     *                             is empty and has undefined ownership.
     * @param stack                if nbr fits criteria then add to stack
     * @param samePlayerOnly       mus the nbr be owned by the same player only
     * @param type                 one of REGULAR_PIECE, UNOCCUPIED, or NOT_FRIEND
     * @return 1 if this is a valid neighbor of the type that we want
     */
    private int checkNeighbor(int r, int c, int rowOffset, int colOffset,
                              boolean friendOwnedByPlayer1, GoBoardPositionList stack,
                              boolean samePlayerOnly, NeighborType type) {

        GoBoardPosition nbr = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);

        switch (type) {
            case FRIEND:
            case OCCUPIED:
                if (!nbr.isVisited() && nbr.isOccupied() &&
                        (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendOwnedByPlayer1)) {
                    stack.push(nbr);
                    return 1;
                }
                break;
            case UNOCCUPIED:
                if (!nbr.isVisited() && nbr.isUnoccupied()) {
                    stack.push(nbr);
                    return 1;
                }
                break;
            case NOT_FRIEND:
                if (!nbr.isVisited() &&
                        (nbr.isUnoccupied() ||
                                (nbr.isOccupied() && (nbr.getPiece().isOwnedByPlayer1() != friendOwnedByPlayer1))
                        )) {
                    stack.push(nbr);
                    return 1;
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported string neighbor type:" + type);
        }
        return 0;
    }
}