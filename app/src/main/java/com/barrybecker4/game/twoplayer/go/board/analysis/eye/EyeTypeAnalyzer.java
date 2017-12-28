/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye;

import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E1Information;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E2Information;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E3Information;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.FalseEyeInformation;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.TerritorialEyeInformation;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.Set;

/**
 * Determine the type of an eye on the board.
 *
 * @author Barry Becker
 */
public class EyeTypeAnalyzer {

    private IGoEye eye_;
    private GoBoard board_;
    private NeighborAnalyzer nbrAnalyzer_;
    private GroupAnalyzer groupAnalyzer_;

    public EyeTypeAnalyzer(IGoEye eye, GoBoard board, GroupAnalyzer analyzer) {
        eye_ = eye;
        board_ = board;
        groupAnalyzer_ = analyzer;
        nbrAnalyzer_ = new NeighborAnalyzer(board);
    }

    /**
     * @return the eye type determined based on the properties and
     * nbrs of the positions in the spaces_ list.
     */
    public EyeInformation determineEyeInformation() {
        GoBoardPositionSet spaces = eye_.getMembers();
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
            BigEyeAnalyzer bigEyeAnalyzer = new BigEyeAnalyzer(eye_);
            return bigEyeAnalyzer.determineEyeInformation();
        }
        return new TerritorialEyeInformation();
    }

    /**
     * Iterate through the spaces_ in the eye.
     * if any are determined to be a false-eye, then return false-eye for the eye type.
     *
     * @return true if we are a false eye.
     */
    private boolean isFalseEye() {
        GoBoardPositionSet spaces = eye_.getMembers();
        for (GoBoardPosition space : spaces) {
            if (isFalseEye(space)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generally if 3 or more of the nobi neighbors are friendly,
     * and 2 or more of the diagonal nbrs are not, then it is a false eye.
     * However, if against the edge or in the corner, then 2 or more friendly nobi nbrs
     * and 1 or more enemy diagonal nbrs are needed in order to call a false eye.
     * Those enemy diagonal neighbors need to be stronger otherwise they
     * should be considered too weak to cause a false eye.
     *
     * @param space check to see if this space is part of a false eye.
     * @return true if htis is a false eye.
     */
    private boolean isFalseEye(GoBoardPosition space) {
        IGoGroup ourGroup = eye_.getGroup();
        boolean groupP1 = ourGroup.isOwnedByPlayer1();
        Set nbrs = nbrAnalyzer_.getNobiNeighbors(space, groupP1, NeighborType.FRIEND);

        if (nbrs.size() >= 2) {

            int numOppDiag = getNumOpponentDiagonals(space, groupP1);

            // now decide if false eye based on nbrs and proximity to edge.
            if (numOppDiag >= 2 && (nbrs.size() >= 3))
                return true;
            else if (board_.isOnEdge(space) && numOppDiag >= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check the diagonals for > 2 of the opponents pieces.
     * there are 2 cases: both opponent pieces on the same vertical or horizontal, or
     * the opponents pieces are on the opposite diagonals
     *
     * @return The number of diagonal points that are occupied by opponent stones (0, 1, or 2)
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
     * @return true of the enemy piece on the diagonal is relatively strong and there are group stones adjacent.
     */
    private boolean qualifiedOpponentDiagonal(int rowOffset, int colOffset, int r, int c, boolean groupP1) {
        GoBoardPosition diagPos = (GoBoardPosition) board_.getPosition(r + rowOffset, c + colOffset);
        if (diagPos == null || diagPos.isUnoccupied() || diagPos.getPiece().isOwnedByPlayer1() == groupP1)
            return false;

        BoardPosition pos1 = board_.getPosition(r + rowOffset, c);
        BoardPosition pos2 = board_.getPosition(r, c + colOffset);

        return (pos1.isOccupied() && (pos1.getPiece().isOwnedByPlayer1() == groupP1) &&
                pos2.isOccupied() && (pos2.getPiece().isOwnedByPlayer1() == groupP1) &&
                groupAnalyzer_.isTrueEnemy(diagPos));
    }
}
