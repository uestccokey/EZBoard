/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * 分析棋子相对于他所处的棋群的强度
 *
 * @author Barry Becker
 */
public class StoneInGroupAnalyzer {

    // 一个敌方棋子考虑为己方眼位的一部分时，相对于己方棋群的健康差距阈值 0.6~1.8
    private static final float DIFFERENCE_THRESHOLD = 0.6f;

    // 用来决定棋子死活的阈值
    private static final float MIN_LIFE_THRESH = 0.2f;

    // 当前正在分析的棋群
    private IGoGroup mGroup;

    public StoneInGroupAnalyzer(IGoGroup group) {
        mGroup = group;
    }

    /**
     * 是否指定棋子比当前棋群更弱
     *
     * @param stone
     * @param absoluteHealth 棋群的绝对健康值
     * @return
     */
    public boolean isStoneMuchWeakerThanGroup(GoStone stone, float absoluteHealth) {
        return isStoneWeakerThanGroup(stone, DIFFERENCE_THRESHOLD, absoluteHealth);
    }

    private boolean isStoneWeakerThanGroup(GoStone stone, float threshold, float groupHealth) {
        float constrainedGroupHealth = getConstrainedGroupHealth(groupHealth);

        float stoneHealth = stone.getHealth();
        boolean muchWeaker;
        if (stone.isOwnedByPlayer1()) {
            assert (!mGroup.isOwnedByPlayer1());
            muchWeaker = (-constrainedGroupHealth - stoneHealth > threshold);
        } else {
            assert (mGroup.isOwnedByPlayer1());
            muchWeaker = (constrainedGroupHealth + stoneHealth > threshold);
        }

        return muchWeaker;
    }

    private float getConstrainedGroupHealth(float rawGroupHealth) {
        float health = rawGroupHealth;
        if (mGroup.isOwnedByPlayer1() && rawGroupHealth < MIN_LIFE_THRESH) {
            health = MIN_LIFE_THRESH;
        } else if (!mGroup.isOwnedByPlayer1() && rawGroupHealth > -MIN_LIFE_THRESH) {
            health = -MIN_LIFE_THRESH;
        }
        return health;
    }
}
