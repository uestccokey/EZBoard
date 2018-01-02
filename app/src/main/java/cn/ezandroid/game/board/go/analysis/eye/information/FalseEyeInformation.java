/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * Info about a false eye
 *
 * @author Barry Becker
 */
public class FalseEyeInformation extends AbstractEyeInformation {

    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {
        if (eye.getMembers().size() > 5) {
            return EyeStatus.NAKADE;
        }
        if (eye.getMembers().size() > 2) {
            return EyeStatus.UNSETTLED;
        } else return EyeStatus.KO;
    }

    @Override
    public String getTypeName() {
        return "False";
    }
}
