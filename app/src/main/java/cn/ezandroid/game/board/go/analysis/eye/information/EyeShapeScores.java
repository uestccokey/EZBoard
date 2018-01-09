/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

/**
 * 眼型的分数
 * <p>
 * 0表示死亡，1表示无条件活
 *
 * @author Barry Becker
 */
final class EyeShapeScores {

    // 有假眼点眼位的分数
    public static final float FALSE_EYE = 0.19f;

    // 单眼的分数
    public static final float SINGLE_EYE = 1.0f;

    // 假如敌方先行，可能变成单眼的分数
    public static final float BIG_EYE = 1.2f;

    // 保证有两眼的分数
    public static final float GUARANTEED_TWO_EYES = 2.0f;

    private EyeShapeScores() {}
}