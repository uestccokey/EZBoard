/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;

/**
 * Eye6Type containing MetaData for the different possible Eye shapes of size 7.
 * There are 14 different subtypes to consider (if you count the 2 sub-subtypes of E1112234).
 *
 * @author Barry Becker
 */
public class E7Information extends AbstractEyeSubtypeInformation {

    /** Different sorts of eye with 7 spaces. */
    public enum Eye7Type {
        E1122222, E1112223, E1122233, E1111233, E1222223, E1111224, E1112333,
        E1222333, E1112234, E1112234a, E1112234b, E1222234, E1122224, E2222224
    }

    private Eye7Type type;

    /**
     * Constructor
     *
     * @param subTypeDesc description of the type - something like "E112223".
     */
    E7Information(String subTypeDesc) {
        type = Eye7Type.valueOf(subTypeDesc);
        switch (type) {
            case E1122222:
                initialize(true, 7);
                break;
            case E1112223:
                initialize(true, 7);
                break;
            case E1122233:
                initialize(true, 7);
                break;
            case E1111233:
                initialize(true, 7);
                break;
            case E1222223:
                initialize(true, 7);
                break;
            case E1111224:
                initialize(true, 7);
                break;
            case E1112333:
                initialize(true, 7);
                break;
            case E1222333:
                initialize(true, 7);
                break;
            case E1112234:
                initialize(false, 7);
                break;
            case E1112234a:
                initialize(false, 7, new float[]{2.07f, 3.05f, 4.06f, 2.07f},
                        new float[]{1.03f});
                break;
            case E1112234b:
                initialize(false, 7, new float[]{3.07f, 4.07f},
                        new float[]{1.03f});
                break;
            case E1222234:
                initialize(false, 7, new float[]{4.08f},
                        new float[]{1.04f});
                break;
            case E1122224:
                initialize(false, 7, new float[]{2.05f, 4.07f},
                        new float[]{1.02f});
                break;
            case E2222224:
                initialize(false, 7, new float[]{4.10f},
                        new float[]{1.04f, 1.04f});
                break;
        }
    }

    /**
     * @return eye status for E7 types.
     */
    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {
        EyeNeighborMap nbrMap = new EyeNeighborMap(eye);
        switch (type) {
            case E1122222:
            case E1112223:
            case E1122233:
            case E1111233:
            case E1222223:
            case E1111224:
            case E1112333:
            case E1222333:
                return handleSubtypeWithLifeProperty(eye, board);
            case E1112234:
                Eye7Type E112233Subtype = determineE1112234Subtype(nbrMap);
                if (E112233Subtype == Eye7Type.E1112234a) {
                    return handleVitalPointCases(nbrMap, eye, 4);
                } else {
                    return handleVitalPointCases(nbrMap, eye, 2);
                }
            case E1222234:
                return handleVitalPointCases(nbrMap, eye, 1);
            case E1122224:
                return handleVitalPointCases(nbrMap, eye, 2);
            case E2222224:
                return handleVitalPointCases(nbrMap, eye, 1);
        }
        return EyeStatus.NAKADE; // never reached
    }

    /**
     * find the 2 spaces with only 1 nbr
     * if the box defined by those 2 positions contains the other 4 spaces, then case b, else a
     *
     * @return the subtype E112233a or E112233b
     */
    private Eye7Type determineE1112234Subtype(EyeNeighborMap nbrMap) {

        GoBoardPositionList oneNbrPoints = new GoBoardPositionList();
        GoBoardPositionList otherPoints = new GoBoardPositionList();

        for (GoBoardPosition pos : nbrMap.keySet()) {
            if (nbrMap.getNumEyeNeighbors(pos) == 1) {
                oneNbrPoints.add(pos);
            } else {
                otherPoints.add(pos);
            }
        }
        assert oneNbrPoints.size() == 3;  // hitting this
        Box bounds = new Box(oneNbrPoints.getFirst().getLocation(), oneNbrPoints.get(1).getLocation());
        bounds.expandBy(oneNbrPoints.get(2).getLocation());

        for (GoBoardPosition otherPt : otherPoints) {
            if (!bounds.contains(otherPt.getLocation())) {
                return Eye7Type.E1112234a;
            }
        }
        return Eye7Type.E1112234b;
    }

    @Override
    public String getTypeName() {
        return type.toString();
    }
}