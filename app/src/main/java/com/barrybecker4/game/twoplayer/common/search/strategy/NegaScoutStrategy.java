/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * This strategy class defines the NegaScout search algorithm.
 * (sometimes known as principal variation search (PVS) )
 * Negascout is very much like negamax except that it uses a 0 sized search window
 * and iterative deepening.
 * See http://en.wikipedia.org/wiki/Negascout
 * <p>
 * psuedo code:<pre>
 *  int negascout(node, depth, α, β) {
 *     if node is a terminal node or depth = 0 {
 *         return the heuristic value of node
 *     }
 *     b = β                           // initial window is (-β, -α)
 *     foreach child of node {
 *        a = -negascout (child, depth-1, -b, -α)
 *        if (a>α) α = a
 *        if (α≥β) return α            // Beta cut-off
 *        if (α≥b) {                   // check if null-window failed high
 *            α = -negascout(child, depth-1, -β, -α)  // full re-search
 *            if (α≥β) return α        // Beta cut-off
 *        }
 *        b = α+1                      // set new null window
 *      }
 *    return α
 *  }
 * <p>
 * int NegaScout ( p, α, β );   {
 *    determine successors p_1,...,p_w of p
 *    if ( w = 0 )
 *       return  Evaluate(p)                // leaf
 *    b = β;
 *    for ( i = 1; i <= w; i++ ) {
 *       t = -NegaScout ( p_i, -b, -α );
 *       if (t > α) && (t < β) && (i > 1) && (d < maxdepth-1)
 *           α = -NegaScout ( p_i, -β, -t )  // re-search
 *       α = max( α, t )
 *       if ( α >= β )
 *          return  α                   // cut-off
 *       b = α + 1                      // set new null window
 *    }
 *    return α
 * }
 * <p>
 * from http://lurgee.net/abstract-strategy-games/
 * negascout(node, alpha, beta)
 *   if node is a leaf
 *       return an evaluated score for the node
 *   maxscore = alpha
 *   b = beta
 *   for each child of node
 *       v = -negascout(child, -b, -alpha)
 *       if alpha < v < beta and not the first child and depth > 1
 *             v = -negascout(child, -beta, -v)  // re-search
 *       alpha = max(alpha, v)
 *       if alpha >= beta
 *           return alpha  // cut-off
 *      b = alpha + 1      // set new null window
 *   return alpha
 * <p>
 *  </pre>
 *
 * @author Barry Becker
 */
public class NegaScoutStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends NegaMaxStrategy<M, B> {
    /**
     * Construct NegaScout strategy given a controller interface.
     */
    public NegaScoutStrategy(Searchable<M, B> controller, ParameterArray weights) {
        super(controller, weights);
    }

    @Override
    protected M findBestMove(M lastMove, int depth, MoveList<M> list,
                             SearchWindow window, SearchTreeNode parent) {
        int i = 0;
        int newBeta = window.beta;
        M selectedMove;
        M bestMove = list.getFirstMove();

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
                if (window.alpha >= window.beta) {      // beta cut-off
                    //System.out.println(getIndent(depth) + "beta cut-off1 because a=" + window.alpha + " >= " + window.beta);
                    theMove.setInheritedValue(window.alpha);
                    bestMove = theMove;
                    break;
                }
                if (window.alpha >= newBeta) {
                    // re-search with narrower window (typical alpha beta search).
                    searchable.makeInternalMove(theMove);
                    selectedMove = searchInternal(theMove, depth - 1, window.negateAndSwap(), child);
                    if (selectedMove != null) {
                        searchable.undoInternalMove(theMove);

                        selectedValue = -selectedMove.getInheritedValue();
                        theMove.setInheritedValue(selectedValue);
                        bestMove = theMove;

                        if (window.alpha >= window.beta) {
                            showPrunedNodesInTree(list, parent, i, selectedValue, window);
                            break;
                        }
                    }
                }
                i++;
                newBeta = window.alpha + 1;
            }
        }
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }
}