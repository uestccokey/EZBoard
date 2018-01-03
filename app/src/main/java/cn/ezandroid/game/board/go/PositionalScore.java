/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go;

/**
 * For debugging purposes we want to keep more detail on what composes the overall score.
 *
 * @author Barry Becker
 */
public class PositionalScore {

    private double deadStoneScore = 0;
    private double eyeSpaceScore = 0;
    private double badShapeScore = 0;
    private double posScore = 0;
    private double healthScore = 0;

    /** Loosely based on badShapeScore, posScore, and healthScore */
    private double positionScore_ = 0;

    /** whether or not it has been incremented yet. */
    private boolean incremented_ = false;

    /** Create only using one of the two factory methods. */
    private PositionalScore() {}

    public static PositionalScore createEyePointScore(double deadStoneScore, double eyeSpaceScore) {
        PositionalScore score = new PositionalScore();
        score.deadStoneScore = deadStoneScore;
        score.eyeSpaceScore = eyeSpaceScore;
        score.calcPositionScore();
        return score;
    }

    public static PositionalScore createOccupiedScore(double badShapeScore, double posScore, double healthScore) {
        PositionalScore score = new PositionalScore();
        score.badShapeScore = badShapeScore;
        score.healthScore = healthScore;
        score.posScore = posScore;
        score.calcPositionScore();
        return score;
    }

    /** Create a score accumulator starting at 0 */
    public static PositionalScore createZeroScore() {
        return new PositionalScore();
    }

    public double getPositionScore() {
        return positionScore_;
    }

    // for unit test access
    public double getDeadStoneScore() { return deadStoneScore; }

    public double getEyeSpaceScore() { return eyeSpaceScore; }

    public double getBadShapeScore() { return badShapeScore; }

    public double getPosScore() { return posScore; }

    public double getHealthScore() { return healthScore; }

    public void incrementBy(PositionalScore score) {
        positionScore_ += score.getPositionScore();
        deadStoneScore += score.deadStoneScore;
        eyeSpaceScore += score.eyeSpaceScore;
        badShapeScore += score.badShapeScore;
        posScore += score.posScore;
        healthScore += score.healthScore;
        incremented_ = true;
    }

    /**
     * Don't call this after incrementing, but you must call once before incrementing.
     */
    private void calcPositionScore() {
        assert (!incremented_);
        positionScore_ = deadStoneScore + eyeSpaceScore + healthScore + posScore + badShapeScore;
    }
}
