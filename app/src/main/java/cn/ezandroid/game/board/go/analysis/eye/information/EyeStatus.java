/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import static cn.ezandroid.game.board.go.analysis.eye.information.EyeShapeScores.BIG_EYE;
import static cn.ezandroid.game.board.go.analysis.eye.information.EyeShapeScores.FALSE_EYE;
import static cn.ezandroid.game.board.go.analysis.eye.information.EyeShapeScores.GUARANTEED_TWO_EYES;
import static cn.ezandroid.game.board.go.analysis.eye.information.EyeShapeScores.SINGLE_EYE;

/**
 * 眼位状态枚举
 * <p>
 * http://www.lamsade.dauphine.fr/~cazenave/papers/eyeLabelling.pdf
 *
 * @author Barry Becker
 */
public enum EyeStatus {

    /**
     * 已经被点杀的眼
     */
    NAKADE("Nakade", "Ends up as one eye and this will not be sufficient to live.", SINGLE_EYE),

    /**
     * 不稳定的眼（有可能被点杀的眼）
     */
    UNSETTLED("Unsettled", "Ends up either nakade or alive depending on who moves first.", BIG_EYE),

    /**
     * 无条件活眼（该眼位所属的棋串已经是活棋或者该眼位是无法被点杀的眼）
     */
    ALIVE("Alive", "Unconditionally alive no matter who plays first.", GUARANTEED_TWO_EYES),

    /**
     * 有条件活棋（如果该眼位周围的棋串必须提掉眼位中的棋子才能活棋时）
     */
    ALIVE_IN_ATARI("Alive in atari", "There are only one or zero empty intersections adjacent to the surrounding" +
            " block, and capturing the opponent stones inside the eye grants an alive status.", FALSE_EYE),

    /**
     * 只有一个空点的假眼眼位
     */
    KO("Ko", "Will never be an eye no matter who plays first.", FALSE_EYE);

    private String mLabel;
    private String mDescription;
    private float mScore;

    EyeStatus(String label, String description, float score) {
        mLabel = label;
        mDescription = description;
        mScore = score;
    }

    @Override
    public String toString() {
        return mLabel;
    }

    public String getDescription() {
        return mDescription;
    }

    public float getScore() {
        return mScore;
    }
}
