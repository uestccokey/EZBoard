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
 * This strategy class defines the MiniMax search algorithm.
 * This is the simplest search strategy to which the other variants are compared.
 *
 * @author Barry Becker
 */
public final class MiniMaxStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends AbstractBruteSearchStrategy<M, B> {

    /**
     * Constructor for the strategy.
     */
    public MiniMaxStrategy(Searchable<M, B> controller, ParameterArray weights) {
        super(controller, weights);
    }

    @Override
    protected M findBestMove(M lastMove, int depth, MoveList<M> list,
                             SearchWindow window, SearchTreeNode parent) {
        int i = 0;
        int selectedValue;
        M selectedMove;
        // lastMove is the opponent player.
        // if player 1, then search for a high score, else search for a low score.
        boolean player1ToMove = !lastMove.isPlayer1();
        int bestInheritedValue = player1ToMove ? -SearchStrategy.INFINITY : SearchStrategy.INFINITY;

        M bestMove = list.get(0);
        while (!list.isEmpty()) {
            if (pauseInterrupted())
                return lastMove;

            M theMove = getNextMove(list);
            updatePercentDone(depth, list);

            searchable.makeInternalMove(theMove);
            SearchTreeNode child = addNodeToTree(parent, theMove, window);
            i++;

            // recursive call
            selectedMove = searchInternal(theMove, depth - 1, window.copy(), child);

            searchable.undoInternalMove(theMove);

            if (selectedMove != null) {
                selectedValue = selectedMove.getInheritedValue();
                if (player1ToMove) {
                    if (selectedValue > bestInheritedValue) {
                        bestMove = theMove;
                        bestInheritedValue = bestMove.getInheritedValue();
                    }
                } else if (selectedValue < bestInheritedValue) {
                    bestMove = theMove;
                    bestInheritedValue = bestMove.getInheritedValue();
                }

                if (alphaBeta_ && pruneAtCurrentNode(window, selectedValue, player1ToMove)) {
                    showPrunedNodesInTree(list, parent, i, selectedValue, window);
                    break;
                }
            }
        }

        bestMove.setSelected(true);
        lastMove.setInheritedValue(bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Note: The SearchWindow may be adjusted as a side effect.
     *
     * @param player1ToMove true if player one's turn to move
     * @return whether or not we should prune the current subtree.
     */
    private boolean pruneAtCurrentNode(SearchWindow window, int selectedValue, boolean player1ToMove) {
        if (player1ToMove && (selectedValue > window.beta)) {
            if (selectedValue > window.alpha) {
                return true;
            } else {
                window.beta = selectedValue;
            }
        }
        if (!player1ToMove && (selectedValue < window.alpha)) {
            if (selectedValue < window.beta) {
                return true;
            } else {
                window.alpha = selectedValue;
            }
        }
        return false;
    }

    @Override
    protected boolean fromPlayer1sPerspective(M lastMove) {
        return true;
    }

    @Override
    public EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.ALWAYS_PLAYER1;
    }
}