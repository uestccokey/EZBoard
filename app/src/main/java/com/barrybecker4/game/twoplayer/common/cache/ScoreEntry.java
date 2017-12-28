/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.cache;

import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey;
import com.barrybecker4.game.twoplayer.go.board.analysis.WorthInfo;

/**
 * Holds the score and the board state
 *
 * @author Barry Becker
 */
public class ScoreEntry {

    private HashKey key;
    private int score;
    private String boardDesc;
    private WorthInfo info;

    public ScoreEntry(int score, String boardDesc) {
        this.score = score;
        this.boardDesc = boardDesc;
    }

    /** only use this for debugging. normally we do not store the key */
    public ScoreEntry(HashKey key, int score, String boardDesc, WorthInfo info) {
        this.key = key;
        this.score = score;
        this.boardDesc = boardDesc;
        this.info = info;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        return "Cached scoreEntry (for key=" + key + ")\n = " + score + " for\n" + boardDesc + "\n info=" + info;
    }
}
