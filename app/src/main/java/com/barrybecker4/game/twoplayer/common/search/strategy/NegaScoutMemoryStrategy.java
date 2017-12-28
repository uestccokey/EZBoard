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
 * This strategy class defines the NegaScout with memory search algorithm.
 * This version stores the values of moves that have already been searched.
 * See http://people.csail.mit.edu/plaat/mtdf.html
 * and http://en.wikipedia.org/wiki/Negascout
 * <p>
 * Transposition table (TT) enhanced Alpha-Beta
 * (from http://www.top-5000.nl/ps/An%20Algorithm%20faster%20than%20negascout%20and%20SSS%20in%20pratice.pdf)
 * <pre>
 *   function AlphaBetaWithMemory(n, a, b) {
 *     // Check if position is in table and has been searched to sufficient depth.
 *     if (retrieve(n)) {
 *        if (n.max <= a or n.max == n.min)  return n.max;
 *        if (n.min >= b) return n.min ;
 *    }
 *    // Reached the maximum search depth
 *    if (n = leaf) {
 *      n.min = n.max = g = eval(n);
 *    }
 *    else  {
 *      g = -inf;
 *      c = firstchild(n);
 *      // Search until a cutoff occurs or all children have been considered
 *      while g < b and c != null {
 *        g = max(g, -AlphaBetaWithMemory(c, -b, -a));
 *        a = max(a, g);
 *        c = nextbrother(c);
 *      }
 *      // Save in transposition table
 *      if g <= a then n.max = g;
 *      if a < g < b then n.max = n.min = g;
 *      if g >= b then n.min = g;
 *     }
 *    store(n);
 *    return g;
 * }
 * </pre>
 *
 * @author Barry Becker
 */
public final class NegaScoutMemoryStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends NegaScoutStrategy<M, B>
        implements MemorySearchStrategy<M, B> {

    /** Stores positions that have already been evaluated, so we do not need to repeat work. */
    private TranspositionTable<M> lookupTable;

    /**
     * Constructor.
     */
    public NegaScoutMemoryStrategy(Searchable<M, B> controller, ParameterArray weights) {
        super(controller, weights);
        lookupTable = new TranspositionTable<>();
    }

    @Override
    public TranspositionTable getTranspositionTable() {
        return lookupTable;
    }

    @Override
    protected M searchInternal(M lastMove, int depth,
                               SearchWindow window, SearchTreeNode parent) {

        HashKey key = searchable.getHashKey();
        Entry<M> entry = lookupTable.get(key);
        if (lookupTable.entryExists(entry, lastMove, depth, window))
            return entry.bestMove;

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
            lastMove.setInheritedValue(sign * lastMove.getValue());
            entry = new Entry<>(lastMove, -lastMove.getInheritedValue());
            lookupTable.put(key, entry);
            return lastMove;
        }

        MoveList<M> list = searchable.generateMoves(lastMove, weights_);

        if (depth == lookAhead_)
            numTopLevelMoves_ = list.size();

        if (emptyMoveList(list, lastMove)) return null;

        return findBestMove(lastMove, depth, list, window, parent);
    }


    @Override
    protected M findBestMove(M lastMove, int depth, MoveList<M> list,
                             SearchWindow window, SearchTreeNode parent) {
        int newBeta = window.beta;
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

            // search with minimal search window
            selectedMove = searchInternal(theMove, depth - 1, new SearchWindow(-newBeta, -window.alpha), child);

            searchable.undoInternalMove(theMove);
            if (selectedMove != null) {

                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue(selectedValue);

                if (selectedValue > window.alpha) {
                    window.alpha = selectedValue;
                }
                if (window.alpha >= window.beta) {
                    theMove.setInheritedValue(window.alpha);
                    bestMove = theMove;
                    break;
                }
                if (window.alpha >= newBeta) {
                    // re-search with narrower window (typical alpha beta search).
                    searchable.makeInternalMove(theMove);
                    selectedMove = searchInternal(theMove, depth - 1, window.negateAndSwap(), child);
                    searchable.undoInternalMove(theMove);

                    selectedValue = -selectedMove.getInheritedValue();
                    theMove.setInheritedValue(selectedValue);
                    bestMove = theMove;

                    if (window.alpha >= window.beta) {
                        break;
                    }
                }
                newBeta = window.alpha + 1;
            }
        }
        storeBestMove(window, entry, bestMove.getInheritedValue());
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Store off the best move so we do not need to analyze it again.
     */
    private void storeBestMove(SearchWindow window, Entry<M> entry, int bestValue) {
        if (bestValue <= window.alpha) {
            entry.upperValue = bestValue;
        } else if (window.alpha < bestValue && bestValue < window.beta) {
            entry.lowerValue = bestValue;
            entry.upperValue = bestValue;
        } else if (bestValue >= window.beta) {
            entry.lowerValue = bestValue;
        }
        lookupTable.put(searchable.getHashKey(), entry);
    }
}