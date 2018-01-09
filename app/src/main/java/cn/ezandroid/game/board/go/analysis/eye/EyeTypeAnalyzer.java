/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye;

import java.util.Set;

import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.eye.information.E1Information;
import cn.ezandroid.game.board.go.analysis.eye.information.E2Information;
import cn.ezandroid.game.board.go.analysis.eye.information.E3Information;
import cn.ezandroid.game.board.go.analysis.eye.information.EyeInformation;
import cn.ezandroid.game.board.go.analysis.eye.information.FalseEyeInformation;
import cn.ezandroid.game.board.go.analysis.eye.information.TerritorialEyeInformation;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzer;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 眼位类型分析器
 *
 * @author Barry Becker
 */
public class EyeTypeAnalyzer {

    private IGoEye mEye;
    private GoBoard mBoard;
    private NeighborAnalyzer mNeighborAnalyzer;
    private GroupAnalyzer mGroupAnalyzer;

    public EyeTypeAnalyzer(IGoEye eye, GoBoard board, GroupAnalyzer analyzer) {
        mEye = eye;
        mBoard = board;
        mGroupAnalyzer = analyzer;
        mNeighborAnalyzer = new NeighborAnalyzer(board);
    }

    /**
     * 分析眼位信息
     *
     * @return
     */
    public EyeInformation determineEyeInformation() {
        GoBoardPositionSet spaces = mEye.getMembers();
        assert (spaces != null) : "spaces_ is null";
        int size = spaces.size();
        if (isFalseEye()) {
            return new FalseEyeInformation();
        }
        if (size == 1) {
            return new E1Information();
        }
        if (size == 2) {
            return new E2Information();
        }
        if (size == 3) {
            return new E3Information();
        }
        if (size > 3 && size < 8) {
            BigEyeAnalyzer bigEyeAnalyzer = new BigEyeAnalyzer(mEye);
            return bigEyeAnalyzer.determineEyeInformation();
        }
        return new TerritorialEyeInformation();
    }

    /**
     * 判断眼位中是否包含假眼位
     *
     * @return
     */
    private boolean isFalseEye() {
        GoBoardPositionSet spaces = mEye.getMembers();
        for (GoBoardPosition space : spaces) {
            if (isFalseEye(space)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 假眼判断规则：
     * 1，在棋盘中间，有3个或更多邻接点位被己方棋子占据，并且有2个或更多斜接点位被敌方棋子占据
     * 2，在边上或者角上，如果有2个或更多邻接点位被己方棋子占据，并且有1个或更多斜接点位被敌方棋子占据
     * 另外这些斜接的敌方棋子如果快死了或者已经死亡，则不构成假眼
     *
     * @param space
     * @return
     */
    private boolean isFalseEye(GoBoardPosition space) {
        IGoGroup ourGroup = mEye.getGroup();
        boolean groupP1 = ourGroup.isOwnedByPlayer1();
        Set nbrs = mNeighborAnalyzer.getNobiNeighbors(space, groupP1, NeighborType.FRIEND);

        if (nbrs.size() >= 2) {
            int numOppDiag = getNumOpponentDiagonals(space, groupP1);

            // now decide if false eye based on nbrs and proximity to edge.
            if (numOppDiag >= 2 && (nbrs.size() >= 3))
                return true;
            else if (mBoard.isOnEdge(space) && numOppDiag >= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取斜接的合格敌方棋子数量
     *
     * @param space
     * @param groupP1
     * @return
     */
    private int getNumOpponentDiagonals(GoBoardPosition space, boolean groupP1) {
        int numOppDiag = 0;
        int r = space.getRow();
        int c = space.getCol();

        if (qualifiedOpponentDiagonal(-1, -1, r, c, groupP1))
            numOppDiag++;
        if (qualifiedOpponentDiagonal(-1, 1, r, c, groupP1))
            numOppDiag++;
        if (qualifiedOpponentDiagonal(1, -1, r, c, groupP1))
            numOppDiag++;
        if (qualifiedOpponentDiagonal(1, 1, r, c, groupP1))
            numOppDiag++;
        return numOppDiag;
    }

    /**
     * 判断是否是合格的敌方棋子
     *
     * @param rowOffset
     * @param colOffset
     * @param r
     * @param c
     * @param groupP1
     * @return
     */
    private boolean qualifiedOpponentDiagonal(int rowOffset, int colOffset, int r, int c, boolean groupP1) {
        GoBoardPosition diagPos = (GoBoardPosition) mBoard.getPosition(r + rowOffset, c + colOffset);
        if (diagPos == null || diagPos.isUnoccupied() || diagPos.getPiece().isOwnedByPlayer1() == groupP1)
            return false;

        BoardPosition pos1 = mBoard.getPosition(r + rowOffset, c);
        BoardPosition pos2 = mBoard.getPosition(r, c + colOffset);

        return (pos1.isOccupied() && (pos1.getPiece().isOwnedByPlayer1() == groupP1) &&
                pos2.isOccupied() && (pos2.getPiece().isOwnedByPlayer1() == groupP1) &&
                mGroupAnalyzer.isTrueEnemy(diagPos));
    }
}
