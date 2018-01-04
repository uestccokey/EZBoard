/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.neighbor;

/**
 * 邻居类型的枚举
 *
 * @author Barry Becker
 */
public enum NeighborType {

    /** 已被占用 */
    OCCUPIED,

    /** 未被占用 */
    UNOCCUPIED,

    /** 友方棋子 */
    FRIEND,

    /** 敌方棋子 */
    ENEMY,

    /** 非己方棋子 */
    NOT_FRIEND,

    /** 任意类型. */
    ANY
}

