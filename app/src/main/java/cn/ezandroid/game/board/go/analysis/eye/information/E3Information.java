/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * 三空间眼位
 * <p>
 * 只有一种类型
 * 关键点位置有没有被填充决定了它的死活状态
 *
 * @author Barry Becker
 */
public class E3Information extends AbstractEyeSubtypeInformation {

    public E3Information() {
        initialize(false, 3, new float[]{2.02f});
    }

    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {
        EyeNeighborMap nbrMap = new EyeNeighborMap(eye);
        return handleVitalPointCases(nbrMap, eye, 1);
    }

    @Override
    public String getTypeName() {
        return "E112";
    }
}