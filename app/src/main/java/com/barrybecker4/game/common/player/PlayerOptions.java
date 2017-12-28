// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.player;

import java.io.Serializable;

/**
 * Represents a player in a game (either human or computer).
 *
 * @author Barry Becker
 */
public class PlayerOptions implements Serializable {

    private static final long serialVersionUID = 1;

    /** name of the player. */
    protected String name_;

    /** each player has an associated color. */
    private Color color_;

    /**
     * Constructor.
     *
     * @param name  name of the player
     * @param color some color identifying th eplayer in the ui.
     */
    public PlayerOptions(String name, Color color) {
        name_ = name;
        assert color != null;
        color_ = color;
    }

    public String getName() {
        return name_;
    }

    public void setName(String name) {
        this.name_ = name;
    }

    public Color getColor() {
        return color_;
    }

    public void setColor(Color color) {
        this.color_ = color;
    }

    @Override
    public String toString() {
        return "name = " + name_ + "* " + additionalInfo() + " ]";
    }

    protected String additionalInfo() {
        return "";
    }
}



