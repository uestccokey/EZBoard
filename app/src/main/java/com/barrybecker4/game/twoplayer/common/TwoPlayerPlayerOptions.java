// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.game.common.player.Color;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

/**
 * Not a great name for this class. These are the options for a player in a two player game.
 * Since each player could be a computer player with different search options, the search options
 * are part of this class and not TwoPlayerOptions.
 *
 * @author Barry Becker
 */
public class TwoPlayerPlayerOptions extends PlayerOptions {

    private SearchOptions searchOptions_;

    /**
     * Default Constructor
     */
    public TwoPlayerPlayerOptions(String name, Color color) {
        super(name, color);
        searchOptions_ = createDefaultSearchOptions();
    }

    /**
     * Constructor
     *
     * @param searchOptions search options to use.
     */
    protected TwoPlayerPlayerOptions(String name, Color color, SearchOptions searchOptions) {
        this(name, color);
        searchOptions_ = searchOptions;
    }

    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions();
    }

    public SearchOptions getSearchOptions() {
        return searchOptions_;
    }

    /** Used by unit tests to set desired search options. */
    public void setSearchOptions(SearchOptions options) {
        searchOptions_ = options;
    }
}
