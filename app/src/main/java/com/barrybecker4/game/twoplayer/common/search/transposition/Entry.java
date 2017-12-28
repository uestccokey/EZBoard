/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;

/**
 * An entry in the transposition table.
 * We could also store a key that is more accurate than than the Zobrist key to detect if there is a collision.
 *
 * @author Barry Becker
 */
public class Entry<M extends TwoPlayerMove> {

    public M bestMove;
    public int upperValue;
    public int lowerValue;
    public int depth;

    /**
     * Constructor.
     */
    public Entry(M bestMove, int depth, SearchWindow window) {
        this.bestMove = bestMove;
        this.upperValue = window.beta;
        this.lowerValue = window.alpha;
        this.depth = depth;
    }

    /**
     * Constructor.
     * Use this version if the upper and lower bounds are the same.
     * We must be at level 0 in this case
     */
    public Entry(M bestMove, int value) {
        this.bestMove = bestMove;
        this.upperValue = value;
        this.lowerValue = value;
        this.depth = 0;
    }

    public String toString() {
        return "Entry depth=" + depth + " bestMove=" + bestMove
                + " range=[" + lowerValue + ", " + upperValue + "]";
    }
}