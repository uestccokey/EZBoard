/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.transposition.TranspositionTable;

/**
 * Interface for all memory based SearchStrategies for 2 player games with perfect information.
 *
 * @author Barry Becker
 */
public interface MemorySearchStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends SearchStrategy<M> {

    /**
     * @return the internal transposition table cache. It's used to avoid recomputing move scores.
     */
    TranspositionTable getTranspositionTable();
}
