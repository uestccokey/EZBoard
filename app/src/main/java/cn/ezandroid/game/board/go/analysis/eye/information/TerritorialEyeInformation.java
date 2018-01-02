/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * Describes an eye shape with 8 or more internal spaces.
 * It is almost certainly an unconditionally alive shape.
 * If its not, you have to play more to tell.
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

    public String toString() {
        return getTypeName();
    }
}