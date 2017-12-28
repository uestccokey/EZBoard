/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable;
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache;
import com.barrybecker4.game.twoplayer.go.board.analysis.BoardEvaluator;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.board.move.GoMoveGenerator;
import com.barrybecker4.game.twoplayer.go.board.move.UrgentMoveGenerator;
import com.barrybecker4.game.twoplayer.go.board.update.DeadStoneUpdater;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * For searching go game's search space.
 *
 * @author Barry Becker
 */
public class GoSearchable extends TwoPlayerSearchable<GoMove, GoBoard> {

    /** keeps track of dead stones. */
    private DeadStoneUpdater deadStoneUpdater_;

    private BoardEvaluator boardEvaluator_;

    /**
     * Constructor.
     */
    public GoSearchable(GoBoard board, PlayerList players, ScoreCache cache) {
        super(board, players);
        init(cache);
    }

    public GoSearchable(GoSearchable searchable) {
        super(searchable);
        init(searchable.getScoreCache());
    }

    @Override
    public GoSearchable copy() {
        return new GoSearchable(this);
    }

    @Override
    public GoBoard getBoard() {
        return board_;
    }

    public ScoreCache getScoreCache() {
        return boardEvaluator_.getCache();
    }

    /** don't really want to expose this, but renderer needs it */
    //public GroupAnalyzer getGroupAnalyzer(IGoGroup group) {
    //    return boardEvaluator_.getGroupAnalyzer(group);
    //}
    private void init(ScoreCache cache) {
        deadStoneUpdater_ = new DeadStoneUpdater(getBoard());
        boardEvaluator_ = new BoardEvaluator(getBoard(), cache);
    }

    @Override
    protected GoProfiler getProfiler() {
        return GoProfiler.getInstance();
    }

    /**
     * Given a move determine whether the game is over.
     * If recordWin is true then the variables for player1/2HasWon can get set.
     * Sometimes, like when we are looking ahead, we do not want to set these.
     * The game is over if we have a resignation move, or the last two moves were passing moves.
     *
     * @param move      the move to check
     * @param recordWin if true then the controller state will record wins
     * @return true if the game is over
     */
    @Override
    public final boolean done(GoMove move, boolean recordWin) {
        boolean gameOver = false;

        if (move == null) {
            gameOver = true;
        } else if (move.isResignationMove()) {
            if (recordWin) {
                setWinner(!move.isPlayer1());
            }
            gameOver = true;
        } else if (twoPasses(move)) {
            if (recordWin) {
                setWinner(getFinalScore(true) > getFinalScore(false));
            }
            gameOver = true;
        }
        if (!gameOver) {
            // try normal handling
            gameOver = super.done(move, recordWin);
        }

        if (gameOver && recordWin) {
            doFinalBookKeeping();
        }

        return gameOver;
    }

    /**
     * Update final territory and number of dead stones.
     * Include this in calcWorth because we call updateTerritory which is under calcWorth for timing.
     */
    private void doFinalBookKeeping() {
        getProfiler().startCalcWorth();
        boardEvaluator_.updateTerritoryAtEndOfGame();

        //we should not call this twice
        if (getNumDeadStonesOnBoard(true) > 0 || getNumDeadStonesOnBoard(false) > 0) {
            GameContext.log(0, " Error: should not update life and death twice.");
        }
        // now that we are finally at the end of the game,
        // update the life and death of all the stones still on the board
        GameContext.log(1, "about to update life and death.");
        deadStoneUpdater_.determineDeadStones();
        getProfiler().stopCalcWorth();
    }

    /**
     * Statically evaluate the board position.
     *
     * @return statically evaluated value for the board.
     * a positive value means that player1 has the advantage.
     * A big negative value means a good move for p2.
     */
    @Override
    public int worth(GoMove lastMove, ParameterArray weights) {
        return boardEvaluator_.worth(lastMove, weights, getHashKey());
    }

    /**
     * get the number of black (player1=true) or white (player1=false) stones that were captured and removed.
     *
     * @param player1sStones if true, get the captures for player1, else for player2.
     * @return num captures of the specified color
     */
    public int getNumCaptures(boolean player1sStones) {
        return getBoard().getNumCaptures(player1sStones);
    }

    /**
     * @param move the move to play.
     */
    @Override
    public void makeInternalMove(GoMove move) {
        super.makeInternalMove(move);
        updateHashIfCaptures(move);
    }

    /**
     * takes back the most recent move.
     *
     * @param move move to undo
     */
    @Override
    public void undoInternalMove(GoMove move) {
        super.undoInternalMove(move);
        updateHashIfCaptures(move);
    }

    /**
     * Whether we are removing captures from the board or adding them back, the operation is the same: XOR.
     */
    private void updateHashIfCaptures(GoMove goMove) {
        if (goMove.getNumCaptures() > 0) {
            for (BoardPosition pos : goMove.getCaptures()) {
                hash.applyMove(pos.getLocation(), getBoard().getStateIndex(pos));
            }
            // this is needed to disambiguate ko's and positions that have captures.
            if (goMove.isKo(getBoard()) || goMove.getNumCaptures() > 0) {
                hash.applyMoveNumber(getNumMoves() + goMove.getNumCaptures());
            }
        }
    }

    /**
     * @param move last move
     * @return true if last two moves were passing moves.
     */
    private boolean twoPasses(GoMove move) {
        if (move.isPassingMove() && moveList_.size() > 2) {
            GoMove secondToLast = moveList_.get(moveList_.size() - 2);
            if (secondToLast.isPassingMove()) {
                GameContext.log(0, "Done: The last 2 moves were passes :" + move + ", " + secondToLast);
                return true;
            }
        }
        return false;
    }

    /**
     * @param player1 if true, set player1 as the winner, else player2.
     */
    private void setWinner(boolean player1) {
        if (player1) {
            players_.getPlayer1().setWon(true);
        } else {
            players_.getPlayer2().setWon(true);
        }
    }

    /**
     * get a territory estimate for player1 or player2
     * When the game is over, this should return a precise value for the amount of territory
     * (not yet filled with captures).
     * So the estimate will be very rough at the beginning of the game, but should get better as more pieces are played.
     * <p>
     * Since this can be called while we are processing, we return cached values in
     * those cases to avoid a ConcurrentModificationException.
     *
     * @param forPlayer1 if true, get the captures for player1, else for player2
     * @return estimate of the amount of territory the player has
     */
    public int getTerritoryEstimate(boolean forPlayer1) {
        GoMove m = moveList_.getLastMove();
        if (m == null)
            return 0;

        return boardEvaluator_.getTerritoryEstimate(forPlayer1, false);
    }

    /**
     * Call this at the end of the game when we need to try to get an accurate score.
     *
     * @param forPlayer1 true if player one (black)
     * @return the actual score (each empty space counts as one)
     */
    public int getFinalTerritory(boolean forPlayer1) {
        return boardEvaluator_.getTerritoryEstimate(forPlayer1, true);
    }

    /**
     * Only valid after final bookkeeping has been done at the end of the game.
     *
     * @param forPlayer1 player to get dead stones for.
     * @return number of dead stones of specified players color.
     */
    public int getNumDeadStonesOnBoard(boolean forPlayer1) {
        return deadStoneUpdater_.getNumDeadStonesOnBoard(forPlayer1);
    }

    /**
     * @param player1 if true, then the score for player one is returned else player2's score is returned
     * @return the score (larger is better regardless of player)
     */
    public double getFinalScore(boolean player1) {
        int numDead = getNumDeadStonesOnBoard(player1);
        int totalCaptures = numDead + getNumCaptures(!player1);
        int p1Territory = getFinalTerritory(player1);

        String side = (player1 ? "black" : "white");
        GameContext.log(1, "----");
        GameContext.log(1, "final score for " + side);
        GameContext.log(2, "getNumCaptures(" + side + ")=" + getNumCaptures(player1));
        GameContext.log(2, "num dead " + side + " stones on board: " + numDead);
        GameContext.log(2, "getTerritory(" + side + ")=" + p1Territory);
        GameContext.log(0, "terr + totalEnemyCaptures=" + (p1Territory + totalCaptures));
        return p1Territory + totalCaptures;
    }

    /**
     * @return any moves that take captures or get out of atari.
     */
    @Override
    public final MoveList<GoMove> generateUrgentMoves(GoMove lastMove, ParameterArray weights) {
        UrgentMoveGenerator generator = new UrgentMoveGenerator(getBoard());
        return generator.generateUrgentMoves(generateMoves(lastMove, weights), lastMove);
    }

    /**
     * True if the specified move caused CRITICAL_GROUP_SIZE or more opponent pieces to become jeopardized
     * For go, if the specified move caused a sufficiently large group of stones to become in atari, then we return true.
     *
     * @return true if the last move created a big change in the score
     */
    @Override
    public boolean inJeopardy(GoMove move, ParameterArray weights) {
        return UrgentMoveGenerator.inJeopardy(move, getBoard());
    }

    /**
     * generate all good next moves (statically evaluated)
     */
    @Override
    public final MoveList<GoMove> generateMoves(GoMove lastMove, ParameterArray weights) {
        GoMoveGenerator generator = new GoMoveGenerator(this);
        return generator.generateEvaluatedMoves(lastMove, weights);
    }
}