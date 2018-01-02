/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.analysis.neighbor;

import com.ezandroid.board.go.board.GoBoard;
import com.ezandroid.board.go.board.elements.position.GoBoardPosition;
import com.ezandroid.board.go.board.elements.position.GoBoardPositionList;
import com.ezandroid.board.go.board.elements.position.GoBoardPositionSet;
import com.ezandroid.board.go.board.elements.position.GoStone;

/**
 * Performs static analysis of a go board to determine
 * immediately adjacent, strongly connected (nobi) neighbor locations.
 *
 * @author Barry Becker
 */
public class NobiNeighborAnalyzer {

    private GoBoard board_;

    public NobiNeighborAnalyzer(GoBoard board) {
        board_ = board;
    }

    /**
     * @param empties a list of unoccupied positions.
     * @return a list of stones bordering the set of empty board positions.
     */
    GoBoardPositionSet findOccupiedNobiNeighbors(GoBoardPositionList empties) {
        GoBoardPositionSet allNbrs = new GoBoardPositionSet();
        for (GoBoardPosition empty : empties) {
            assert (empty.isUnoccupied());
            GoBoardPositionSet nbrs = getNobiNeighbors(empty, false, NeighborType.OCCUPIED);
            // add these nbrs to the set of all nbrs
            // (dupes automatically culled because HashSets only have unique members)
            allNbrs.addAll(nbrs);
        }
        return allNbrs;
    }

    /**
     * get neighboring stones of the specified stone.
     *
     * @param stone           the stone (or space) whose neighbors we are to find.
     * @param friendOwnedByP1 need to specify this in the case that the stone is a blank space
     *                        and has undefined ownership.
     * @param neighborType    (EYE, NOT_FRIEND etc)
     * @return a set of stones that are immediate (nobi) neighbors.
     */
    GoBoardPositionSet getNobiNeighbors(GoBoardPosition stone, boolean friendOwnedByP1,
                                        NeighborType neighborType) {
        GoBoardPositionSet nbrs = new GoBoardPositionSet();
        int row = stone.getRow();
        int col = stone.getCol();

        if (row > 1)
            addNobiNeighbor((GoBoardPosition) board_.getPosition(row - 1, col),
                    friendOwnedByP1, nbrs, neighborType);
        if (row + 1 <= board_.getNumRows())
            addNobiNeighbor((GoBoardPosition) board_.getPosition(row + 1, col),
                    friendOwnedByP1, nbrs, neighborType);
        if (col > 1)
            addNobiNeighbor((GoBoardPosition) board_.getPosition(row, col - 1),
                    friendOwnedByP1, nbrs, neighborType);
        if (col + 1 <= board_.getNumCols())
            addNobiNeighbor((GoBoardPosition) board_.getPosition(row, col + 1),
                    friendOwnedByP1, nbrs, neighborType);

        return nbrs;
    }

    /**
     * Get an adjacent neighbor stones restricted to the desired type.
     *
     * @param nbrStone        the neighbor to check
     * @param friendOwnedByP1 type of the center stone (can't use center.owner since center may be unnoccupied)
     * @param nbrs            hashset of the ngbors matching the criteria.
     * @param neighborType    one of the defined neighbor types.
     */
    private static void addNobiNeighbor(GoBoardPosition nbrStone, boolean friendOwnedByP1,
                                        GoBoardPositionSet nbrs, NeighborType neighborType) {
        boolean correctNeighborType = true;
        switch (neighborType) {
            case ANY:
                correctNeighborType = true;
                break;
            case OCCUPIED:
                // note friendOwnedByP1 is intentionally ignored
                correctNeighborType = nbrStone.isOccupied();
                break;
            case UNOCCUPIED:
                // note friendOwnedByP1 is intentionally ignored
                correctNeighborType = nbrStone.isUnoccupied();
                break;
            case ENEMY: // the opposite color
                if (nbrStone.isUnoccupied())
                    return;
                GoStone st = (GoStone) nbrStone.getPiece();
                correctNeighborType = st.isOwnedByPlayer1() != friendOwnedByP1;
                break;
            case FRIEND: // the same color
                if (nbrStone.isUnoccupied())
                    return;
                correctNeighborType = (nbrStone.getPiece().isOwnedByPlayer1() == friendOwnedByP1);
                break;
            case NOT_FRIEND: // the opposite color or empty
                GoStone stone = (GoStone) nbrStone.getPiece();
                correctNeighborType = (nbrStone.isUnoccupied() || stone.isOwnedByPlayer1() != friendOwnedByP1);
                break;
        }
        if (correctNeighborType) {
            nbrs.add(nbrStone);
        }
    }
}