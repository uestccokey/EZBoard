/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.common.util.LRUCache;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;

/**
 * A kind of LRU cache for game moves so that we do not need to
 * recompute scores. Since lookups can use more accurate scores, alpha-beta
 * pruning can be much more effective - thereby dramatically reducing the
 * search space.
 * This technique applies the memoization pattern to search.
 * See http://en.wikipedia.org/wiki/Transposition_table
 * http://pages.cs.wisc.edu/~mjr/Pente/
 * shttp://people.csail.mit.edu/plaat/mtdf.html
 *
 * @author Barry Becker
 */
public class TranspositionTable<M extends TwoPlayerMove> extends LRUCache<HashKey, Entry<M>> {

    /** Size of the table. If bigger, will take longer before we have to recycle positions. */
    private static final int MAX_ENTRIES = 100000;

    private int cacheHits = 0;
    private int cacheNearHits = 0;
    private int cacheMisses = 0;

    public TranspositionTable() {
        super(MAX_ENTRIES);
    }

    /**
     * if we can just look up the best move in the transposition table, then just do that.
     * Has side effect of updating the lastMove with the correct boundary if cache hit.
     *
     * @return saved best move in entry
     */
    public boolean entryExists(Entry entry, M lastMove, int depth, SearchWindow window) {
        if (entry != null && entry.depth >= depth) {
            cacheHits++;
            //System.out.println("Cache hit. \nentry.depth=" + entry.depth + " depth=" + depth  + "\n" + entry);

            if (entry.upperValue <= window.alpha || entry.upperValue == entry.lowerValue) {
                entry.bestMove.setInheritedValue(entry.upperValue);
                lastMove.setInheritedValue(-entry.upperValue);
                return true;
            }
            if (entry.lowerValue >= window.beta) {
                entry.bestMove.setInheritedValue(entry.lowerValue);
                lastMove.setInheritedValue(-entry.lowerValue);
                return true;
            }
        } else {
            if (entry != null) cacheNearHits++;
            else cacheMisses++;
        }
        return false;
    }

    /**
     * @return the number of times we were able to retrieve a stored move that was useful to us.
     */
    public int getCacheHits() {
        return cacheHits;
    }

    /**
     * @return the number of times we were able to retrieve a stored move, but it was not useful to us.
     */
    public int getNearCacheHits() {
        return cacheNearHits;
    }

    /**
     * @return the number of times we looked but failed to find a stored move..
     */
    public int getCacheMisses() {
        return cacheMisses;
    }

    public String toString() {
        return "TranspositionTable [\n" + "numEntries=" + numEntries()
                + " hits" + this.getCacheHits()
                + " nearHits=" + this.getNearCacheHits()
                + " misses=" + this.getCacheMisses() + "\n]";
    }

}
