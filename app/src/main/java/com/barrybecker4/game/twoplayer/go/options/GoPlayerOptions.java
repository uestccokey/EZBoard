// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.options;

import com.barrybecker4.game.common.player.Color;
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;

/**
 * @author Barry Becker
 */
public class GoPlayerOptions extends TwoPlayerPlayerOptions {

    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 4;

    /** for any given ply never consider less than this of the top moves. */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 0;

    /** for any given ply never consider more than BEST_PERCENTAGE of the top moves. Not used for go */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 60;

    /** for any given ply never consider less than this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 6;

    /** Constructor */
    public GoPlayerOptions(String name, Color color) {
        super(name, color);
    }

    /** Constructor */
    public GoPlayerOptions(String name, SearchOptions searchOptions) {
        super(name, null, searchOptions);
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        SearchOptions opts = new SearchOptions(new BruteSearchOptions(DEFAULT_LOOK_AHEAD, 16),
                new BestMovesSearchOptions(DEFAULT_PERCENTAGE_BEST_MOVES,
                        DEFAULT_PERCENT_LESS_THAN_BEST_THRESH, DEFAULT_MIN_BEST_MOVES
                ),
                new MonteCarloSearchOptions(200, 0.9, 10));
        opts.setSearchStrategyMethod(SearchStrategyType.NEGASCOUT);
        opts.getBruteSearchOptions().setQuiescence(true);
        return opts;
    }
}
