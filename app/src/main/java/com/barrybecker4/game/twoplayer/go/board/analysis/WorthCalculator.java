/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.go.GoController;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoProfiler;
import com.barrybecker4.game.twoplayer.go.board.PositionalScore;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.TerritoryAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.TerritoryUpdater;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.options.GoWeights;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * For statically evaluating the current state of a GoBoard.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public class WorthCalculator {

    private GoBoard board_;
    private TerritoryAnalyzer territoryAnalyzer;
    private TerritoryUpdater territoryUpdater;
    private WorthInfo info;

    /**
     * Constructor.
     */
    public WorthCalculator(GoBoard board, GroupAnalyzerMap analyzerMap) {
        board_ = board;
        territoryAnalyzer = new TerritoryAnalyzer(board, analyzerMap);
        territoryUpdater = new TerritoryUpdater(board, analyzerMap);
    }

    /**
     * Statically evaluate the board position.
     *
     * @return statically evaluated value of the board.
     * a positive value means that player1 has the advantage.
     * A big negative value means a good move for p2.
     */
    public int worth(Move lastMove, ParameterArray weights) {
        GoProfiler.getInstance().startCalcWorth();
        double worth = calculateWorth(board_, lastMove, weights);

        GameContext.log(3, "GoController.worth: worth=" + worth);
        if (worth < -GoController.WIN_THRESHOLD) {
            // then the margin is too great the losing player should resign
            return -SearchStrategy.WINNING_VALUE;
        }
        if (worth > GoController.WIN_THRESHOLD) {
            // then the margin is too great the losing player should resign
            return SearchStrategy.WINNING_VALUE;
        }
        GoProfiler.getInstance().stopCalcWorth();
        return (int) worth;
    }

    /**
     * Get estimate of territory for specified player.
     *
     * @param forPlayer1  the player to get the estimate for
     * @param isEndOfGame then we need the estimate to be more accurate.
     * @return estimate of size of territory for specified player.
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        return territoryAnalyzer.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * Statically evaluate the board position.
     * The most naive thing we could do here is to simply return the sum of the captures
     * for player1 - sum of the captures for player2. However for go, since search is not likely to
     * be that useful given the huge branch factor, we need to rely heavily on sophisticated evaluation.
     * We have every space on the board have a score representing
     * how strongly it is controlled by player1 (black).  If the score is 1.00, then that
     * position is inside or part of an unconditionally alive group owned by player1 (black)
     * or it is inside a dead white group.
     * If the score is -1.00 then its player 2's(white) unconditionally alive group
     * or black's dead group. A blank dame might have a score
     * of 0. A white stone might have a positive score if its part of a white group
     * which is considered mostly dead.
     *
     * @return statically evaluated value of the board.
     */
    private double calculateWorth(GoBoard board, Move lastMove, ParameterArray weights) {
        // adjust for board size - so worth will be comparable regardless of board size.
        double scaleFactor = 361.0 / Math.pow(board.getNumRows(), 2);

        // Update status of groups and stones on the board. Expensive. // Should not Change board state, but it does.
        territoryUpdater.updateTerritory(false);

        PositionalScore[][] positionScores = new PositionalScore[board.getNumRows()][board.getNumCols()];
        PositionalScore totalScore = PositionalScore.createZeroScore();

        PositionalScoreAnalyzer positionalScorer = new PositionalScoreAnalyzer(board);

        for (int row = 1; row <= board.getNumRows(); row++) {
            for (int col = 1; col <= board.getNumCols(); col++) {

                PositionalScore s =
                        positionalScorer.determineScoreForPosition(row, col, weights);
                positionScores[row - 1][col - 1] = s;
                totalScore.incrementBy(s);
            }
        }

        double territoryDelta = territoryUpdater.getTerritoryDelta();
        double captureScore = getCaptureScore(board, weights);
        double positionalScore = totalScore.getPositionScore();
        double worth = scaleFactor * (positionalScore + captureScore + territoryDelta);

        if (GameContext.getDebugMode() > 0) {
            String desc = totalScore.getDescription(worth, captureScore, territoryDelta, scaleFactor);
            ((TwoPlayerMove) lastMove).setScoreDescription(desc);
        }
        int blackCap = board.getNumCaptures(true);
        int whiteCap = board.getNumCaptures(false);
        this.info =
                new WorthInfo(territoryDelta, captureScore, blackCap, whiteCap,
                        positionalScore, positionScores, ((GoMove) lastMove).getCaptures(),
                        worth, board.getMoveList().copy());
        return worth;
    }

    public WorthInfo getWorthInfo() {
        return info;
    }

    /**
     * @return the estimated difference in territory between the 2 sides.
     */
    public float updateTerritoryAtEndOfGame() {
        return territoryUpdater.updateTerritory(true);
    }

    /**
     * @return score attributed to captured stones.
     */
    private double getCaptureScore(GoBoard board, ParameterArray weights) {
        double captureWt = weights.get(GoWeights.CAPTURE_WEIGHT_INDEX).getValue();
        return captureWt * (board.getNumCaptures(false) - board.getNumCaptures(true));
    }
}