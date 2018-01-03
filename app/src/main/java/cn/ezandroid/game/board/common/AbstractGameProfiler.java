/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common;

import cn.ezandroid.game.common.profile.Profiler;

/**
 * 抽象的游戏分析类
 *
 * @author Barry Becker
 */
public abstract class AbstractGameProfiler extends Profiler {

    protected static final String UNDO_MOVE = "undoing move";
    protected static final String MAKE_MOVE = "making move";
    protected static final String CALC_WORTH = "calculating worth";

    protected AbstractGameProfiler() {
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
