/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.player;

import java.io.Serializable;

/**
 * This constitutes a move by a player in a game.
 * It is what will be serialized and sent between client and server to
 * communicate the player's action (whether robot or human).
 *
 * @author Barry Becker
 */
public abstract class PlayerAction implements Serializable {

    private static final long serialVersionUID = 1;

    private String playerName_;

    /**
     * @param playerName the name of the player making the action.
     */
    protected PlayerAction(String playerName) {
        playerName_ = playerName;
    }

    public String getPlayerName() {
        return playerName_;
    }
}
