/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.board;

import cn.ezandroid.game.board.common.Move;
import cn.ezandroid.game.common.geometry.Location;

/**
 * 矩形棋盘接口
 *
 * @author Barry Becker
 */
public interface IRectangularBoard<M extends Move> extends IBoard<M> {

    /**
     * 修改棋盘大小
     * <p>
     * 注意：修改大小后需要调用reset方法重置棋盘，因为原来的棋盘现在无效了
     *
     * @param numRows
     * @param numCols
     */
    void setSize(int numRows, int numCols);

    /**
     * 获取棋盘行数
     *
     * @return
     */
    int getNumRows();

    /**
     * 获取棋盘列数
     *
     * @return
     */
    int getNumCols();

    BoardPosition getPosition(int row, int col);

    BoardPosition getPosition(Location loc);

    /**
     * 是否指定位置在棋盘范围内
     *
     * @param r
     * @param c
     * @return
     */
    boolean inBounds(int r, int c);

    /**
     * 深克隆
     *
     * @return
     */
    @Override
    IRectangularBoard copy();
}
