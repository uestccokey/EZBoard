/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.PositionalScore;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.options.GoWeights;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Used to keep track of evaluating a measure of score based only on values at positions.
 *
 * @author Barry Becker
 */
public final class PositionalScoreAnalyzer {

    /**
     * I used to pass this in as a parameter and have it vary with how far along into the game we are,
     * but I am trying to eliminate scores that might vary for the same position.
     * Identical positions could have different number of moves because of captures and kos.
     */
    private static final double GAME_STAGE_BOOST = 0.5;

    /** a lookup table of scores to attribute to the board positions when calculating the worth */
    private final PositionalScoreArray positionalScores_;

    private GoBoard board;

    /**
     * Construct the Go game controller.
     */
    public PositionalScoreAnalyzer(GoBoard board) {
        this.board = board;
        positionalScores_ = PositionalScoreArray.getArray(board.getNumRows());
    }

    /**
     * @param weights game weights
     * @return accumulated totalScore so far.
     */
    public PositionalScore determineScoreForPosition(int row, int col, ParameterArray weights) {
        GoBoardPosition position = (GoBoardPosition) board.getPosition(row, col);
        double positionalScore = positionalScores_.getValue(row, col);
        PositionalScore score =
                calcPositionalScore(position, weights, positionalScore);

        double s = score.getPositionScore();
        position.setScoreContribution(s);
        return score;
    }

    /**
     * @return the score contribution from a single point on the board
     */
    private PositionalScore calcPositionalScore(GoBoardPosition position, ParameterArray weights,
                                                double positionalScore) {
        PositionalScore score;

        if (position.isInEye()) {
            score = createEyePointScore(position);
        } else if (position.isOccupied()) {
            score = createNormalizedOccupiedScore(position, weights, positionalScore);
        } else {
            score = PositionalScore.createZeroScore();
        }
        return score;
    }

    /**
     * Score gets updated with value.
     * A dead enemy stone in the eye counts twice.
     */
    private PositionalScore createEyePointScore(GoBoardPosition position) {
        float scoreForPosition = position.getEye().isOwnedByPlayer1() ? 1.0f : -1.0f;
        float deadStoneScore = 0;
        float eyeSpaceScore = 0;
        if (position.isOccupied()) {
            deadStoneScore = scoreForPosition;
        } else {
            eyeSpaceScore = scoreForPosition;
        }

        return PositionalScore.createEyePointScore(deadStoneScore, eyeSpaceScore);
    }

    /**
     * Normalize each of the scores by the game weights.
     */
    private PositionalScore createNormalizedOccupiedScore(
            GoBoardPosition position, ParameterArray weights, double positionalScore) {
        GoStone stone = (GoStone) position.getPiece();

        int side = position.getPiece().isOwnedByPlayer1() ? 1 : -1;
        double badShapeWt = weights.get(GoWeights.BAD_SHAPE_WEIGHT_INDEX).getValue();
        double positionalWt = weights.get(GoWeights.POSITIONAL_WEIGHT_INDEX).getValue();
        double healthWt = weights.get(GoWeights.HEALTH_WEIGHT_INDEX).getValue();
        double totalWeight = badShapeWt + positionalWt + healthWt;
        StringShapeAnalyzer shapeAnalyzer = new StringShapeAnalyzer(board);

        // penalize bad shape like empty triangles
        double badShapeScore =
                -(side * shapeAnalyzer.formsBadShape(position) * badShapeWt) / totalWeight;

        // Usually a very low weight is assigned to where stone is played unless we are at the start of the game.
        double posScore = side * positionalWt * GAME_STAGE_BOOST * positionalScore / totalWeight;
        double healthScore = healthWt * stone.getHealth() / totalWeight;

        PositionalScore score = PositionalScore.createOccupiedScore(badShapeScore, posScore, healthScore);
        if (GameContext.getDebugMode() > 1) {
            stone.setPositionalScore(score);
        }
        return score;
    }
}