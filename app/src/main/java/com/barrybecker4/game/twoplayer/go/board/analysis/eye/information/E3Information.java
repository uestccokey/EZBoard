/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;

/**
 * Three space eye *** - there is only one type.
 * The vital point may or may not be filled - determining its status.
 *
 * @author Barry Becker
 */
public class E3Information extends AbstractEyeSubtypeInformation {

    public E3Information() {
        initialize(false, 3, new float[]{2.02f});
    }

    /**
     * @return eye status for E3 type.
     */
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