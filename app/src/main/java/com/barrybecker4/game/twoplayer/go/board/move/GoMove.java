/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.move;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoStringSet;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;

import java.util.Iterator;

/**
 * Describes a change in state from one board
 * position to the next in a Go game.
 *
 * @author Barry Becker
 */
public class GoMove extends TwoPlayerMove {

    /**
     * A linked list of the pieces that were captured with this move.
     * null if there were no captures.
     */
    private GoCaptureList captureList_ = null;

    /**
     * Constructor.
     */
    public GoMove(Location destination, int val, GoStone stone) {
        super(destination, val, stone);
    }

    /** Copy constructor */
    protected GoMove(GoMove move) {
        super(move);
        if (move.captureList_ != null) {
            captureList_ = move.captureList_.copy();
        }
    }

    /**
     * make a deep copy of the move object
     */
    @Override
    public GoMove copy() {
        return new GoMove(this);
    }

    /**
     * factory method for creating a passing move
     *
     * @return new passing move
     */
    public static GoMove createPassMove(int val, boolean player1) {
        GoMove m = new GoMove(new ByteLocation(1, 1), val, null);
        m.isPass_ = true;
        m.setPlayer1(player1);
        return m;
    }

    /**
     * factory method for creating a resignation move
     *
     * @return new resignation move
     */
    public static GoMove createResignationMove(boolean player1) {
        GoMove m = new GoMove(new ByteLocation(1, 1), 0, null);
        m.isResignation_ = true;
        m.setPlayer1(player1);
        return m;
    }

    /**
     * Check if the current move is suicidal.
     * Suicidal moves (ones that kill your own pieces) are illegal.
     * Usually a move is suicidal if you play on your last liberty.
     * However, if you kill an enemy string by playing on your last liberty,
     * then it is legal.
     *
     * @return true if this move is suicidal.
     */
    public boolean isSuicidal(GoBoard board) {
        GoBoardPosition stone = (GoBoardPosition) board.getPosition(getToRow(), getToCol());

        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPositionSet nobiNbrs = na.getNobiNeighbors(stone, false, NeighborType.ANY);
        GoBoardPositionSet occupiedNbrs = new GoBoardPositionSet();
        for (GoBoardPosition pos : nobiNbrs) {
            if (pos.isOccupied()) {
                occupiedNbrs.add(pos);
            }
        }

        return !hasLiberties(occupiedNbrs, nobiNbrs) && partOfDeadString(occupiedNbrs, board);
    }

    /**
     * Can't be suicidal if we have a liberty.
     *
     * @return true if one or more liberties still available.
     */
    private boolean hasLiberties(GoBoardPositionSet occupiedNbrs, GoBoardPositionSet nobiNbrs) {
        return (nobiNbrs.size() > occupiedNbrs.size());
    }

    /**
     * If the newly placed stone captures an opponent string, then we return false.
     *
     * @param occupiedNbrs The 4 occupied Nbrs neighbors to check
     * @return true if the newly placed stone is part of a string that is now captured as a result of playing.
     */
    private boolean partOfDeadString(GoBoardPositionSet occupiedNbrs, GoBoard board) {
        for (GoBoardPosition nbr : occupiedNbrs) {
            if (nbr.getPiece().isOwnedByPlayer1() == this.isPlayer1()) {
                // friendly string
                if (nbr.getString().getNumLiberties(board) > 1) {
                    // can't be suicidal if a neighboring friendly string has > 1 liberty
                    return false;
                }
            } else {
                if (nbr.getString().getNumLiberties(board) == 1) {
                    // can't be suicidal if by playing we capture an opponent string.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * It's a ko if its a single stone string, it captured exactly one stone, and it has one liberty.
     *
     * @param board the go board
     * @return true if this move is part of a ko fight sequence.
     */
    public boolean isKo(GoBoard board) {

        if (getNumCaptures() == 1) {
            //GoBoardPosition capture = (GoBoardPosition) getCaptures().getFirst();
            GoBoardPosition pos = (GoBoardPosition) board.getPosition(getToLocation());

            NeighborAnalyzer nbrAnal = new NeighborAnalyzer(board);
            GoBoardPositionSet enemyNbrs = nbrAnal.getNobiNeighbors(pos, isPlayer1(), NeighborType.ENEMY);
            int numEnemyNbrs = enemyNbrs.size();

            if (numEnemyNbrs == 3
                    || board.isOnEdge(pos) && numEnemyNbrs == 2
                    || board.isInCorner(pos) && numEnemyNbrs == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns true if the specified move caused one or more opponent groups to be in atari
     *
     * @return a number > 0 if the move m caused an atari. The number gives the number of stones in atari.
     */
    public int numStonesAtaried(GoBoard board) {
        if (isPassingMove())
            return 0; // a pass cannot cause an atari

        GoBoardPosition pos = (GoBoardPosition) board.getPosition(getToRow(), getToCol());
        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPositionSet enemyNbrs = na.getNobiNeighbors(pos, NeighborType.ENEMY);
        Iterator it = enemyNbrs.iterator();
        int numInAtari = 0;
        GoStringSet stringSet = new GoStringSet();
        while (it.hasNext()) {
            GoBoardPosition s = (GoBoardPosition) it.next();
            IGoString atariedString = s.getString();
            if (!stringSet.contains(atariedString) && atariedString.getNumLiberties(board) == 1) {
                numInAtari += atariedString.size();
            }
            stringSet.add(atariedString); // once its in the set we won't check it again.
        }
        return numInAtari;
    }

    /**
     * I would like to avoid this setter.
     *
     * @param captures captures to set. We make a defensive copy of them.
     */
    public void setCaptures(GoCaptureList captures) {
        captureList_ = captures.copy();
    }

    public GoCaptureList getCaptures() {
        return captureList_;
    }

    public int getNumCaptures() {
        if (captureList_ != null) {
            return captureList_.size();
        } else {
            return 0;
        }
    }

    /**
     * @return stringified form.
     */
    @Override
    public String toString() {
        String s = super.toString();
        if (captureList_ != null) {
            s += "num captured=" + captureList_.size();
        }
        return s;
    }
}



