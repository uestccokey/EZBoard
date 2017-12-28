/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;

/**
 * Determines the approximate likelihood of a player winning the game.
 *
 * @author Barry Becker
 */
public class WinProbabilityCalculator {

    /**
     * Constructor.
     */
    public WinProbabilityCalculator() {}

    /**
     * Returns a number between 0 and 1 representing the estimated probability of player 1 winning the game.
     * The chance of player2 winning = 1 - chance of p1 winning.
     *
     * @param moveList list of moves made so far.
     * @return estimated chance of player one winning the game
     */
    public final double getChanceOfPlayer1Winning(MoveList moveList) {
        // if true then too early in the game to tell.
        TwoPlayerMove lastMove = (TwoPlayerMove) moveList.getLastMove();

        // at the beginning of the game its anybody's guess : 50-50
        if (moveList.getNumMoves() < 4) {
            return 0.5f;
        }

        assert (lastMove != null) : "last move was null";

        // we can use this formula to estimate the outcome:
        float inherVal = lastMove.getInheritedValue();
        if (Math.abs(inherVal) > SearchStrategy.WINNING_VALUE)
            GameContext.log(1, "TwoPlayerController: warning: the score for p1 is greater than WINNING_VALUE(" +
                    SearchStrategy.WINNING_VALUE + ")  inheritedVal=" + inherVal);

        return computeChanceOfWinning(inherVal);
    }

    /**
     * Returns a number between 0 and 1 representing the estimated probability of player 1 winning the game.
     * The chance of player2 winning = 1 - chance of p1 winning.
     *
     * @param score best estimate of current move score.
     * @return estimated chance of player one winning the game
     */
    public static float getChanceOfPlayer1Winning(int score) {
        return computeChanceOfWinning((float) score);
    }

    /**
     * @param lastMoveValue inherited value for last move played.
     * @return Something close to 1 if the chance of winning for player 1 is really good. Close to -1 if bad.
     */
    private static float computeChanceOfWinning(float lastMoveValue) {
        float val = lastMoveValue + SearchStrategy.WINNING_VALUE;
        float chance = val / (2.0f * SearchStrategy.WINNING_VALUE);

        return (float) Math.max(0, Math.min(chance, 1.0));
    }
}