/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go;

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Color;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoProfiler;
import com.barrybecker4.game.twoplayer.go.board.GoSearchable;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.options.GoOptions;
import com.barrybecker4.game.twoplayer.go.options.GoPlayerOptions;
import com.barrybecker4.game.twoplayer.go.options.GoWeights;
import com.barrybecker4.game.twoplayer.go.persistence.GoGameExporter;
import com.barrybecker4.game.twoplayer.go.persistence.GoGameImporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Go.
 *
 * @author Barry Becker
 */
public final class GoController extends TwoPlayerController<GoMove, GoBoard> {

    public static final String VERSION = "0.99";

    /** if true use an additional heuristic to get more accurate scoring of group health in a second pass. */
    public static final boolean USE_RELATIVE_GROUP_SCORING = true;

    /** default num row and columns for a default square go board. */
    private static final int DEFAULT_SIZE = 9;

    /** if difference greater than this, then consider a win. */
    public static final int WIN_THRESHOLD = 2000;

    private ScoreCache scoreCache_;

    private BoardOptions boardOpts;

    /**
     * Construct the Go game controller.
     */
    public GoController() {
        boardOpts = new BoardOptions(DEFAULT_SIZE, 0);
    }

    /**
     * Construct the Go game controller given dimensions and number of handicap stones.
     *
     * @param numHandicapStones 0 - 9 handicap stones to show initially.
     */
    public GoController(int size, int numHandicapStones) {
        boardOpts = new BoardOptions(size, numHandicapStones);
        initializeData();
    }

    @Override
    protected GoBoard createBoard() {
        return new GoBoard(boardOpts.size, boardOpts.numHandicaps);
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new GoOptions();
    }

    /**
     * create the 2 players.
     */
    @Override
    protected PlayerList createPlayers() {
        PlayerList players = new PlayerList();
//        players.add(new Player(createPlayerOptions(GameContext.getLabel("BLACK"),
//                GoStoneRenderer.DEFAULT_PLAYER1_COLOR), true));
//        players.add(new Player(createPlayerOptions(GameContext.getLabel("WHITE"),
//                GoStoneRenderer.DEFAULT_PLAYER1_COLOR), false));
        players.add(new Player(createPlayerOptions("Black",
                Color.BLACK), true));
        players.add(new Player(createPlayerOptions("WHITE",
                Color.WHITE), false));
        return players;
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new GoPlayerOptions(playerName, color);
    }

    /**
     * this gets the Go specific patterns and weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new GoWeights();
    }

    @Override
    protected GoProfiler getProfiler() {
        return GoProfiler.getInstance();
    }

    /**
     * specify the number of handicap stones.
     *
     * @param handicap number of handicap stones to place on the board at star points.
     */
    public void setHandicap(int handicap) {
        getBoard().setHandicap(handicap);
        player1sTurn = false;
    }

    /**
     * @return true if the computer is to make the first move.
     */
    @Override
    public boolean doesComputerMoveFirst() {
        int handicap = getBoard().getHandicap();
        Player player1 = getPlayers().getPlayer1();
        return ((!player1.isHuman() && (handicap == 0)) ||
                (player1.isHuman() && (handicap > 0)));
    }

    /**
     * Measure is determined by the score (amount of territory + captures)
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     *
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin() {
        return (int) Math.abs(getFinalScore(true) - getFinalScore(false));
    }

    /**
     * @param player1 if true, then the score for player one is returned else player2's score is returned
     * @return the score (larger is better regardless of player)
     */
    public double getFinalScore(boolean player1) {
        if (isProcessing()) {
            GameContext.log(0, "Error: tried to get Score() while processing!");
            return 0;
        }
        GameContext.log(0, "cache results " + scoreCache_);
        return ((GoSearchable) getSearchable()).getFinalScore(player1);
    }

    /**
     * Call this at the end of the game when we need to try to get an accurate score.
     *
     * @param forPlayer1 true if for player one (black)
     * @return the actual amount of territory for the specified player (each empty space counts as one)
     */
    public int getFinalTerritory(boolean forPlayer1) {
        return ((GoSearchable) getSearchable()).getFinalTerritory(forPlayer1);
    }

    /**
     * Restore the game board back to its initial opening state.
     */
    @Override
    public void reset() {
        super.reset();
        if (getBoard().getHandicap() > 0) {
            player1sTurn = false;
        }
        scoreCache_ = new ScoreCache();
    }

    @Override
    public void computerMovesFirst() {
        List moveList = getSearchable().generateMoves(null, weights_.getPlayer1Weights());
        // select the best (first move, since they are sorted) move to use
        GoMove m = (GoMove) moveList.get(0);

        makeMove(m);
    }

    /**
     * save the current state of the go game to a file in SGF (4) format (standard game format).
     * This should some day be xml (xgf)
     */
    @Override
    public TwoPlayerGameExporter getExporter() {
        return new GoGameExporter(this);
    }

    @Override
    public void restoreFromStream(InputStream iStream) throws IOException, SGFException {
        GoGameImporter importer = new GoGameImporter(this);
        importer.restoreFromStream(iStream);
    }

    @Override
    protected GoSearchable createSearchable(GoBoard board, PlayerList players) {
        return new GoSearchable(board, players, scoreCache_);
    }

    private class BoardOptions {
        int size;
        int numHandicaps;

        BoardOptions(int size, int numHandicaps) {
            this.size = size;
            this.numHandicaps = numHandicaps;
        }
    }
}
