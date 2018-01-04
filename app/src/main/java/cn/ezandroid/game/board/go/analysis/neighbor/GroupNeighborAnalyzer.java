/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.neighbor;

import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.group.GoGroup;
import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 对棋盘进行静态分析，以确定棋群
 *
 * @author Barry Becker
 */
public class GroupNeighborAnalyzer {

    private GoBoard mBoard;
    private StringNeighborAnalyzer mStringNeighborAnalyzer;

    GroupNeighborAnalyzer(GoBoard board) {
        mBoard = board;
        mStringNeighborAnalyzer = new StringNeighborAnalyzer(board);
    }

    /**
     * 查找与指定棋子有棋群连接的所有棋子位置列表
     *
     * @param stone
     * @param returnToUnvisitedState
     * @return
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
     * 查找棋盘上的所有棋群
     *
     * @return
     */
    GoGroupSet findAllGroups() {
        GoGroupSet groups = new GoGroupSet();

        for (int i = 1; i <= mBoard.getNumRows(); i++) {
            for (int j = 1; j <= mBoard.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) mBoard.getPosition(i, j);
                if (pos.isOccupied() && !groups.containsPosition(pos)) {
                    // would this run faster if  second param was false?
                    groups.add(new GoGroup(findGroupFromInitialPosition(pos, true)));
                }
            }
        }
        return groups;
    }

    /**
     * 获取与指定棋子松散连接的棋子的集合
     * 包括如下*所示的16个标准棋群邻居和S所示的4个棋串邻居
     * <p>
     * __***
     * _**S**
     * _*SXS*
     * _**S**
     * __***
     *
     * @param stone
     * @param friendPlayer1
     * @param samePlayerOnly
     * @return
     */
    GoBoardPositionSet findGroupNeighbors(GoBoardPosition stone,
                                          boolean friendPlayer1, boolean samePlayerOnly) {
        GoBoardPositionList stack = new GoBoardPositionList();

        pushGroupNeighbors(stone, friendPlayer1, stack, samePlayerOnly);
        GoBoardPositionSet nbrStones = new GoBoardPositionSet();
        nbrStones.addAll(stack);

        return nbrStones;
    }

    private int pushGroupNeighbors(GoBoardPosition s, boolean friendPlayer1, GoBoardPositionList stack) {
        return pushGroupNeighbors(s, friendPlayer1, stack, true);
    }

    private int pushGroupNeighbors(GoBoardPosition s, boolean friendPlayer1, GoBoardPositionList stack,
                                   boolean samePlayerOnly) {
        // start with the nobi string nbrs
        int numPushed = mStringNeighborAnalyzer.pushStringNeighbors(s, friendPlayer1, stack, samePlayerOnly);

        // now push the non-nobi group neighbors
        if (!samePlayerOnly)
            numPushed += pushEnemyDiagonalNeighbors(s, friendPlayer1, stack);

        // we only find pure group neighbors of the same color
        numPushed += pushPureGroupNeighbors(s, friendPlayer1, true, stack);
        return numPushed;
    }

    private int pushEnemyDiagonalNeighbors(GoBoardPosition s, boolean friendPlayer1,
                                           GoBoardPositionList stack) {
        int r = s.getRow();
        int c = s.getCol();
        return checkDiagonalNeighbors(r, c, !friendPlayer1, true, stack);
    }

    private int pushPureGroupNeighbors(GoBoardPosition pos, boolean friendPlayer1, boolean sameSideOnly,
                                       GoBoardPositionList stack) {
        int r = pos.getRow();
        int c = pos.getCol();
        int numPushed = 0;

        // if the stone of which we are checking nbrs is in atari, then there are no pure group nbrs. no
        // if (pos.isInAtari(mBoard))  return 0;

        numPushed += checkDiagonalNeighbors(r, c, friendPlayer1, sameSideOnly, stack);
        numPushed += checkOneSpaceNeighbors(r, c, friendPlayer1, sameSideOnly, stack);
        numPushed += checkKogeimaNeighbors(r, c, friendPlayer1, sameSideOnly, stack);

        return numPushed;
    }

    private int checkDiagonalNeighbors(int r, int c, boolean friendPlayer1, boolean sameSideOnly,
                                       GoBoardPositionList stack) {
        int numRows = mBoard.getNumRows();
        int numCols = mBoard.getNumCols();
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

    private int checkOneSpaceNeighbors(int r, int c, boolean friendPlayer1, boolean sameSideOnly,
                                       GoBoardPositionList stack) {
        // now check the diagonals

        int numRows = mBoard.getNumRows();
        int numCols = mBoard.getNumCols();
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

    private int checkKogeimaNeighbors(int r, int c, boolean friendPlayer1, boolean sameSideOnly,
                                      GoBoardPositionList stack) {
        int numRows = mBoard.getNumRows();
        int numCols = mBoard.getNumCols();
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
     * 检查尖位的邻居
     *
     * @param r
     * @param c
     * @param rowOffset
     * @param colOffset
     * @param friendPlayer1
     * @param sameSideOnly
     * @param stack
     * @return
     */
    private int checkDiagonalNeighbor(int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean sameSideOnly,
                                      GoBoardPositionList stack) {
        GoBoardPosition nbr = (GoBoardPosition) mBoard.getPosition(r + rowOffset, c + colOffset);
        if (nbr.isUnoccupied()) {
            return 0;
        }
        // determine the side we are checking for (one or the other)
        boolean sideTest = sameSideOnly ? friendPlayer1 : !friendPlayer1;
        if ((nbr.getPiece().isOwnedByPlayer1() == sideTest) && !nbr.isVisited()) {
            BoardPosition diag1 = mBoard.getPosition(r + rowOffset, c);
            BoardPosition diag2 = mBoard.getPosition(r, c + colOffset);
            if (!isDiagonalCut(diag1, diag2, sideTest)) {
                stack.add(0, nbr);
                return 1;
            }
        }
        return 0;
    }

    /**
     * 是否尖被切断
     *
     * @param diag1
     * @param diag2
     * @param sideTest
     * @return
     */
    private boolean isDiagonalCut(BoardPosition diag1, BoardPosition diag2, boolean sideTest) {
        return ((diag1.isOccupied() && diag1.getPiece().isOwnedByPlayer1() != sideTest) &&
                (diag2.isOccupied() && diag2.getPiece().isOwnedByPlayer1() != sideTest));
    }

    /**
     * 检查一间跳位的棋子
     *
     * @param r
     * @param c
     * @param rowOffset
     * @param colOffset
     * @param friendPlayer1
     * @param samePlayerOnly
     * @param stack
     * @return
     */
    private int checkOneSpaceNeighbor(int r, int c, int rowOffset, int colOffset,
                                      boolean friendPlayer1, boolean samePlayerOnly,
                                      GoBoardPositionList stack) {
        GoBoardPosition nbr = (GoBoardPosition) mBoard.getPosition(r + rowOffset, c + colOffset);
        // don't add it if it is in atari
        //if (nbr.isInAtari(mBoard))
        //    return 0;
        if (nbr.isOccupied() &&
                (!samePlayerOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited()) {
            BoardPosition oneSpacePt;
            if (rowOffset == 0) {
                int col = c + (colOffset >> 1);
                oneSpacePt = mBoard.getPosition(r, col);
            } else {
                int row = r + (rowOffset >> 1);
                oneSpacePt = mBoard.getPosition(row, c);
            }
            if (!isOneSpaceCut(friendPlayer1, oneSpacePt)) {
                stack.add(0, nbr);
                return 1;
            }
        }
        return 0;
    }

    /**
     * 是否一间跳被切断
     *
     * @param friendPlayer1
     * @param oneSpacePt
     * @return
     */
    private boolean isOneSpaceCut(boolean friendPlayer1, BoardPosition oneSpacePt) {
        if (oneSpacePt.isUnoccupied()) {
            return false;
        }
        return oneSpacePt.getPiece().isOwnedByPlayer1() != friendPlayer1;
    }

    /**
     * 检查飞位的邻居
     *
     * @param r
     * @param c
     * @param rowOffset
     * @param colOffset
     * @param friendPlayer1
     * @param sameSideOnly
     * @param stack
     * @return
     */
    private int checkKogeimaNeighbor(int r, int c, int rowOffset, int colOffset,
                                     boolean friendPlayer1, boolean sameSideOnly,
                                     GoBoardPositionList stack) {
        if (!mBoard.inBounds(r + rowOffset, c + colOffset)) {
            return 0;
        }
        GoBoardPosition nbr = (GoBoardPosition) mBoard.getPosition(r + rowOffset, c + colOffset);
        // don't add it if it is in atari
        //if (nbr.isInAtari(mBoard)) {
        //    return 0;
        //}

        if (nbr.isOccupied() &&
                (!sameSideOnly || nbr.getPiece().isOwnedByPlayer1() == friendPlayer1) && !nbr.isVisited()) {
            BoardPosition intermediate1, intermediate2;
            if (Math.abs(rowOffset) == 2) {
                int rr = r + (rowOffset >> 1);
                intermediate1 = mBoard.getPosition(rr, c);
                intermediate2 = mBoard.getPosition(rr, c + colOffset);
            } else {
                int cc = c + (colOffset >> 1);
                intermediate1 = mBoard.getPosition(r, cc);
                intermediate2 = mBoard.getPosition(r + rowOffset, cc);
            }
            if (!isKogeimaCut(friendPlayer1, intermediate1, intermediate2)) {
                stack.add(0, nbr);
                return 1;
            }
        }
        return 0;
    }

    /**
     * 是否飞被切断
     *
     * @param friendPlayer1
     * @param intermediate1
     * @param intermediate2
     * @return
     */
    private boolean isKogeimaCut(boolean friendPlayer1, BoardPosition intermediate1, BoardPosition intermediate2) {
        return (intermediate1.isOccupied()
                && (intermediate1.getPiece().isOwnedByPlayer1() != friendPlayer1)) ||
                (intermediate2.isOccupied()
                        && (intermediate2.getPiece().isOwnedByPlayer1() != friendPlayer1));
    }
}