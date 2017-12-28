/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.WinProbabilityCalculator;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.List;

/**
 * Implementation of Upper Confidence Tree (UCT) search strategy.
 * This method uses a monte carlo (stochastic) method and is fundamentally different than minimax and its derivatives.
 * It's subclasses define the key search algorithms for 2 player zero sum games with perfect information.
 * <p>
 * - add option to use concurrency. Need lock on uctNodes
 *
 * @author Barry Becker
 */
public class UctStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends AbstractSearchStrategy<M, B> {

    /** ratio of exploration to exploitation (of known good moves) while searching. */
    private double exploreExploitRatio;

    /** Number of moves to play in a random game from the starting move state */
    private int numRandomLookAhead;

    /** When selecting a random move for a random game, select from only this many of the top moves. */
    private int percentLessThanBestThresh;


    /**
     * Constructor - do not call directly.
     *
     * @param searchable the thing to be searched that has options and can make/undo moves.
     * @param weights    coefficients for the evaluation polynomial that indirectly determines the best move.
     */
    public UctStrategy(Searchable<M, B> searchable, ParameterArray weights) {
        super(searchable, weights);
        exploreExploitRatio = getOptions().getMonteCarloSearchOptions().getExploreExploitRatio();
        numRandomLookAhead = getOptions().getMonteCarloSearchOptions().getRandomLookAhead();
        percentLessThanBestThresh = getOptions().getBestMovesSearchOptions().getPercentLessThanBestThresh();
    }

    @Override
    public SearchOptions getOptions() {
        return searchable.getSearchOptions();
    }

    @Override
    public EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.ALWAYS_PLAYER1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Move UCTSearch(int numsim) {
     * root = new Node(-1,-1); //init uct tree
     * createChildren(root);
     * Board clone=new Board();
     * for (int i=0; i<numsim; i++) {
     * clone.copyStateFrom(this);
     * clone.playSimulation(root);
     * }
     * Node n = getBestChild(root);
     * return new Move(n.x, n.y);
     * }
     */
    @Override
    public M search(M lastMove, SearchTreeNode parent) {

        int numSimulations = 0;
        int maxSimulations = getOptions().getMonteCarloSearchOptions().getMaxSimulations();
        UctNode<M> root = new UctNode<>(lastMove);

        while (numSimulations < maxSimulations) {
            playSimulation(root, parent);
            numSimulations++;
            percentDone = (100 * numSimulations) / maxSimulations;
        }

        return root.findBestChildMove(getOptions().getMonteCarloSearchOptions().getMaxStyle());
    }

    /**
     * This recursive method ultimately expands the in-memory game tree by one node and updates that node's parents.
     * <p>
     * return 0=lose 1=win for current player to move
     * int playSimulation(Node n) {
     * int randomresult=0;
     * if (n.child==null && n.visits<10) { // 10 simulations until chilren are expanded (saves memory)
     * randomresult = playRandomGame();
     * }
     * else {
     * if (n.child == null)  createChildren(n);
     * Node next = UCTSelect(n); // select a move
     * if (next==null) {  ERROR }
     * makeMove(next.x, next.y);
     * int res=playSimulation(next);
     * randomresult = 1-res;
     * }
     * n.update(1-randomresult); //update node (Node-wins are associated with moves in the Nodes)
     * return randomresult;
     * }
     *
     * @return chance of player1 winning when running a simulation from this board position.
     * This probability is a number between 0 and 1 inclusive.
     */
    public float playSimulation(UctNode<M> lastMoveNode, SearchTreeNode parent) {

        float player1Score;
        if (lastMoveNode.getNumVisits() == 0) {
            player1Score = playRandomGame(lastMoveNode.move);
            movesConsidered++;
        } else {
            UctNode<M> nextNode = null;

            if (!searchable.done(lastMoveNode.move, false)) {
                if (!lastMoveNode.hasChildren()) {
                    int added = lastMoveNode.addChildren(searchable.generateMoves(lastMoveNode.move, weights_));
                    if (added == 0) {
                        GameContext.log(0, "No moves added for " + lastMoveNode);
                    }
                    addNodesToTree(parent, lastMoveNode.getChildren());
                }
                nextNode = uctSelect(lastMoveNode);
            }

            // may be null if there are no move valid moves or lastMoveNode won the game.
            if (nextNode != null) {
                searchable.makeInternalMove(nextNode.move);
                SearchTreeNode nextParent = (parent != null) ? parent.findChild(nextNode.move) : null;
                player1Score = playSimulation(nextNode, nextParent);
                searchable.undoInternalMove(nextNode.move);
            } else {
                player1Score = WinProbabilityCalculator.getChanceOfPlayer1Winning(lastMoveNode.move.getValue());
            }
        }

        lastMoveNode.update(player1Score);
        if (parent != null)
            parent.attributes = lastMoveNode.getAttributes();
        return player1Score;
    }

    /**
     * Selects the best child of parentNode.
     * <p>
     * // Larger values give uniform search
     * // Smaller values give very selective search
     * public Node UCTSelect(Node node) {
     * Node res=null;
     * Node next = node.child;
     * double best_uct=0;
     * while (next!=null) { // for all children
     * uctvalue = next.calcUctValue(exploreExploit, numVisists)
     * if (uctvalue > best_uct) { // get max uctvalue of all children
     * best_uct = uctvalue;
     * res = next;
     * }
     * next = next.sibling;
     * }
     * return res;
     * }
     *
     * @return the best child of parentNode. May be null if there are no next moves.
     */
    private UctNode<M> uctSelect(UctNode<M> parentNode) {
        double bestUct = -1.0;
        UctNode<M> selected = null;

        for (UctNode<M> child : parentNode.getChildren()) {
            double uctValue = child.calculateUctValue(exploreExploitRatio, parentNode.getNumVisits());
            if (uctValue > bestUct) {
                bestUct = uctValue;
                selected = child;
            }
        }
        return selected;
    }

    /**
     * Plays a semi-random game from the current node position.
     * Its semi random in the sense that we try to avoid obviously bad moves.
     *
     * @return a score (0 = p1 lost; 0.5 = tie; or 1= p1 won) indication p1 advantage.
     */
    private float playRandomGame(M lastMove) {

        Searchable<M, B> s = searchable.copy();
        return playRandomMove(lastMove, s, s.getNumMoves());
    }

    /**
     * Plays a semi-random game from the current node position.
     * Its semi-random in the sense that we try to avoid obviously bad moves.
     * <p>
     * <p>
     * public void makeRandomMove() {
     * int x=0;
     * int y=0;
     * while (true) {
     * x=rand.nextInt(BOARD_SIZE);
     * y=rand.nextInt(BOARD_SIZE);
     * if (f[x][y]==0 && isOnBoard(x,y))  break;
     * }
     * makeMove(x,y);
     * }
     * <p>
     * // return 0=lose 1=win for current player to move
     * int playRandomGame() {
     * int cur_player1=cur_player;
     * while (!isGameOver()) {
     * makeRandomMove();
     * }
     * return getWinner() == curplayer1 ? 1 : 0;
     *
     * @param startNumMoves how many moves have already been played.
     * @return a score (0 = p1 lost; 0.5 = tie; or 1= p1 won) indication p1 advantage.
     */
    private float playRandomMove(
            M lastMove, Searchable<M, B> searchable, int startNumMoves) {

        int numRandMoves = searchable.getNumMoves() - startNumMoves;
        if (numRandMoves >= numRandomLookAhead || searchable.done(lastMove, false)) {
            int score = searchable.worth(lastMove, weights_);
            lastMove.setValue(score);
            return WinProbabilityCalculator.getChanceOfPlayer1Winning(score);
        }
        MoveList<M> moves = searchable.generateMoves(lastMove, weights_);
        if (moves.size() == 0) {
            return WinProbabilityCalculator.getChanceOfPlayer1Winning(lastMove.getValue());
        }
        M randomMove = moves.getRandomMoveForThresh(percentLessThanBestThresh);

        searchable.makeInternalMove(randomMove);
        return playRandomMove(randomMove, searchable, startNumMoves);
    }

    /**
     * add a move to the visual game tree (if parent not null).
     * If the new node is already in the tree, do not add it, but maybe update values.
     */
    protected void addNodesToTree(SearchTreeNode parent, List<UctNode<M>> childUctNodes) {

        if (parent == null) return;

        for (UctNode<M> child : childUctNodes) {
            addNodeToTree(parent, child.move, child.getAttributes());
        }
    }
}