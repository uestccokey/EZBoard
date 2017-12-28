/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.Color;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameImporter;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.optimization.optimizee.Optimizee;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//import com.barrybecker4.game.twoplayer.common.ui.OptimizationDoneHandler;
//import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

//import javax.swing.JOptionPane;

/**
 * This is an abstract base class for a two player Game Controller.
 * It contains the key logic for 2 player zero sum games with perfect information.
 * Some examples include chess, checkers, go, othello, gomoku, blockade, Mancala, nine-mens morris, etc.
 * It implements Optimizee because the games derived from this class
 * can be optimized to improve their playing ability.
 * <p>
 * Instance of this class process requests from the GameViewer.
 * Specifically, the GameViewer tells the TwoPlayerController what move the human made,
 * and the TwoPlayerController returns information such as the computer's move.
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerController<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends GameController<M, B> {

    protected boolean player1sTurn = true;

    /**
     * These weights determine how the computer values each move as parameters to a game dependent evaluation function.
     */
    protected GameWeights weights_;

    /** if this becomes non-null, we will fill in the game tree for display in a UI. */
    private IGameTreeViewable gameTreeViewer_;

    /** Worker represents a separate thread for finding the next move. */
    private TwoPlayerSearchWorker<M, B> worker_;

    /** Capable of searching for the best next move */
    private Searchable<M, B> searchable_;


    /**
     * Construct the game controller.
     */
    public TwoPlayerController() {
        setPlayers(createPlayers());
        worker_ = new TwoPlayerSearchWorker<>(this);
    }

    @Override
    public TwoPlayerOptions getOptions() {
        if (gameOptions_ == null) {
            gameOptions_ = createOptions();
        }
        return (TwoPlayerOptions) gameOptions_;
    }

    /**
     * @return custom set of search and game options.
     */
    protected abstract TwoPlayerOptions createOptions();


    public TwoPlayerViewModel<M, B> getViewer() {
        return (TwoPlayerViewModel) viewer_;
    }

    /**
     * Return the game board back to its initial opening state
     */
    @Override
    public void reset() {
        worker_.interrupt();
        super.reset();
        searchable_ = null;
        getPlayers().reset();
        player1sTurn = true;
    }

    /**
     * save the current state of the game to a file in SGF (4) format (standard game format).
     * This should some day be xml (xgf)
     */
    @Override
    public TwoPlayerGameExporter<M, B> getExporter() {
        return new TwoPlayerGameExporter<>(this);
    }

    @Override
    public MoveList<M> getMoveList() {
        return getBoard().getMoveList();
    }

    @Override
    public void restoreFromFile(String fileName) {

        try {
            FileInputStream iStream = new FileInputStream(fileName);
            GameContext.log(2, "opening " + fileName);

            restoreFromStream(iStream);

        } catch (FileNotFoundException fnfe) {
//            JOptionPane.showMessageDialog(null,
//                    "file " + fileName + " was not found." + fnfe.getMessage());
            fnfe.printStackTrace();
        } catch (IOException ioe) {
//            JOptionPane.showMessageDialog(null,
//                    "IOException occurred while reading " +
//                            fileName + " :" + ioe.getMessage());
            ioe.printStackTrace();
        } catch (SGFException sgfe) {
//            JOptionPane.showMessageDialog(null,
//                    "file " + fileName + " had an SGF error while loading: " +
//                            sgfe.getMessage());
            sgfe.printStackTrace();
        }
    }

    public void restoreFromStream(InputStream iStream) throws IOException, SGFException {
        TwoPlayerGameImporter<M, B> importer = new TwoPlayerGameImporter<>(this);
        importer.restoreFromStream(iStream);
        M m = getLastMove();
        if (m != null) {
            int value = getSearchable().worth(m, weights_.getDefaultWeights());
            m.setValue(value);
        }
    }

    /**
     * @return true if the computer is supposed to make the first move.
     */
    public boolean doesComputerMoveFirst() {
        return !getPlayers().getPlayer1().isHuman();
    }

    /**
     * @return the 2 players.
     */
    protected PlayerList createPlayers() {
        PlayerList players = new PlayerList();

//        PlayerOptions p1Opts =
//                createPlayerOptions(GameContext.getLabel("PLAYER1"), TwoPlayerPieceRenderer.DEFAULT_PLAYER1_COLOR);
//        PlayerOptions p2Opts =
//                createPlayerOptions(GameContext.getLabel("PLAYER2"), TwoPlayerPieceRenderer.DEFAULT_PLAYER2_COLOR);

        PlayerOptions p1Opts =
                createPlayerOptions("PLAYER1", Color.BLACK);
        PlayerOptions p2Opts =
                createPlayerOptions("PLAYER2", Color.WHITE);

        players.add(new Player(p1Opts, true));
        players.add(new Player(p2Opts, false));
        return players;
    }

    /** color is ignored right now. Comes from the piece renderer */
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new TwoPlayerPlayerOptions(playerName, color);
    }

    /**
     * @return the search strategy to use to find the next move.
     */
    public final SearchStrategy getSearchStrategy() {
        return getSearchable().getSearchStrategy();
    }

    /**
     * @return true if it is currently player1's turn.
     */
    public final boolean isPlayer1sTurn() {
        return player1sTurn;
    }

    /**
     * @return true if player2 is a computer player
     */
    @Override
    public final Player getCurrentPlayer() {
        return player1sTurn ? getPlayers().getPlayer1() : getPlayers().getPlayer2();
    }

    /**
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     *
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin() {
        if (!getPlayers().anyPlayerWon()) {
            return 0;
        }
        return getBoard().getTypicalNumMoves() / getNumMoves();
    }

    /**
     * this returns a reference to the weights class for editing
     *
     * @return contains the weights used for computer player1 and 2.
     */
    public final GameWeights getComputerWeights() {
        return weights_;
    }

    /**
     * retract the most recently played move
     *
     * @return the move which was undone (null returned if no prior move)
     */
    @Override
    public M undoLastMove() {
        M lastMove = getLastMove();
        getSearchable().undoInternalMove(lastMove);
        if (lastMove != null) {
            player1sTurn = lastMove.isPlayer1();
        }
        return lastMove;
    }

    /**
     * Currently online play not available for 2 player games - coming soon!
     *
     * @return false
     */
    @Override
    public boolean isOnlinePlayAvailable() {
        return false;
    }

    /**
     * The computer will search for and make its next move.
     * The search for the best computer move happens on a separate thread so the UI does not lock up.
     *
     * @param player1 if true then the computer moving is player1
     * @return the move the computer selected (may return null if no move possible)
     */
    M findComputerMove(boolean player1) {
        ParameterArray weights;
        player1sTurn = player1;

        // we cannot find a computer move if no move played yet.
        if (getMoveList().isEmpty()) return null;

        M move = getMoveList().getLastMove();
        M lastMove = (M) move.copy();

        weights = player1 ? weights_.getPlayer1Weights() : weights_.getPlayer2Weights();

        if (gameTreeViewer_ != null) {
            gameTreeViewer_.resetTree(lastMove);
        }
        M selectedMove = getSearchable().searchForNextMove(weights, lastMove, gameTreeViewer_);

        if (selectedMove != null) {
            makeMove(selectedMove);
            GameContext.log(2, "computer move :" + selectedMove.toString());
        }

        return selectedMove;
    }

    /**
     * record the humans move p.
     *
     * @param move the move the player made
     * @return the same move with some of the fields filled in
     */
    public final Move manMoves(M move) {
        makeMove(move);
        // we pass the default weights because we just need to know if the game is over
        move.setValue(getSearchable().worth(move, weights_.getDefaultWeights()));
        return move;
    }

    /**
     * this makes the specified move (assumed valid) and adds it to the move list.
     * Calling this does not keep track of weights or the search.
     * Its most common use is for browsing the game tree.
     *
     * @param move the move to play.
     */
    @Override
    public void makeMove(M move) {
        getSearchable().makeInternalMove(move);
        player1sTurn = !(move).isPlayer1();
    }

    /**
     * When this method is called the game controller will asynchronously search for the next move
     * for the computer to make. It returns immediately (unless the computer is playing itself).
     * Usually returns false, but will return true if it is a computer vs computer game, and the
     * game is over.
     *
     * @param player1ToMove true if is player one to move.
     * @return true if the game is over.
     * @throws AssertionError thrown if something bad happened while searching.
     */
    public boolean requestComputerMove(boolean player1ToMove) throws AssertionError {
        return requestComputerMove(player1ToMove, getPlayers().allPlayersComputer());
    }

    /**
     * Request the next computer move. It will be the best move that the computer can find.
     * Launches a separate thread to do the search for the next move so the UI is not blocked.
     *
     * @param player1ToMove true if player one to move.
     * @param synchronous   if true then the method does not return until the next move has been found.
     * @return true if the game is over
     * @throws AssertionError if something bad happened while searching.
     */
    public boolean requestComputerMove(final boolean player1ToMove, boolean synchronous) throws AssertionError {
        return worker_.requestComputerMove(player1ToMove, synchronous);
    }

//    /**
//     * Let the computer play against itself for a long time as it optimizes its parameters.
//     * @param handler will be called when the optimization is done processing.
//     */
//    public void runOptimization(final OptimizationDoneHandler handler) {
//
//        final Optimizer optimizer =
//                new Optimizer(this.getOptimizee(), getOptions().getAutoOptimizeFile());
//
//        ParameterArray weights = getComputerWeights().getDefaultWeights();
//
//        new TwoPlayerOptimizationWorker(optimizer, weights, handler).execute();
//    }

    /**
     * @return true if the viewer is currently processing (i.e. searching)
     */
    public boolean isProcessing() {
        return worker_.isProcessing();
    }

    public void pause() {
        if (getSearchStrategy() == null) {
            GameContext.log(1, "There is no search to pause");
            return;
        }
        getSearchStrategy().pause();
        GameContext.log(1, "search strategy paused.");
    }

    public boolean isPaused() {
        return getSearchStrategy().isPaused();
    }

    /**
     * if desired we can set a game tree viewer. If non-null then this
     * will be updated as the search is conducted. The GameTreeDialog
     * is an example of something that implements this interface and can
     * be used to view the game tree as the search is progressing.
     * <p>
     * Here's how the GameTreeDialog is able to show the game tree:
     * When the user indicates that they want to see the GameTreeDialog,
     * the game panel gives the GameTreeDialog to the Controller:
     * controller_.setGameTreeViewer( treeDialog_ );
     * Then whenever a move by either party occurs, the GameTreeDialog receives
     * a game tree event. The GameTreeDialog renders the tree that was build up during search.
     * It already has a reference to the root of the tree.
     * If this method is never called, the controller knows
     * that it should not bother to create the tree when searching.
     */
    public final void setGameTreeViewable(IGameTreeViewable gameTreeViewable) {
        gameTreeViewer_ = gameTreeViewable;
    }

    @Override
    public boolean isDone() {
        M lastMove = getLastMove();
        return getSearchable().done(lastMove, false);
    }

    final Optimizee getOptimizee() {
        return new TwoPlayerOptimizee(this);
    }

    public synchronized Searchable<M, B> getSearchable() {
        if (searchable_ == null) {
            searchable_ = createSearchable(getBoard(), getPlayers());
        }

        return searchable_;
    }

    protected abstract Searchable<M, B> createSearchable(
            B board, PlayerList players);
}