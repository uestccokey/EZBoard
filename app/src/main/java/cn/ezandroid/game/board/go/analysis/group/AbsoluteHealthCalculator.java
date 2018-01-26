/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.eye.EyeHealthEvaluator;
import cn.ezandroid.game.board.go.analysis.group.eye.GroupEyeCache;
import cn.ezandroid.game.board.go.elements.eye.GoEyeSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;

/**
 * 棋群的绝对健康值分析器
 *
 * @author Barry Becker
 */
class AbsoluteHealthCalculator {

    private IGoGroup mGroup;

    /**
     * 为-1到1之间的数字.
     * <p>
     * 很多因素都会引导到一个棋群的健康值，通常应该使用搜索算法来使得这个值更加精确
     * 当值为1时，说明这个棋群至少有两个眼，是无条件活棋状态
     * 当值为-1时，说明这个棋群即使连走两步也无法活棋
     * 接近0的分数表示，不是非常确定这个棋群是死还是活
     */
    private float mAbsoluteHealth = 0;

    /** 棋群的眼位缓存. */
    private GroupEyeCache mEyeCache;

    private GroupAnalyzerMap mAnalyzerMap;

    public AbsoluteHealthCalculator(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        mGroup = group;
        mAnalyzerMap = analyzerMap;
        mEyeCache = new GroupEyeCache(group, mAnalyzerMap);
    }

    /**
     * 眼位缓存是否有效，当棋群有改变时，眼位缓存无效
     *
     * @return
     */
    public boolean isValid() {
        return mEyeCache.isValid();
    }

    /**
     * 清除眼位缓存
     */
    public void invalidate() {
        mEyeCache.invalidate();
    }

    /**
     * 活棋眼位的潜在价值
     *
     * @return
     */
    public float getEyePotential() {
        return mEyeCache.getEyePotential();
    }

    /**
     * 计算棋群的绝对健康值
     * <p>
     * 所有棋群里的棋子拥有相同的健康值，因为棋群通常一起生活死（不一定会这样，因为棋串才是一定一起生或死，但是有关系的）
     * 对黑棋来说好的健康值是正值，对白棋来说好的健康值是负值
     * 绝对健康值由眼位数量（包括类型和状态）及气数决定
     * <p>
     * http://senseis.xmp.net/?BensonsAlgorithm
     *
     * @return 与周围棋群无关的绝对健康值
     */
    public float calculateAbsoluteHealth(GoBoard board) {
        if (mEyeCache.isValid()) {
//            GameContext.log(1, "cache valid. Returning health=" + mAbsoluteHealth);
            return mAbsoluteHealth;
        }

        int numLiberties = mGroup.getNumLiberties(board);

        // we multiply by a +/- sign depending on the side
        float side = mGroup.isOwnedByPlayer1() ? 1.0f : -1.0f;

        // first come up with some approximation for the health so update eyes can be done more accurately.
        float numEyes = mEyeCache.calcNumEyes();
        int numStones = mGroup.getNumStones();
        LifeAnalyzer lifeAnalyzer = new LifeAnalyzer(mGroup, board, mAnalyzerMap);
        EyeHealthEvaluator eyeEvaluator = new EyeHealthEvaluator(lifeAnalyzer);

        mAbsoluteHealth = eyeEvaluator.determineHealth(side, numEyes, numLiberties, numStones);

        mEyeCache.updateEyes(board);  // expensive

        float eyePotential = mEyeCache.getEyePotential();
        float revisedNumEyes = mEyeCache.calcNumEyes();
        numEyes = Math.max(eyePotential, revisedNumEyes);

        // health based on eye shape - the most significant factor
        float health = eyeEvaluator.determineHealth(side, numEyes, numLiberties, numStones);

        // No bonus at all for false eyes
        mAbsoluteHealth = health;
        if (Math.abs(mAbsoluteHealth) > 1.0) {
//            GameContext.log(0, "Warning: health exceeded 1.0: " + " health=" + health + " numEyes=" + numEyes);
            mAbsoluteHealth = side;
        }

        return mAbsoluteHealth;
    }

    /**
     * 获取这个棋群的眼位集合
     *
     * @return
     */
    public GoEyeSet getEyes(GoBoard board) {
        if (!mEyeCache.isValid()) {
            calculateAbsoluteHealth(board);
        }
        return mEyeCache.getEyes(board);
    }
}
