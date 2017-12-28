/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.player;

import java.util.ArrayList;

/**
 * A list of players.
 *
 * @author Barry Becker
 */
public class PlayerList extends ArrayList<Player> {

    /**
     * Construct set of players
     */
    public PlayerList() {}

    /**
     * @return the player that goes first.
     */
    public Player getFirstPlayer() {
        return get(0);
    }

    /**
     * @return the player that goes first.
     */
    public Player getPlayer1() {
        return get(0);
    }

    /**
     * @return the player that goes second.
     */
    public Player getPlayer2() {
        return get(1);
    }

    /**
     * @return number of active players.
     */
    public int getNumPlayers() {
        return size();
    }

    /**
     * Reset won state so no one is marked as having won.
     */
    public void reset() {
        for (Player p : this) {
            p.setWon(false);
        }
    }

    /**
     * @return true if any of the players have won.
     */
    public boolean anyPlayerWon() {
        for (Player player : this) {
            if (player.hasWon()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the winning player or null if there isn't any yet.
     */
    public Player getWinningPlayer() {
        for (Player player : this) {
            if (player.hasWon()) {
                return player;
            }
        }
        return null;
    }

    /**
     * @return true if there are only human players
     */
    public boolean allPlayersHuman() {
        for (Player player : this) {
            if (!player.isHuman()) return false;
        }
        return true;
    }

    /**
     * @return true if there are only computer players
     */
    public boolean allPlayersComputer() {
        for (Player player : this) {
            if (player.isHuman()) return false;
        }
        return true;
    }
}