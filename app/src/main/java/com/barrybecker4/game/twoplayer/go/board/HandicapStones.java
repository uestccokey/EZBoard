/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board;

import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;

import java.util.ArrayList;
import java.util.List;

/**
 * The number of star points used for handicap stones on the board
 * There may be none.
 * <p>
 * Immutable
 *
 * @author Barry Becker
 */
class HandicapStones {

    /** Initially the handicap stones have this health score */
    private static final float HANDICAP_STONE_HEALTH = 0.8f;

    /** the number of initial handicap stones to use. */
    private int numHandicapStones_ = 0;

    /** typically there are at most 9 handicap stones in an uneven game */
    private GoBoardPositionList starPoints_ = null;

    /**
     * Constructor
     * You cannot change the number of handicap stones after construction.
     *
     * @param num       number of handicap stones
     * @param boardSize on one side.
     */
    HandicapStones(int num, int boardSize) {
        initStarPoints(boardSize);
        numHandicapStones_ = num;
    }

    /**
     * @return number of handicap stones in this set.
     */
    public int getNumber() {
        return numHandicapStones_;
    }

    public List getStarPoints() {
        return starPoints_;
    }

    /**
     * specify the number of handicap stones that will actually be used this game.
     * public since we might set if from the options dialog
     *
     * @return handicap stones
     */
    public List<GoMove> getHandicapMoves() {
        assert numHandicapStones_ <= starPoints_.size();
        List<GoMove> handicapMoves = new ArrayList<>(numHandicapStones_);

        for (int i = 0; i < numHandicapStones_; i++) {
            GoBoardPosition hpos = starPoints_.get(i);

            GoMove m = new GoMove(hpos.getLocation(), 0, (GoStone) hpos.getPiece());
            handicapMoves.add(m);
        }
        return handicapMoves;
    }

    /**
     * initialize a list of stones at the star points
     */
    private void initStarPoints(int boardSize) {
        // initialize the list of handicap stones.
        // The number of these that actually get placed on the board
        // depends on the handicap
        starPoints_ = new GoBoardPositionList();
        int min = 4;
        // on a really small board we put the corner star points at 3-3.
        if (boardSize < 13)
            min = 3;
        int max = boardSize - (min - 1);
        int mid = (boardSize >> 1) + 1;

        // add the star points
        GoStone handicapStone = new GoStone(true, HANDICAP_STONE_HEALTH);
        starPoints_.add(new GoBoardPosition(min, min, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(max, max, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(min, max, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(max, min, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(min, mid, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(max, mid, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(mid, min, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(mid, max, null, handicapStone.copy()));
        starPoints_.add(new GoBoardPosition(mid, mid, null, handicapStone));
    }
}