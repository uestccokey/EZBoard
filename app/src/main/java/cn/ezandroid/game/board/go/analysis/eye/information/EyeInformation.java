/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * 眼位信息接口
 *
 * @author Barry Becker
 */
public interface EyeInformation {

    /**
     * 获取编码后的关键点数组
     * <p>
     * 关键点被编码后，以<紧邻的棋子数量>.<邻接棋子的邻接棋子的数量之和/100>的float型数字表示
     * 比如金字塔型的眼位关键点为3.03，因为金字塔中间关键点有3个邻接棋子，并且它的每个邻接棋子只有一个邻接棋子
     * 其他的比如星型眼位关键点为4.04，直三眼位的关键点位1.02
     *
     * @return
     */
    float[] getVitalPoints();

    /**
     * 获取编码后的坏点数组
     * <p>
     * 坏点被编码后，以<紧邻的棋子数量>.<邻接棋子的邻接棋子的数量之和/100>的float型数字表示
     * 坏点表示此点被眼位同色的玩家落子后会变成一个可点杀眼的情况
     * 比如
     * > X
     * >XXXX
     * > X
     * 此种眼位，第二行最右边的点位坏点，编码后为1.02
     *
     * @return
     */
    float[] getEndPoints();

    /**
     * 确定眼位状态
     *
     * @return
     */
    EyeStatus determineStatus(IGoEye eye, GoBoard board);

    /**
     * 获取眼位类型名称
     *
     * @return
     */
    String getTypeName();
}