/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.string;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.IGoSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 棋串接口
 *
 * @author Barry Becker
 */
public interface IGoString extends IGoSet {

    @Override
    GoBoardPositionSet getMembers();

    /**
     * 获取该棋串所属的棋群
     *
     * @return
     */
    IGoGroup getGroup();

    /**
     * 设置该棋串所属的棋群
     *
     * @param group
     */
    void setGroup(IGoGroup group);

    /**
     * 检查当前棋串是否包含指定位置点
     *
     * @param pos
     * @return
     */
    boolean contains(GoBoardPosition pos);

    /**
     * 检查这个棋串是否无条件存活（根据Benson算法）
     *
     * @return
     */
    boolean isUnconditionallyAlive();

    /**
     * 设置是否无条件存活
     *
     * @param unconditionallyAlive
     */
    void setUnconditionallyAlive(boolean unconditionallyAlive);

    void remove(GoBoardPosition stone, GoBoard board);

    /**
     * 更新棋串成员的健康评分为指定值
     *
     * @param health 取值[-1~1]
     */
    void updateTerritory(float health);
}
