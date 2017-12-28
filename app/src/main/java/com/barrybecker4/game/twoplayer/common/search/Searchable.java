/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Implementations of this interface allow searching for successive moves in a two player game.
 * The SearchStrategy classes call methods of this interface to do their search.
 *
 * @author Barry Becker
 * @see TwoPlayerController for the default implementation of this interface
 */
public interface Searchable<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>> {

    /**
     * @return the search options having to do with search parameters.
     */
    SearchOptions getSearchOptions();

    /**
     * @param move the move to play.
     */
    void makeInternalMove(M move);

    /**
     * Takes back the most recent move.
     *
     * @param move move to undo.
     */
    void undoInternalMove(M move);

    /**
     * @return the current strategy used for searching.
     */
    SearchStrategy getSearchStrategy();

    /**
     * **** SEARCH ******
     *
     * @return the best move to use as the next move.
     */
    M searchForNextMove(ParameterArray weights, M lastMove, IGameTreeViewable treeViewer);

    /**
     * Given a move, determine whether the game is over.
     * If recordWin is true then the variables for player1/2HasWon can get set.
     * sometimes, like when we are looking ahead in search we do not want to set these.
     *
     * @param move      the move to check. If null then return true. This is typically the last move played
     * @param recordWin if true then the controller state will record wins
     * @return true if the game is over.
     */
    boolean done(M move, boolean recordWin);

    /**
     * Statically evaluate a boards state to compute the value of the last move
     * from player1's perspective.
     * This function is a key function that must be created for each type of game added.
     * If evaluating from player 1's perspective, then good moves for p1 are given a positive score.
     *
     * @param lastMove the last move made
     * @param weights  the polynomial weights to use in the polynomial evaluation function
     * @return the worth of the board from the specified players point of view
     */
    int worth(M lastMove, ParameterArray weights);

    /**
     * Generate a list of good evaluated next moves given the last move.
     * This function is a key function that must be created for each type of game added.
     *
     * @param lastMove the last move made if there was one. (null if first move of the game)
     * @param weights  the polynomial weights to use in the polynomial evaluation function.
     * @return list of possible next moves.
     */
    MoveList<M> generateMoves(M lastMove, ParameterArray weights);

    /**
     * generate those moves that are critically urgent
     * If you generate too many, then you run the risk of an explosion in the search tree.
     * These moves should be sorted from most to least urgent
     *
     * @param lastMove the last move made
     * @param weights  the polynomial weights to use in the polynomial evaluation function
     * @return a list of moves that the current player needs to urgently play or face imminent defeat.
     */
    MoveList<M> generateUrgentMoves(M lastMove, ParameterArray weights);

    /**
     * returns true if the specified move caused one or more opponent pieces to become jeopardized
     *
     * @return true if the move m is in jeopardy.
     */
    boolean inJeopardy(M move, ParameterArray weights);

    /** The current board state. */
    B getBoard();

    /**
     * List of moves made so far.
     *
     * @return list of moves made on the board.
     */
    MoveList<M> getMoveList();

    /**
     * @return num moves played so far
     */
    int getNumMoves();

    /**
     * @return a copy of our current state so we can make moves and not worry about undoing them.
     */
    Searchable<M, B> copy();

    /**
     * @return the Zobrist hash for the currently searched position
     */
    HashKey getHashKey();
}
