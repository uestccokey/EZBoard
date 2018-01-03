/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.position;

import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.IGoMember;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 围棋位置点模型
 *
 * @author Barry Becker
 */
public final class GoBoardPosition extends BoardPosition implements IGoMember {

    // 该点所属的棋串
    private IGoString mString;

    // 该点所属的眼位
    private IGoEye mEye;

    // 是否被访问过
    private boolean mIsVisited;

    // 得分
    private double mScore;

    public GoBoardPosition(int row, int col, IGoString string, GoStone stone) {
        super(row, col, stone);
        mString = string;
        mEye = null;
        mIsVisited = false;
    }

    public GoBoardPosition(GoBoardPosition pos) {
        this(pos.mLocation.getRow(), pos.mLocation.getCol(), pos.mString,
                (pos.mPiece == null) ? null : (GoStone) pos.mPiece.copy());
        pos.setEye(getEye());
        setVisited(pos.isVisited());
    }

    @Override
    public GoBoardPosition copy() {
        return new GoBoardPosition(this);
    }

    public void setString(IGoString string) {
        mString = string;
    }

    public IGoString getString() {
        return mString;
    }

    public void setEye(IGoEye eye) {
        mEye = eye;
    }

    public IGoEye getEye() {
        return mEye;
    }

    public IGoGroup getGroup() {
        if (mString != null)
            return mString.getGroup();
        if (mEye != null) {
            return mEye.getGroup();
        }
        return null;
    }

    public void setVisited(boolean visited) {
        mIsVisited = visited;
    }

    public boolean isVisited() {
        return mIsVisited;
    }

    public boolean isInEye() {
        return mEye != null;
    }

    /**
     * 该位置点的气的数量
     *
     * @param board
     * @return
     */
    public int getNumLiberties(GoBoard board) {
        int numLiberties = 0;
        int row = getRow();
        int col = getCol();
        if (row > 1 && board.getPosition(row - 1, col).isUnoccupied())
            numLiberties++;
        if (row < board.getNumRows() && board.getPosition(row + 1, col).isUnoccupied())
            numLiberties++;
        if (col > 1 && board.getPosition(row, col - 1).isUnoccupied())
            numLiberties++;
        if (col < board.getNumCols() && board.getPosition(row, col + 1).isUnoccupied())
            numLiberties++;

        return numLiberties;
    }

    public void clear(GoBoard board) {
        IGoString string = getString();

        if (string != null) {
            string.remove(this, board);
        } else {
            assert isUnoccupied();
        }
        clear();
    }

    @Override
    public void clear() {
        super.clear();
        setString(null);
        setEye(null);
        mScore = 0;
        setVisited(false);
    }

    public double getScore() {
        return mScore;
    }

    public void setScore(double score) {
        this.mScore = score;
    }
}
