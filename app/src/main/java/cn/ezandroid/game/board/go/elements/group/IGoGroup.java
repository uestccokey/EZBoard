/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.group;

import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.go.elements.IGoSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 棋群接口
 *
 * @author Barry Becker
 */
public interface IGoGroup extends IGoSet {

    void addMember(IGoString string);

    @Override
    GoStringSet getMembers();

    @Override
    boolean isOwnedByPlayer1();

    void addChangeListener(GoGroupChangeListener listener);

    int getNumStones();

    boolean containsStone(GoBoardPosition stone);

    void remove(IGoString string);

    GoBoardPositionSet getStones();

    /**
     * 更新棋群成员的健康评分为指定值
     *
     * @param health 取值[-1~1]
     */
    void updateTerritory(float health);

    Box findBoundingBox();
}