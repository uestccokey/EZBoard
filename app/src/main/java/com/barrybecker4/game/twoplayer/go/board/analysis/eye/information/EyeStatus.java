/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEye;

import static com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeShapeScores.BIG_EYE;
import static com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeShapeScores.FALSE_EYE;
import static com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeShapeScores.GUARANTEED_TWO_EYES;
import static com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeShapeScores.SINGLE_EYE;

/**
 * Enum for the different possible center Eye Status'.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 * @see GoEye
 */
public enum EyeStatus {
    /**
     * The eye will end up as only one eye and this will not be sufficient to
     * live. A nakade eye can be the result of: (1) an eye with an empty set of vital
     * points or (2) an eye with all the set of vital points filled by the opponent's stones.
     */
    NAKADE("Nakade", "Ends up as one eye and this will not be sufficient to live.", SINGLE_EYE),

    /**
     * The eye can end up as a nakade eye or an
     * alive eye depending on the next color to play. An unsettled
     * eye is the result of an eye with one and only one empty
     * intersection in the set of vital points.
     */
    UNSETTLED("Unsettled", "Ends up either nakade or alive depending on who moves first.", BIG_EYE),

    /**
     * The string owning the eye is alive no matter who plays first and no matter what the surrounding conditions are.
     * An alive eye can be the result of: (1) an eye with two or more empty intersections in the set of vital points
     * or (2) the eye is a shape that cannot be filled by the opponent with an n-1 nakade shape.
     * We will make no distinction between being alive or being alive in seki, because in many cases
     * being alive in seki is nearly as good.
     */
    ALIVE("Alive", "Unconditionally alive no matter who plays first.", GUARANTEED_TWO_EYES),

    /**
     * This is a particular case in which the surrounding conditions
     * determine the status of the eye. We say that an eye has an AliveInAtari status
     * if there are only one or zero empty intersections adjacent to the surrounding
     * block but capturing the opponent stones inside the eye grants an alive status.
     * Only when the external liberties of the string owning the eye are played it is
     * necessary to capture the stones inside the eye.
     */
    ALIVE_IN_ATARI("Alive in atari", "There are only one or zero empty intersections adjacent to the surrounding" +
            " block, and capturing the opponent stones inside the eye grants an alive status.", FALSE_EYE),

    /**
     * For example if may be a false eye with only one space in the eye - in other words a ko.
     */
    KO("Ko", "Will never be an eye no matter who plays first.", FALSE_EYE),

    /**
     * This can arise when there is a ko on the edge or corner.
     */
    UNSETTLED_KO("Unsettled ko", "Unsettled ko status can only happen on the edge or in the corner.", FALSE_EYE),

    /**
     * Other possibilities: Unknown, dead, false
     */
    UNCLASSIFIED("Unclassified", "The status has not been determined yet", SINGLE_EYE);

    private String label_;
    private String description_;
    private float score_;


    /**
     * constructor.
     *
     * @param label       simple label
     * @param description long description of the eye status.
     */
    EyeStatus(String label, String description, float score) {
        label_ = label;
        description_ = description;
        score_ = score;
    }

    @Override
    public String toString() {
        return label_;
    }

    public String getDescription() {
        return description_;
    }

    public float getScore() {
        return score_;
    }
}