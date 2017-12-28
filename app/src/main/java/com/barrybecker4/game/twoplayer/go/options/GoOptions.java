/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.options;

import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;

/**
 * Go specific game options.
 *
 * @author Barry Becker
 */
public class GoOptions extends TwoPlayerOptions {

    /** The komi can vary, but 5.5 seems most commonly used. */
    private static final float DEFAULT_KOMI = 5.5f;

    /**
     * Additional score given to black or white to bring things into balance.
     * sort of like giving a partial handicap stone.
     */
    private float komi_ = DEFAULT_KOMI;


    /** Default constructor */
    public GoOptions() {}

    /** Constructor */
    public GoOptions(String preferredTone, float komi) {
        super(preferredTone);
        setKomi(komi);
    }

    public float getKomi() {
        return komi_;
    }

    public void setKomi(float komi) {
        this.komi_ = komi;
    }
}
