/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.neighbor;

import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;

/**
 * 对棋盘进行静态分析，以确定棋串
 *
 * @author Barry Becker
 */
class StringNeighborAnalyzer {

    private GoBoard mBoard;

    StringNeighborAnalyzer(GoBoard board) {
        mBoard = board;
    }

    /**
     * 查找指定范围内的指定种子棋子连接的棋串的位置列表
     * <p>
     * 执行广度优先搜索，直到找到所有邻居
     * 使用访问标志来表示棋子已被添加到棋串
     *
     * @param stone
     * @param friendOwnedByP1
     * @param returnToUnvisitedState
     * @param type
     * @param box
     * @return
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
        }

        return stones;
    }

    /**
     * 获取指定位置邻接的棋串集合
     *
     * @param stone
     * @return
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

    int pushStringNeighbors(GoBoardPosition s, boolean friendIsPlayer1, GoBoardPositionList stack,
                            boolean samePlayerOnly) {
        return pushStringNeighbors(s, friendIsPlayer1, stack, samePlayerOnly, NeighborType.OCCUPIED,
                new Box(1, 1, mBoard.getNumRows(), mBoard.getNumCols()));
    }

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

    private int checkNeighbor(int r, int c, int rowOffset, int colOffset,
                              boolean friendOwnedByPlayer1, GoBoardPositionList stack,
                              boolean samePlayerOnly, NeighborType type,
                              Box bbox) {
        GoBoardPosition nbr = (GoBoardPosition) mBoard.getPosition(r + rowOffset, c + colOffset);
        if (bbox.contains(nbr.getLocation())) {
            return checkNeighbor(r, c, rowOffset, colOffset, friendOwnedByPlayer1, stack, samePlayerOnly, type);
        } else {
            return 0;
        }
    }

    private int checkNeighbor(int r, int c, int rowOffset, int colOffset,
                              boolean friendOwnedByPlayer1, GoBoardPositionList stack,
                              boolean samePlayerOnly, NeighborType type) {
        GoBoardPosition nbr = (GoBoardPosition) mBoard.getPosition(r + rowOffset, c + colOffset);

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