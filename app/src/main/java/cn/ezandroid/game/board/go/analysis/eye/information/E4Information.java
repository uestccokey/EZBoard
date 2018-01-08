/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * 包含3种不同类型的4空间眼位
 *
 * @author Barry Becker
 */
public class E4Information extends AbstractEyeSubtypeInformation {

    public enum Eye4Type {
        E1122, E1113, E2222
    }

    private Eye4Type mE4Type;

    E4Information(String subTypeDesc) {
        mE4Type = Eye4Type.valueOf(subTypeDesc);
        switch (mE4Type) {
            case E1122:
                initialize(false, 4, new float[]{2.03f, 2.03f});
                break;
            case E1113:
                initialize(false, 4, new float[]{3.03f});
                break;
            case E2222:
                initialize(false, 4);
                break;
        }
    }

    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {
        EyeNeighborMap nbrMap = new EyeNeighborMap(eye);
        switch (mE4Type) {
            case E1122:
                return handleVitalPointCases(nbrMap, eye, 2);
            case E1113:
                return handleVitalPointCases(nbrMap, eye, 1);
            case E2222:
                return EyeStatus.UNSETTLED;
        }
        return EyeStatus.NAKADE; // never reached
    }

    @Override
    public String getTypeName() {
        return mE4Type.toString();
    }
}