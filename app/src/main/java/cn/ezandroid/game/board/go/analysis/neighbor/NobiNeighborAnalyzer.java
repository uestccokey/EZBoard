/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.neighbor;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * 对棋盘进行静态分析，以确定邻接的邻居
 *
 * @author Barry Becker
 */
public class NobiNeighborAnalyzer {

    private GoBoard mBoard;

    public NobiNeighborAnalyzer(GoBoard board) {
        mBoard = board;
    }

    /**
     * 查找指定空位置点列表周围的棋子位置集合
     *
     * @param empties
     * @return
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
     * 获取指定位置的周围的指定类型邻居的位置集合
     *
     * @param stone
     * @param friendOwnedByP1
     * @param neighborType
     * @return
     */
    GoBoardPositionSet getNobiNeighbors(GoBoardPosition stone, boolean friendOwnedByP1,
                                        NeighborType neighborType) {
        GoBoardPositionSet nbrs = new GoBoardPositionSet();
        int row = stone.getRow();
        int col = stone.getCol();

        if (row > 1)
            addNobiNeighbor((GoBoardPosition) mBoard.getPosition(row - 1, col),
                    friendOwnedByP1, nbrs, neighborType);
        if (row + 1 <= mBoard.getNumRows())
            addNobiNeighbor((GoBoardPosition) mBoard.getPosition(row + 1, col),
                    friendOwnedByP1, nbrs, neighborType);
        if (col > 1)
            addNobiNeighbor((GoBoardPosition) mBoard.getPosition(row, col - 1),
                    friendOwnedByP1, nbrs, neighborType);
        if (col + 1 <= mBoard.getNumCols())
            addNobiNeighbor((GoBoardPosition) mBoard.getPosition(row, col + 1),
                    friendOwnedByP1, nbrs, neighborType);

        return nbrs;
    }

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