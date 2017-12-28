/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.options;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Encapsulate two player search options here to keep the TwoPlayerController class much simpler.
 * While some options are used for all strategies,
 * some Search strategies with different SearchAttributes use different sets of options.
 *
 * @author Barry Becker
 */
public class SearchOptions<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>> {

    public static final SearchStrategyType DEFAULT_STRATEGY_METHOD = SearchStrategyType.NEGASCOUT;

    /** The default search method. */
    private SearchStrategyType strategyMethod_ = DEFAULT_STRATEGY_METHOD;

    private BruteSearchOptions bruteOptions_;
    private MonteCarloSearchOptions monteCarloOptions_;
    private BestMovesSearchOptions bestMovesOptions_;

    /**
     * Default Constructor
     */
    public SearchOptions() {
        this(new BruteSearchOptions(), new BestMovesSearchOptions(), new MonteCarloSearchOptions());
    }

    /**
     * Constructor
     */
    public SearchOptions(SearchStrategyType algorithm) {
        this(algorithm, new BruteSearchOptions(), new BestMovesSearchOptions(), new MonteCarloSearchOptions());
    }

    /**
     * Constructor
     *
     * @param bruteOptions brute force search options to use.
     */
    public SearchOptions(BruteSearchOptions bruteOptions,
                         BestMovesSearchOptions bestMovesOptions,
                         MonteCarloSearchOptions mcOptions) {
        this(DEFAULT_STRATEGY_METHOD, bruteOptions, bestMovesOptions, mcOptions);
    }

    /**
     * Constructor
     */
    public SearchOptions(SearchStrategyType algorithm,
                         BruteSearchOptions bruteOptions,
                         BestMovesSearchOptions bestMovesOptions,
                         MonteCarloSearchOptions mcOptions) {
        strategyMethod_ = algorithm;
        bruteOptions_ = bruteOptions;
        bestMovesOptions_ = bestMovesOptions;
        monteCarloOptions_ = mcOptions;
    }

    /**
     * Constructor
     *
     * @param bruteOptions brute force search options to use.
     */
    public SearchOptions(BruteSearchOptions bruteOptions, BestMovesSearchOptions bestMovesOptions) {
        this(bruteOptions, bestMovesOptions, new MonteCarloSearchOptions());
    }

    /**
     * @return the strategy method currently being used.
     */
    public SearchStrategyType getSearchStrategyMethod() {
        return strategyMethod_;
    }

    /**
     * @param method the desired search strategy for evaluating the game tree.
     *               (eg MINIMAX, NEGAMAX, etc)
     */
    public final void setSearchStrategyMethod(SearchStrategyType method) {
        strategyMethod_ = method;
    }

    public BruteSearchOptions getBruteSearchOptions() {
        return bruteOptions_;
    }

    public BestMovesSearchOptions getBestMovesSearchOptions() {
        return bestMovesOptions_;
    }

    public MonteCarloSearchOptions getMonteCarloSearchOptions() {
        return monteCarloOptions_;
    }

    /**
     * @param searchable something that can be searched.
     * @return the search strategy to use given a searchable object.
     */
    public SearchStrategy<M> getSearchStrategy(
            Searchable<M, B> searchable,
            ParameterArray weights) {

        SearchStrategyType type = getSearchStrategyMethod();
        return (SearchStrategy<M>) type.createStrategy(searchable, weights);
    }

    public String toString() {
        return "SearchOptions " + "strategy=" + strategyMethod_
                + "\\n" + bruteOptions_.toString()
                + "\\n" + bestMovesOptions_.toString()
                + "\\n" + monteCarloOptions_.toString()
                + "\\n";
    }
}