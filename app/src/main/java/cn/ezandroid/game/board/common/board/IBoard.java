// Copyright by Barry G. Becker, 2013-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.common.board;

import cn.ezandroid.game.board.common.Move;

/**
 * This is the interface that all game boards should implement.
 * We assume that the board is composed of a 2D array of BoardPositions.
 * <p>
 * Providing both an interface and an abstract implementation is a pattern
 * which maximizes flexibility in a framework. The interface defines the
 * public contract. The abstract class may be package private if we don't
 * want to expose it. Or other classes may implement this interface without
 * extending the abstract base class.
 *
 * @author Barry Becker
 */
public interface IBoard<M extends Move> {

    /**
     * Reset the board to its initial starting state.
     */
    void reset();

    /**
     * We should be able to create a deep copy of ourselves
     *
     * @return deep copy of the board.
     */
    IBoard copy();

    /**
     * given a move specification, execute it on the board
     * This places the players symbol at the position specified by move.
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    boolean makeMove(M move);

    /**
     * Allow reverting a move so we can step backwards in time.
     * Board is returned to the exact state it was in before the last move was made.
     *
     * @return the move that was just undone.
     */
    M undoMove();
}
