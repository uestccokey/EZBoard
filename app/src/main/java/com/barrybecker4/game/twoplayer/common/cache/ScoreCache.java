/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.cache;

import com.barrybecker4.common.util.LRUCache;
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey;

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
public class ScoreCache extends LRUCache<HashKey, ScoreEntry> {

    /** Size of the table. If bigger, it will take longer before we have to recycle positions. */
    private static final int MAX_ENTRIES = 110000;

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public ScoreCache() {
        super(MAX_ENTRIES);
    }

    /**
     * @return the number of times we were able to retrieve a stored move that was useful to us.
     */
    public int getCacheHits() {
        return cacheHits;
    }

    /**
     * @return the number of times we looked but failed to find a stored move..
     */
    public int getCacheMisses() {
        return cacheMisses;
    }

    @Override
    public ScoreEntry get(HashKey key) {
        ScoreEntry score = super.get(key);
        if (score == null)
            cacheMisses++;
        else
            cacheHits++;
        return score;
    }

    public String toString() {
        return "ScoreCache [\n" + "numEntries=" + numEntries() + " hits" +
                getCacheHits() + " misses=" + this.getCacheMisses() + "\n]";
    }
}
