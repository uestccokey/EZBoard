/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis;

import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoString;

/**
 * 棋串气点分析器
 * <p>
 * 跟踪气的数量，防止每次都进行计算，以提高性能
 *
 * @author Barry Becker
 */
public class StringLibertyAnalyzer {

    private GoBoardPositionSet mLiberties;

    private GoBoard mBoard;
    private GoString mString;
    private boolean mCacheValid;

    public StringLibertyAnalyzer(GoBoard board, GoString string) {
        this.mBoard = board;
        this.mString = string;
        mCacheValid = false;
    }

    /**
     * 获取棋串气点集合
     *
     * @return
     */
    public final GoBoardPositionSet getLiberties() {
        if (!mCacheValid) {
            initializeLiberties();
        }
        return mLiberties;
    }

    /**
     * 当棋串或者棋串邻接的点有变化时，调用此方法，刷新棋串气点集合
     */
    public void invalidate() {
        mCacheValid = false;
    }

    private void initializeLiberties() {
        mLiberties = new GoBoardPositionSet();
        GoBoardPositionSet members = mString.getMembers();

        for (GoBoardPosition stone : members) {
            addLiberties(stone, mBoard);
        }
        mCacheValid = true;
    }

    private void addLiberties(GoBoardPosition stone, GoBoard board) {
        int r = stone.getRow();
        int c = stone.getCol();
        if (r > 1) {
            addLiberty(board.getPosition(r - 1, c));
        }
        if (r < board.getNumRows()) {
            addLiberty(board.getPosition(r + 1, c));
        }
        if (c > 1) {
            addLiberty(board.getPosition(r, c - 1));
        }
        if (c < board.getNumCols()) {
            addLiberty(board.getPosition(r, c + 1));
        }
    }

    private void addLiberty(BoardPosition libertySpace) {
        if (libertySpace.isUnoccupied())
            mLiberties.add((GoBoardPosition) libertySpace);
    }
}