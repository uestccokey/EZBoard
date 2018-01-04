/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.neighbor;

import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.group.GoGroupSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoString;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 对棋盘进行静态分析，确定棋群，棋串及紧邻的邻居位置
 * 也作为其他邻居分析器的外观（facade模式）
 *
 * @author Barry Becker
 */
public class NeighborAnalyzer {

    private GoBoard mBoard;
    private GroupNeighborAnalyzer mGroupNeighborAnalyzer;
    private StringNeighborAnalyzer mStringNeighborAnalyzer;
    private NobiNeighborAnalyzer mNobiNeighborAnalyzer;

    public NeighborAnalyzer(GoBoard board) {
        mBoard = board;
        mGroupNeighborAnalyzer = new GroupNeighborAnalyzer(board);
        mStringNeighborAnalyzer = new StringNeighborAnalyzer(board);
        mNobiNeighborAnalyzer = new NobiNeighborAnalyzer(board);
    }

    /**
     * 查找指定空位置点列表周围的棋子位置集合
     *
     * @param empties
     * @return
     */
    public GoBoardPositionSet findOccupiedNobiNeighbors(GoBoardPositionList empties) {
        return mNobiNeighborAnalyzer.findOccupiedNobiNeighbors(empties);
    }

    public GoBoardPositionSet getNobiNeighbors(GoBoardPosition stone, NeighborType neighborType) {
        return getNobiNeighbors(stone, stone.getPiece().isOwnedByPlayer1(), neighborType);
    }

    /**
     * 获取指定位置的周围的指定类型邻居的位置集合
     *
     * @param stone
     * @param friendOwnedByP1
     * @param neighborType
     * @return
     */
    public GoBoardPositionSet getNobiNeighbors(GoBoardPosition stone, boolean friendOwnedByP1,
                                               NeighborType neighborType) {
        return mNobiNeighborAnalyzer.getNobiNeighbors(stone, friendOwnedByP1, neighborType);
    }

    public GoBoardPositionList findStringFromInitialPosition(GoBoardPosition stone,
                                                             boolean returnToUnvisitedState) {
        return findStringFromInitialPosition(
                stone, stone.getPiece().isOwnedByPlayer1(), returnToUnvisitedState, NeighborType.OCCUPIED,
                new Box(1, 1, mBoard.getNumRows(), mBoard.getNumCols()));
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
    public GoBoardPositionList findStringFromInitialPosition(GoBoardPosition stone, boolean friendOwnedByP1,
                                                             boolean returnToUnvisitedState, NeighborType type, Box box) {
        return mStringNeighborAnalyzer.findStringFromInitialPosition(stone, friendOwnedByP1, returnToUnvisitedState, type, box);
    }

    /**
     * 获取指定位置邻接的棋串集合
     *
     * @param position
     * @return
     */
    public GoStringSet findStringNeighbors(GoBoardPosition position) {
        return mStringNeighborAnalyzer.findStringNeighbors(position);
    }

    public GoBoardPositionSet findGroupNeighbors(GoBoardPosition position, boolean samePlayerOnly) {
        assert (position.getPiece() != null) : "Position " + position + " does not have a piece!";
        return findGroupNeighbors(position, position.getPiece().isOwnedByPlayer1(), samePlayerOnly);
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
    public GoBoardPositionSet findGroupNeighbors(GoBoardPosition stone, boolean friendPlayer1,
                                                 boolean samePlayerOnly) {
        return mGroupNeighborAnalyzer.findGroupNeighbors(stone, friendPlayer1, samePlayerOnly);
    }

    public GoBoardPositionList findGroupFromInitialPosition(GoBoardPosition stone) {
        return findGroupFromInitialPosition(stone, true);
    }

    /**
     * 查找与指定棋子有棋群连接的所有棋子位置列表
     *
     * @param stone
     * @param returnToUnvisitedState
     * @return
     */
    public GoBoardPositionList findGroupFromInitialPosition(GoBoardPosition stone, boolean returnToUnvisitedState) {
        return mGroupNeighborAnalyzer.findGroupFromInitialPosition(stone, returnToUnvisitedState);
    }

    /**
     * 查找棋盘上的所有棋群
     *
     * @return
     */
    public GoGroupSet findAllGroupsOnBoard() {
        return mGroupNeighborAnalyzer.findAllGroups();
    }

    /**
     * 这是一个昂贵的操作，因为它清除了棋盘上的所有已知信息，并且重新创建了所有棋串
     * 它有更新棋盘状态的副作用，调用时需要小心
     *
     * @return
     */
    public GoStringSet determineAllStringsOnBoard() {
        clearEyes();
        GoStringSet strings = new GoStringSet();
        for (int i = 1; i <= mBoard.getNumRows(); i++) {
            for (int j = 1; j <= mBoard.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) mBoard.getPosition(i, j);
                if (pos.isOccupied()) {
                    IGoString existingString = strings.findStringContainingPosition(pos);
                    if (existingString == null) {
                        GoString str = new GoString(findStringFromInitialPosition(pos, true), mBoard);
                        strings.add(str);
                        pos.setString(str);
                    } else {
                        pos.setString(existingString);
                    }
                }
            }
        }
        return strings;
    }

    private void clearEyes() {
        for (int i = 1; i <= mBoard.getNumRows(); i++) {
            for (int j = 1; j <= mBoard.getNumCols(); j++) {
                GoBoardPosition pos = (GoBoardPosition) mBoard.getPosition(i, j);
                pos.setEye(null);
                pos.setString(null);
                pos.setVisited(false);
                //pos.clear();
            }
        }
    }
}