/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.WinProbabilityCalculator;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.tree.NodeAttributes;

import java.util.LinkedList;
import java.util.List;

/**
 * A node in the in memory UCT tree created during search.
 * derived from http://senseis.xmp.net/?UCT%2FDiscussion
 *
 * @author Barry Becker
 */
public class UctNode<M extends TwoPlayerMove> {

    /** The number of times we have visited this node in the tree. */
    private int numVisits;

    /** The move this node represents. */
    public M move;

    /** The number of times we have won a random game that starts from this node. */
    private float numWins;

    /** List of child nodes (moves) */
    private List<UctNode<M>> children;

    /**
     * not sure what this is for. See http://senseis.xmp.net/?UCT. Make a param.
     * Seems to make the exploreExploit constant balance at 1.
     */
    private static final double DENOM_CONST = 5.0;

    /** Some big number. */
    private static final double BIG = 1000;

    /**
     * Constructor.
     *
     * @param move the move we represent
     */
    public UctNode(M move) {
        this.move = move;
    }

    public int getNumVisits() {
        return numVisits;
    }

    /**
     * Increment our number of wins we won again at this node.
     * A tie counts as only half a win.
     * visits++;
     * wins += val;
     *
     * @param player1Score if 1 then p1 won; if 0, then p1 lost, else 0.5 - considered a tie (inconclusive)
     */
    public void update(double player1Score) {
        numVisits++;
        numWins += (move.isPlayer1()) ? player1Score : 1.0 - player1Score;
    }

    /**
     * Note that the winRate returned is for the player who is about to move.
     * if (visits > 0) return (double) wins / visits;
     * else return 0;     // should not happen
     *
     * @return ratio of wins to visits. Return a tie score if never visited.
     */
    public float getWinRate() {
        return (numVisits == 0) ?
                WinProbabilityCalculator.getChanceOfPlayer1Winning(move.getValue()) :
                numWins / (float) numVisits;
    }

    public List<UctNode<M>> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children != null;
    }

    /**
     * Add the children to the node.
     * <p>
     * // expand children in Node
     * void createChildren(Node parent) {
     * Node last=parent;
     * for (int i=0; i<BOARD_SIZE; i++)
     * for (int j=0; j<BOARD_SIZE; j++)
     * if (isOnBoard(i, j) && f[i][j]==0) {
     * Node node=new Node(i, j);
     * if (last==parent) last.child=node;
     * else last.sibling=node;
     * last=node;
     * }
     * }
     *
     * @param moves child moves to add.
     * @return the number of children added
     */
    public int addChildren(MoveList<M> moves) {
        int numKids = 0;
        children = new LinkedList<>();
        for (M m : moves) {
            UctNode<M> newNode = new UctNode<>(m);
            children.add(newNode);
            numKids++;
        }
        return numKids;
    }

    /**
     * Need to determine if the metric should be number of visits or winrate,
     * <p>
     * Find child with highest number of visits is used (not: best winrate) (recent change on sensei site)
     * public Node getBestChild(Node root) {
     * Node child = root.child;
     * Node best_child = null;
     * int  best_visits= -1;
     * while (child!=null) { // for all children
     * if (child.visits > best_visits) {
     * best_child=child;
     * best_visits=child.visits;
     * }
     * child = child.sibling;
     * }
     * return best_child;
     * }
     *
     * @return the bestNode to the child with the highest winrate   (or maybe numvisits - make option?)
     */
    public M findBestChildMove(MonteCarloSearchOptions.MaximizationStyle style) {
        UctNode<M> bestNode = null;
        if (hasChildren()) {
            for (UctNode<M> child : children) {
                switch (style) {
                    case WIN_RATE:
                        if (bestNode == null || child.getWinRate() > bestNode.getWinRate()) {
                            bestNode = child;
                        }
                        break;
                    case NUM_VISITS:
                        if (bestNode == null || child.getNumVisits() > bestNode.getNumVisits()) {
                            bestNode = child;
                        }
                        break;
                }
            }
        }
        return bestNode == null ? null : bestNode.move;
    }

    /**
     * This is the secret sauce at the core of the UCT algorithm.
     * See http://www-958.ibm.com/software/data/cognos/manyeyes/visualizations/uct-search-parameters
     * For analysis of effect of parameters on UCT value returned.
     * <p>
     * UCTK = 0.44; // 0.44 = sqrt(1/5)
     * if (parentVisits > 0) {
     * double winrate = this.getWinRate();
     * double uct = UCTK * Math.sqrt( Math.log(parentVisits) / this.visits );
     * uctvalue = winrate + uct;
     * }
     * else {
     * // Always play a random unexplored move first
     * uctvalue = 10000 + 1000*Math.random();
     * }
     *
     * @param exploreExploitRatio bigger values mean more exploration as opposed to exploitation of known good moves.
     * @param parentVisits        the number of times our parent node has been visited.
     * @return the uct value which is somewhat related to the winRate
     * and a ratio of our visits and out next siblings visits.
     */
    public double calculateUctValue(double exploreExploitRatio, int parentVisits) {
        if (numVisits > 0) {
            double uct = exploreExploitRatio * Math.sqrt(Math.log(parentVisits) / (DENOM_CONST * numVisits));
            return getWinRate() + uct;
        } else {
            // always play a random unexplored move first.
            // Something bigger than any win rate, yet higher if better move.
            return BIG + getWinRate();
        }
    }

    public NodeAttributes getAttributes() {
        NodeAttributes attributes = new NodeAttributes();
        attributes.put("visits", Integer.toString(numVisits));
        attributes.put("wins", Float.toString(numWins));
        attributes.put("winRate", FormatUtil.formatNumber(getWinRate()));
        return attributes;
    }

    public String toString() {
        return move.toString() + " " + getAttributes().toString();
    }

    /** print the tree rooted at this node *
     public void printTree() {
     System.out.println("ROOT -------------------");
     printTree("", this.numVisits);
     }     */

    /**
     * print the tree rooted at this node.
     * @param indent amount to indent
     *
    private void printTree(String indent, int parentVisits) {
    System.out.println(indent + this.toString()
    + " uct=" + FormatUtil.formatNumber(this.calculateUctValue(1.0, parentVisits)));
    if (hasChildren()) {
    for (UctNode child : this.getChildren()) {
    child.printTree(indent + "  ", this.numVisits);
    }
    }
    }  */
}