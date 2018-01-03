/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements;

import java.util.Set;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 围棋构件集接口
 *
 * @author Barry Becker
 */
public interface IGoSet extends IGoMember {

    /**
     * 获取围棋构件的集合
     *
     * @return
     */
    Set<? extends IGoMember> getMembers();

    /**
     * 获取围棋构件的数目
     */
    int size();

    /**
     * 是否属于1号玩家
     *
     * @return
     */
    boolean isOwnedByPlayer1();

    /**
     * 标记构件集是否被访问过
     *
     * @param visited
     */
    void setVisited(boolean visited);

    /**
     * 获取周围气的集合
     *
     * @param board
     * @return
     */
    GoBoardPositionSet getLiberties(GoBoard board);

    /**
     * 获取周围气的数量
     *
     * @param board
     * @return
     */
    int getNumLiberties(GoBoard board);
}