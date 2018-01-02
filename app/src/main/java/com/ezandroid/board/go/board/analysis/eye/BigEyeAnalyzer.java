/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.analysis.eye;

import com.ezandroid.board.go.board.analysis.eye.information.EyeInformation;
import com.ezandroid.board.go.board.analysis.eye.information.EyeType;
import com.ezandroid.board.go.board.elements.eye.IGoEye;
import com.ezandroid.board.go.board.elements.position.GoBoardPosition;
import com.ezandroid.board.go.board.elements.position.GoBoardPositionSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Determine properties about a big eye on the board.
 * This analyzer is only used by the EyeTypeAnalyzer.
 * It classifies eyes that are not false eye and have between 2 and 8 spaces_.
 * Some of those spaces_ may have enemy stones in them.
 * See EyeTypeAnalyzer
 *
 * @author Barry Becker
 */
class BigEyeAnalyzer {

    /** spaces_ in the eye */
    private GoBoardPositionSet spaces_;

    /**
     * The eye must have between 3 and 8 spaces_.
     *
     * @param eye the eye to analyze
     */
    BigEyeAnalyzer(IGoEye eye) {

        spaces_ = eye.getMembers();
        int size = spaces_.size();
        assert (size > 3 && size < 8);
    }

    /**
     * For some eyes (like big eyes) there are one or more key points that will make a single eye if
     * the opponent plays first (or first and second if 2 key points), or 2 eyes if you play first.
     * We refer to the paper "When One Eye is Sufficient: A Static Classification"
     * to classify the different eye types based solely on eye-point neighbors.
     * <p>
     * The pattern formed by the sorted list of neighbor counts uniquely determines the type.
     *
     * @return the eye type determined based on the properties and neighbors of the positions in the spaces_ list.
     */
    EyeInformation determineEyeInformation() {
        List<Integer> counts = new ArrayList<>(7);

        for (GoBoardPosition space : spaces_) {
            counts.add(getNumEyeNobiNeighbors(space));
        }
        Collections.sort(counts);

        return getEyeInformation(counts);
    }

    private EyeInformation getEyeInformation(List<Integer> counts) {
        StringBuilder bldr = new StringBuilder("E");
        for (int num : counts) {
            bldr.append(num);
        }
        EyeType type = EyeType.valueOf("E" + counts.size());

        return type.getInformation(bldr.toString());
    }

    /**
     * @param space eye space to check
     * @return number of eye-space nobi neighbors.
     * these neighbors may either be blanks or dead stones of the opponent
     */
    private int getNumEyeNobiNeighbors(GoBoardPosition space) {
        int numNbrs = 0;
        for (GoBoardPosition eyeSpace : spaces_) {

            if (space.isNeighbor(eyeSpace))
                numNbrs++;
        }
        return numNbrs;
    }
}
