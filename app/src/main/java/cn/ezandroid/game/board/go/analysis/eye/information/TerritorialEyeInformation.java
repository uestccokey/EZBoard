/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * 拥有8个或者更多空间的眼位，通常都是活型
 *
 * @author Barry Becker
 */
public class TerritorialEyeInformation extends AbstractEyeInformation {

    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {
        return EyeStatus.ALIVE;
    }

    @Override
    public String getTypeName() {
        return "Territorial";
    }
}