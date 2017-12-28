/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.game.common.AbstractGameProfiler;
import com.barrybecker4.game.common.GameProfiler;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Base class for all classes that can search two player games for the next best move.
 *
 * @author Barry Becker
 */
public abstract class AbstractSearchable<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        implements Searchable<M, B> {

    protected MoveList<M> moveList_;
    protected SearchStrategy<M> strategy_;

    /**
     * Constructor.
     *
     * @param moveList list of moves that have been made so far.
     */
    public AbstractSearchable(MoveList<M> moveList) {
        moveList_ = moveList;
    }

    public M searchForNextMove(ParameterArray weights, M lastMove,
                               IGameTreeViewable treeViewer) {
        getProfiler().startProfiling();

        strategy_ = getSearchOptions().getSearchStrategy(this, weights);

        SearchTreeNode root = null;
        if (treeViewer != null) {
            strategy_.setGameTreeEventListener(treeViewer);
            root = treeViewer.getRootNode();
        }

        M nextMove = strategy_.search(lastMove, root);
        getProfiler().stopProfiling(strategy_.getNumMovesConsidered());
        return nextMove;
    }

    public SearchStrategy<M> getSearchStrategy() {
        return strategy_;
    }

    public int getNumMoves() {
        return moveList_.getNumMoves();
    }

    public MoveList<M> getMoveList() {
        return moveList_;
    }

    /** @return the search options to use */
    public abstract SearchOptions<M, B> getSearchOptions();

    protected AbstractGameProfiler getProfiler() {
        return GameProfiler.getInstance();
    }
}