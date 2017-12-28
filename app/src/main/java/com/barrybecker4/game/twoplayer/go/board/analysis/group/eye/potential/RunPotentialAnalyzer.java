/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.geometry.MutableIntLocation;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;

/**
 * Figure out the "eye potential" contribution from a horizontal or vertical run within a string.
 *
 * @author Barry Becker
 */
class RunPotentialAnalyzer {

    /** We will analyze the potential of a run within this string. */
    private IGoString groupString_;
    private GoBoard board_;
    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor.
     */
    public RunPotentialAnalyzer(IGoString groupString, GoBoard board, GroupAnalyzerMap analyzerMap) {
        groupString_ = groupString;
        board_ = board;
        analyzerMap_ = analyzerMap;
    }

    /**
     * Find the potential for one of the bbox's rows or columns.
     *
     * @return eye potential for row and column at pos
     */
    public float getRunPotential(Location position, int rowInc, int colInc, int maxRow, int maxCol) {
        MutableIntLocation pos = new MutableIntLocation(position);
        float runPotential = 0;
        int breadth = (rowInc == 1) ? (maxRow - pos.getRow()) : (maxCol - pos.getCol());
        GoBoardPosition startSpace = (GoBoardPosition) board_.getPosition(pos);

        do {
            GoBoardPosition nextSpace = (GoBoardPosition) board_.getPosition(pos);
            GoBoardPosition firstSpace = nextSpace;
            boolean containsEnemy = false;
            int runLength = 0;
            boolean player1 = groupString_.isOwnedByPlayer1();
            while (inRun(pos, maxRow, maxCol, nextSpace, player1)) {
                if (containsEnemy(player1, nextSpace)) {
                    containsEnemy = true;
                }
                runLength++;
                pos.increment(rowInc, colInc);
                nextSpace = (GoBoardPosition) board_.getPosition(pos);
            }
            boolean bounded = isBounded(startSpace, nextSpace, firstSpace);
            runPotential += accrueRunPotential(rowInc, pos, breadth, firstSpace, containsEnemy, runLength, bounded);

            pos.increment(rowInc, colInc);

        } while (pos.getCol() <= maxCol && pos.getRow() <= maxRow);

        return runPotential;
    }

    private boolean containsEnemy(boolean ownedByPlayer1, GoBoardPosition space) {
        GroupAnalyzer groupAnalyzer = analyzerMap_.getAnalyzer(groupString_.getGroup());
        return space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1
                && groupAnalyzer.isTrueEnemy(space);
    }

    private boolean inRun(Location pos, int maxRow, int maxCol, GoBoardPosition space, boolean ownedByPlayer1) {
        return (pos.getCol() <= maxCol && pos.getRow() <= maxRow
                && (space.isUnoccupied() ||
                (space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1)));
    }

    private boolean isBounded(GoBoardPosition startSpace, GoBoardPosition space, GoBoardPosition firstSpace) {
        return !(firstSpace.equals(startSpace)) && space != null && space.isOccupied();
    }

    /**
     * Accumulate the potential for this run.
     *
     * @return accrued run potential.
     */
    private float accrueRunPotential(int rowInc, Location pos, int breadth,
                                     GoBoardPosition firstSpace, boolean containsEnemy,
                                     int runLength, boolean bounded) {
        float runPotential = 0;

        if (!containsEnemy && runLength < breadth && runLength > 0) {
            int firstPos, max, currentPos;
            if (rowInc == 1) {
                firstPos = firstSpace.getRow();
                max = board_.getNumRows();
                currentPos = pos.getRow();
            } else {
                firstPos = firstSpace.getCol();
                max = board_.getNumCols();
                currentPos = pos.getCol();
            }

            runPotential = new Run(firstPos, currentPos, max, bounded).getPotential();
        }

        return runPotential;
    }
}