/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.board;

import cn.ezandroid.game.board.common.Move;
import cn.ezandroid.game.board.common.MoveList;
import cn.ezandroid.game.common.geometry.Location;

/**
 * the Board describes the physical layout of the game.
 * It is an abstract class that provides a common implementation for many of the
 * methods in the IRectangularBoard.
 * Assumes an M*N grid.
 * Legal positions are [1, numRows_][1, numCols_]
 * <p>
 * Games like gomoku, go, chess, checkers, go-moku,
 * shoji, othello, connect4, squares, Stratego, Blockade fit this pattern.
 * Other games like Risk, Galactic Empire, or Monopoly and might be supportable in the future.
 * They are harder because they do not have perfect information (i.e. they use dice).
 * and have multiple players.
 *
 * @author Barry Becker
 */
public abstract class Board<M extends Move> implements IRectangularBoard<M> {

    // 棋盘位置点模型
    protected BoardPositions mPositions;

    // 落子历史列表（用来支持向前和向后导航）
    private MoveList<M> mMoveList;

    public Board() {
        mMoveList = new MoveList<>();
    }

    protected Board(Board<M> board) {
        this();
        this.setSize(board.getNumRows(), board.getNumCols());

        mMoveList = board.mMoveList.copy();
        mPositions = board.mPositions.copy();
    }

    /**
     * 重置棋盘为初始状态
     */
    @Override
    public void reset() {
        getMoveList().clear();
        mPositions.clear(getPositionPrototype());
    }

    protected BoardPosition getPositionPrototype() {
        return new BoardPosition(1, 1, null);
    }

    /**
     * 修改棋盘大小
     * <p>
     * 注意：修改大小后需要调用reset方法重置棋盘，因为原来的棋盘现在无效了
     *
     * @param numRows
     * @param numCols
     */
    @Override
    public void setSize(int numRows, int numCols) {
        mPositions = new BoardPositions(numRows, numCols);
        reset();
    }

    /**
     * 获取棋盘行数
     *
     * @return
     */
    @Override
    public final int getNumRows() {
        return mPositions.getNumRows();
    }

    /**
     * 获取棋盘列数
     *
     * @return
     */
    @Override
    public final int getNumCols() {
        return mPositions.getNumCols();
    }

    public MoveList<M> getMoveList() {
        return mMoveList;
    }

    @Override
    public BoardPosition getPosition(int row, int col) {
        return mPositions.getPosition(row, col);
    }

    @Override
    public BoardPosition getPosition(Location loc) {
        return getPosition(loc.getRow(), loc.getCol());
    }

    protected void setPosition(BoardPosition pos) {
        mPositions.setPosition(pos);
    }

    /**
     * 落子
     *
     * @param move
     * @return
     */
    @Override
    public boolean makeMove(M move) {
        boolean legal = makeInternalMove(move);
        getMoveList().add(move);
        return legal;
    }

    /**
     * 悔棋
     *
     * @return
     */
    @Override
    public M undoMove() {
        if (!getMoveList().isEmpty()) {
            M move = getMoveList().removeLast();
            undoInternalMove(move);
            return move;
        }
        return null;
    }

    @Override
    public boolean equals(Object b) {
        if (!(b instanceof Board)) return false;
        Board board = (Board) b;
        return (board.mPositions.equals(mPositions));
    }

    @Override
    public int hashCode() {
        return mPositions.hashCode();
    }

    /**
     * 落子内部函数
     *
     * @param move
     * @return
     */
    protected abstract boolean makeInternalMove(M move);

    /**
     * 悔棋内部函数
     */
    protected abstract void undoInternalMove(M move);

    /**
     * 是否指定位置在棋盘范围内
     *
     * @param r
     * @param c
     * @return
     */
    @Override
    public final boolean inBounds(int r, int c) {
        return mPositions.inBounds(r, c);
    }

    @Override
    public String toString() {
        return mPositions.toString();
    }

    /**
     * 检查是否在4个角落
     *
     * @param position
     * @return
     */
    public boolean isInCorner(BoardPosition position) {
        return mPositions.isInCorner(position);
    }

    /**
     * 检查是否在边线上（角落的点也在边线上）
     *
     * @param position
     * @return
     */
    public boolean isOnEdge(BoardPosition position) {
        return mPositions.isOnEdge(position);
    }
}
