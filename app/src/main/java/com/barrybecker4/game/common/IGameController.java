/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common;


import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.IBoard;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;

/**
 * This is the interface that all game controllers should implement.
 * Providing both an interface and an abstract implementation is a pattern
 * which maximizes flexibility in a framework. The interface defines the
 * public contract. The abstract class may be package private if we don't
 * want to expose it. Other classes may implement this interface without
 * extending the abstract base class.
 *
 * @author Barry Becker
 * @see GameController for the abstract implementation of this interface
 * @see Board
 */
public interface IGameController<M extends Move, B extends IBoard<M>> {

    /**
     * @return the board representation object.
     */
    B getBoard();

    /**
     * @return the class which shows the current state of the game board.
     * May be null if there is no user visible representation of the game.
     */
    GameViewModel getViewer();

    /**
     * retract the most recently played move
     *
     * @return the move which was undone (null returned if no prior move)
     */
    M undoLastMove();

    /**
     * this makes an arbitrary move (assumed valid) and
     * adds it to the move list.
     * For two player games, calling this does not keep track of weights or the search.
     * Its most common use is for browsing the game tree.
     *
     * @param m the move to play.
     */
    void makeMove(M m);

    /**
     * @return the list of moves made so far.
     */
    MoveList<M> getMoveList();

    /**
     * @return the number of moves currently played.
     */
    int getNumMoves();

    /**
     * @return an array of the players playing the game
     */
    PlayerList getPlayers();

    /**
     * @return the player who's turn it is now.
     */
    Player getCurrentPlayer();

    /**
     * a computer player makes the first move
     */
    void computerMovesFirst();

    /**
     * @return true if the game is over.
     */
    boolean isDone();
}
