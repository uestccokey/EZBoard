/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.transposition.Entry;
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey;
import com.barrybecker4.game.twoplayer.common.search.transposition.TranspositionTable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * This strategy class defines the NegaMax with memory search algorithm.
 * This version stores the values of moves that have already been searched.
 * Based on pseudo code from Artificial Intelligence for Games by Millington and Funge.
 *
 * @author Barry Becker
 */
public final class NegaMaxMemoryStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends NegaMaxStrategy<M, B> implements MemorySearchStrategy<M, B> {

    /** Stores positions that have already been evaluated, so we do not need to repeat work. */
    private TranspositionTable<M> lookupTable;

    /**
     * Constructor.
     */
    public NegaMaxMemoryStrategy(Searchable<M, B> controller, ParameterArray weights) {
        super(controller, weights);
        lookupTable = new TranspositionTable<>();
    }

    @Override
    public TranspositionTable getTranspositionTable() {
        return lookupTable;
    }

    @Override
    public M search(M lastMove, SearchTreeNode parent) {
        // need to negate alpha and beta on initial call.
        SearchWindow window = getOptions().getBruteSearchOptions().getInitialSearchWindow();
        int g = window.getMidPoint();
        return searchInternal(lastMove, lookAhead_, new SearchWindow(g, g), parent);
    }

    @Override
    public EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.CURRENT_PLAYER;
    }

    @Override
    protected M searchInternal(M lastMove, int depth,
                               SearchWindow window, SearchTreeNode parent) {
        HashKey key = searchable.getHashKey();
        Entry<M> entry = lookupTable.get(key);
        if (lookupTable.entryExists(entry, lastMove, depth, window)) {
            if (entry.lowerValue > window.alpha) {
                entry.bestMove.setInheritedValue(entry.lowerValue);
                return entry.bestMove;
            } else if (entry.upperValue < window.beta) {
                entry.bestMove.setInheritedValue(entry.upperValue);
                return entry.bestMove;
            }
        }

        entry = new Entry<>(lastMove, depth, new SearchWindow(-INFINITY, INFINITY));

        boolean done = searchable.done(lastMove, false);
        if (depth <= 0 || done) {
            if (doQuiescentSearch(depth, done, lastMove)) {
                M qMove = quiescentSearch(lastMove, depth, window, parent);
                if (qMove != null) {
                    entry = new Entry<>(qMove, qMove.getInheritedValue());
                    lookupTable.put(key, entry);
                    return qMove;
                }
            }
            int sign = fromPlayer1sPerspective(lastMove) ? 1 : -1;
            int value = sign * lastMove.getValue();
            lastMove.setInheritedValue(value);
            entry.lowerValue = value;
            entry.upperValue = value;
            lookupTable.put(key, entry);
            return lastMove;
        }

        MoveList<M> list = searchable.generateMoves(lastMove, weights_);

        if (depth == lookAhead_)
            numTopLevelMoves_ = list.size();

        if (emptyMoveList(list, lastMove)) return null;

        return findBestMove(lastMove, depth, list, window, parent);
    }


    /**
     * @inheritDoc
     */
    @Override
    protected M findBestMove(M lastMove, int depth, MoveList<M> list,
                             SearchWindow window, SearchTreeNode parent) {
        int i = 0;
        int bestInheritedValue = -INFINITY;
        TwoPlayerMove selectedMove;

        M bestMove = list.get(0);
        Entry<M> entry = new Entry<>(bestMove, depth, window);

        while (!list.isEmpty()) {
            M theMove = getNextMove(list);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable.makeInternalMove(theMove);
            SearchTreeNode child = addNodeToTree(parent, theMove, window);
            i++;

            selectedMove = searchInternal(theMove, depth - 1, window.negateAndSwap(), child);

            searchable.undoInternalMove(theMove);

            if (selectedMove != null) {

                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue(selectedValue);

                if (selectedValue > bestInheritedValue) {
                    bestMove = theMove;
                    entry.bestMove = theMove;
                    bestInheritedValue = selectedValue;
                    if (alphaBeta_ && bestInheritedValue >= window.beta) {
                        showPrunedNodesInTree(list, parent, i, selectedValue, window);
                        break;
                    }
                }
            }
        }
        storeBestMove(window.alpha, entry, bestMove.getInheritedValue());
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Store off the best move so we do not need to analyze it again.
     */
    private void storeBestMove(int alpha, Entry<M> entry, int bestValue) {
        if (bestValue <= alpha) {
            entry.upperValue = bestValue;
        } else {
            entry.lowerValue = bestValue;
        }
        lookupTable.put(searchable.getHashKey(), entry);
    }
}