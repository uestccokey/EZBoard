/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.CaptureList;
import com.barrybecker4.game.twoplayer.go.board.PositionalScore;

/**
 * All the stuff used to compute the worth.
 * temp class used for debugging.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public class WorthInfo {

    private double territoryDelta;
    private double captureScore;
    private int blackCap;
    private int whiteCap;
    private double positionalScore;
    private PositionalScore[][] positionalScores;
    private CaptureList captures;
    private double worth;
    private MoveList moves;

    /**
     * Constructor.
     */
    public WorthInfo(double territoryDelta,
                     double captureScore, int blackCap, int whiteCap,
                     double positionalScore, PositionalScore[][] positionalScores,
                     CaptureList captures, double worth, MoveList moves) {
        this.territoryDelta = territoryDelta;
        this.captureScore = captureScore;
        this.blackCap = blackCap;
        this.whiteCap = whiteCap;
        this.positionalScore = positionalScore;
        this.positionalScores = positionalScores;
        this.captures = captures;
        this.worth = worth;
        this.moves = moves;
    }

    /**
     * Show all the worth information
     *
     * @return string form.
     */
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append(" territoryDelta=").append(territoryDelta).append(" captureScore=")
                .append(captureScore).append("(b=").append(blackCap).append(" w=")
                .append(whiteCap).append(") positionalScore=").append(positionalScore)
                .append(captures != null ? " captures=" + captures : "").append(" worth=")
                .append(worth).append(" \nposScores:\n");
        for (PositionalScore[] posScoreRow : positionalScores) {
            for (PositionalScore pscore : posScoreRow) {
                bldr.append(pscore);
            }
            bldr.append("\n");
        }
        bldr.append("moves=").append(moves);
        bldr.append("\n");

        return bldr.toString();
    }
}