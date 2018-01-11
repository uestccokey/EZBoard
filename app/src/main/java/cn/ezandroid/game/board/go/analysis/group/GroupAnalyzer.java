/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.GoEyeSet;
import cn.ezandroid.game.board.go.elements.group.GoGroupChangeListener;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * 分析一个棋群的死活以及眼位和气等属性
 *
 * @author Barry Becker
 */
public class GroupAnalyzer implements GoGroupChangeListener {

    // 当前正在分析的棋群
    private IGoGroup mGroup;

    private StoneInGroupAnalyzer mStoneInGroupAnalyzer;

    // 这个健康值也在-1~1之间，比绝对健康值更加精确，因为它考虑到了周围棋串的健康值
    private float mRelativeHealth;

    private AbsoluteHealthCalculator mAbsoluteHealthCalculator;

    // 缓存绝对健康值，避免不必要的重复计算
    private float mAbsoluteHealth;

    private GroupAnalyzerMap mAnalyzerMap;

    public GroupAnalyzer(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        mGroup = group;
        mAnalyzerMap = analyzerMap;
        mAbsoluteHealthCalculator = new AbsoluteHealthCalculator(group, mAnalyzerMap);
        mStoneInGroupAnalyzer = new StoneInGroupAnalyzer(group);
        group.addChangeListener(this);
    }

    public IGoGroup getGroup() {
        return mGroup;
    }

    /**
     * 当棋群增加和删除棋子时调用
     */
    @Override
    public void onGoGroupChanged() {
        invalidate();
    }

    /**
     * 获取与邻近棋群无关的绝对健康值
     *
     * @return
     */
    public float getAbsoluteHealth() {
        return mAbsoluteHealth;
    }

    /**
     * 获取与邻近棋群相关的相对健康值
     *
     * @param board
     * @param useCachedValue
     * @return
     */
    public float getRelativeHealth(GoBoard board, boolean useCachedValue) {
        if (isValid() || useCachedValue) {
            if (!isValid())
                GameContext.log(3, "using cached relative health when not valid");
            return getRelativeHealth();
        }
        GameContext.log(0, "stale abs health. recalculating relative health");
        return calculateRelativeHealth(board);
    }

    /**
     * 获取与邻近棋群相关的相对健康值
     *
     * @return
     */
    private float getRelativeHealth() {
        return mRelativeHealth;
    }

    private void invalidate() {
        mAbsoluteHealthCalculator.invalidate();
    }

    private boolean isValid() {
        return mAbsoluteHealthCalculator.isValid();
    }

    public int getNumLiberties(GoBoard board) {
        return mGroup.getNumLiberties(board);
    }

    public GoEyeSet getEyes(GoBoard board) {
        return mAbsoluteHealthCalculator.getEyes(board);
    }

    public float calculateAbsoluteHealth(GoBoard board) {
        mAbsoluteHealth = mAbsoluteHealthCalculator.calculateAbsoluteHealth(board);
        return mAbsoluteHealth;
    }

    /**
     * 如果棋子之间健康值差距很大，那它们将不是真正的敌人，因为他们其中的一个已经死了
     *
     * @param pos
     * @return
     */
    public boolean isTrueEnemy(GoBoardPosition pos) {
        assert (pos.isOccupied());
        GoStone stone = (GoStone) pos.getPiece();
        boolean muchWeaker = isStoneMuchWeakerThanGroup(stone);

        return (stone.isOwnedByPlayer1() != mGroup.isOwnedByPlayer1() && !muchWeaker);
    }

    /**
     * 计算棋群的相对健康值
     * <p>
     * 这个方法只能在所有棋群都调用calculateAbsoluteHealth后才能调用
     *
     * @param board
     * @return
     */
    public float calculateRelativeHealth(GoBoard board) {
        if (!isValid()) {
            calculateAbsoluteHealth(board);
        }

        RelativeHealthCalculator relativeCalculator = new RelativeHealthCalculator(mGroup, mAnalyzerMap);
        mRelativeHealth = relativeCalculator.calculateRelativeHealth(board, mAbsoluteHealth);
        return mRelativeHealth;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        ((GroupAnalyzer) clone).mAbsoluteHealthCalculator = new AbsoluteHealthCalculator(mGroup, mAnalyzerMap);
        return clone;
    }

    /**
     * 是否指定棋子比当前棋群更弱
     *
     * @param stone
     * @return
     */
    private boolean isStoneMuchWeakerThanGroup(GoStone stone) {
        return mStoneInGroupAnalyzer.isStoneMuchWeakerThanGroup(stone, getAbsoluteHealth());
    }
}
