/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.profile.Profiler;

/**
 * Keep track of timing info for different game searching aspects.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public abstract class AbstractGameProfiler extends Profiler {

    protected static final String GENERATE_MOVES = "generating moves";
    protected static final String UNDO_MOVE = "undoing move";
    protected static final String MAKE_MOVE = "making move";
    protected static final String CALC_WORTH = "calculating worth";

    private long searchTime;

    /**
     * protected constructor.
     */
    protected AbstractGameProfiler() {
    }

    /**
     * Start profiling the game search.
     */
    public void startProfiling() {
        searchTime = 0;
        if (GameContext.isProfiling()) {
            searchTime = System.currentTimeMillis();
            initialize();
        }
    }

    /**
     * Stop profiling and report the stats.
     *
     * @param numMovesConsidered the number of moves considered during search.
     */
    public void stopProfiling(long numMovesConsidered) {
        if (GameContext.isProfiling()) {
            long totalTime = System.currentTimeMillis() - searchTime;
            showProfileStats(totalTime, numMovesConsidered);
        }
    }

    /**
     * Export some useful performance profile statistics in the log.
     *
     * @param totalTime          total elapsed time.
     * @param numMovesConsidered number of moves inspected during search.
     */
    void showProfileStats(long totalTime, long numMovesConsidered) {
        GameContext.log(0, "----------------------------------------------------------------------------------\n");
        GameContext.log(0, "There were " + numMovesConsidered + " moves considered.\n");
        GameContext.log(0, "The total time for the computer to move was : " +
                FormatUtil.formatNumber((float) totalTime / 1000) + " seconds.\n");
        print();
    }

    void initialize() {
        resetAll();
        setEnabled(GameContext.isProfiling());
        setLogger(GameContext.getLogger());
    }

    public void startGenerateMoves() {
        this.start(GENERATE_MOVES);
    }

    public void stopGenerateMoves() {
        this.stop(GENERATE_MOVES);
    }

    public void startUndoMove() {
        this.start(UNDO_MOVE);
    }

    public void stopUndoMove() {
        this.stop(UNDO_MOVE);
    }

    public void startMakeMove() {
        this.start(MAKE_MOVE);
    }

    public void stopMakeMove() {
        this.stop(MAKE_MOVE);
    }

    public void startCalcWorth() {
        this.start(CALC_WORTH);
    }

    public void stopCalcWorth() {
        this.stop(CALC_WORTH);
    }
}
