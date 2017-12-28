/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common;

/**
 * The GameController communicates with the viewer via this interface.
 * Alternatively we could use RMI or events, but for now the minimal interface is
 * defined here and called directly by the controller.
 *
 * @author Barry Becker
 */
public interface GameViewModel {

    /**
     * return the game to its original state.
     */
    void reset();

    /**
     * @return our game controller
     */
    GameController getController();
}
