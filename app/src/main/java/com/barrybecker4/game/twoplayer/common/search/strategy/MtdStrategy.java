/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;

/**
 * Memory enhanced Test Driver search.
 * This strategy class defines the MTD search algorithm.
 * See http://people.csail.mit.edu/plaat/mtdf.html
 * <pre>
 * function MTDF(root : node_type; f : integer; d : integer) : integer;
 *    g = f;
 *    upperbound = +INFINITY;
 *    lowerbound = -INFINITY;
 *    repeat
 *       if (g == lowerbound)  beta = g + 1;
 *       else beta = g;
 *       g = AlphaBetaWithMemory(root, beta - 1, beta, d);
 *       if (g < beta) upperbound = g;
 *       else lowerbound = g;
 *    until lowerbound >= upperbound;
 *    return g;
 * </pre>
 * <p>
 * TODO: add iterative deepening (https://chessprogramming.wikispaces.com/MTD(f))
 *
 * @author Barry Becker
 */
public final class MtdStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        implements SearchStrategy<M> {
    /**
     * The "memory" search strategy to use. Must use memory/cache to avoid researching overhead.
     * Either a memory enhanced NegaMax or memory enhanced NegaScout would work.
     */
    private MemorySearchStrategy<M, B> searchWithMemory_;

    /**
     * Constructor.
     */
    public MtdStrategy(MemorySearchStrategy<M, B> testSearchStrategy) {
        searchWithMemory_ = testSearchStrategy;
    }

    @Override
    public SearchOptions getOptions() {
        return searchWithMemory_.getOptions();
    }

    @Override
    public M search(M lastMove, SearchTreeNode parent) {
        M selectedMove = searchInternal(lastMove, 0, parent);
        return (selectedMove != null) ? selectedMove : lastMove;
    }

    /**
     * @param lastMove last move played on board.
     * @param f        upper or lower bound
     * @param parent   non-null if showing game tree.
     * @return best next move
     */
    private M searchInternal(M lastMove, int f, SearchTreeNode parent) {
        int g = f;
        int upperBound = INFINITY;
        int lowerBound = -INFINITY;

        M selectedMove;
        do {
            int beta = (g == lowerBound) ? g + 1 : g;

            getOptions().getBruteSearchOptions().setInitialSearchWindow(new SearchWindow(beta - 1, beta));
            selectedMove = searchWithMemory_.search(lastMove, parent);
            g = -selectedMove.getInheritedValue();

            if (g < beta) upperBound = g;
            else lowerBound = g;

        } while (lowerBound < upperBound);
        return selectedMove;
    }


    @Override
    public final long getNumMovesConsidered() {
        return searchWithMemory_.getNumMovesConsidered();
    }

    @Override
    public final int getPercentDone() {
        return searchWithMemory_.getPercentDone();
    }

    /**
     * Set an optional ui component that will update when the search tree is modified.
     *
     * @param listener listener
     */
    @Override
    public void setGameTreeEventListener(IGameTreeViewable listener) {
        searchWithMemory_.setGameTreeEventListener(listener);
    }

    @Override
    public EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.CURRENT_PLAYER;
    }

    @Override
    public void pause() {
        searchWithMemory_.pause();
    }

    @Override
    public final boolean isPaused() {
        return searchWithMemory_.isPaused();
    }

    @Override
    public void continueProcessing() {
        searchWithMemory_.continueProcessing();
    }
}