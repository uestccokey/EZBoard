/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.analysis.eye.information;

import com.ezandroid.board.go.board.GoBoard;
import com.ezandroid.board.go.board.elements.eye.IGoEye;
import com.ezandroid.board.go.board.elements.position.GoBoardPosition;
import com.ezandroid.board.go.board.elements.position.GoBoardPositionList;

import java.util.Arrays;

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 */
public abstract class AbstractEyeSubtypeInformation extends AbstractEyeInformation {

    private boolean life;
    private byte size;
    private float[] vitalPoints;
    private float[] endPoints;

    private static final float[] EMPTY_POINTS = new float[]{};

    /**
     * Constructor
     */
    AbstractEyeSubtypeInformation() {}

    void initialize(boolean life, int eyeSize) {
        initialize(life, eyeSize, EMPTY_POINTS, EMPTY_POINTS);
    }

    /**
     * Initialize the subtype information.
     * Vital points are encoded as a floating point number of the form <num nobi neighbors>.<num neighbor neighbors>
     * where the number of neighbor neighbors is the total of all the nobie neighbors nobi neighbors.
     * For example the encoded vital point for a pyramid shaped eye is 3.03 because
     * - the central key point position has 3 neighbors and
     * - each of those neighbors has only one neighbor (the center)
     * A few other examples:
     * For a star shape, the vital point is 4.04.
     * For three in a row, the vital point is 1.02.
     *
     * @param life     true if this shape has the life property.
     * @param eyeSize  number of spaces in the eye (e.g. 4 for a pyramid eye shape)
     * @param vitalPts Encoded location of the vital points
     */
    void initialize(boolean life, int eyeSize, float[] vitalPts) {
        initialize(life, eyeSize, vitalPts, EMPTY_POINTS);
    }

    @SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter"})
    void initialize(boolean life, int eyeSize,
                    final float[] vitalPts, final float[] endPts) {
        this.life = life;
        this.size = (byte) eyeSize;
        this.vitalPoints = vitalPts;
        this.endPoints = endPts;
    }

    /**
     * @return the number of spaces in they eye (maybe be filled with some enemy stones).
     */
    public byte getSize() {
        return size;
    }

    @Override
    public boolean hasLifeProperty() {
        return life;
    }

    @Override
    public float[] getVitalPoints() {
        return vitalPoints;
    }

    @Override
    public float[] getEndPoints() {
        return endPoints;
    }

    /**
     * We only need to consider the non-life property status.
     *
     * @param eye   eye
     * @param board board
     * @return status of the eye shape.
     */
    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {
        return EyeStatus.NAKADE;
    }

    /**
     * If all the vital points have been filled, then we have nakade status (one big eye).
     * If all but one vital point has been filled, then we are unsettles - could be on eor two eyes.
     * If 2 or more vitals are still open, then we assume that this will become 2 eyes.
     *
     * @return status of shape with numVitals vital points.
     */
    EyeStatus handleVitalPointCases(EyeNeighborMap nbrMap, IGoEye eye, final int numVitals) {
        GoBoardPositionList vitalFilledSpaces = findSpecialFilledSpaces(nbrMap, getVitalPoints(), eye);
        int numFilledVitals = vitalFilledSpaces.size();
        assert numFilledVitals <= numVitals :
                "The number of filled vitals (" + numFilledVitals + ") " +
                        "was greater than the total number of vitals (" + numVitals + ") vitals="
                        + Arrays.toString(getVitalPoints()) + " eye=" + eye;

        if (numFilledVitals == numVitals) {
            return EyeStatus.NAKADE;
        } else if (numFilledVitals == numVitals - 1) {
            return EyeStatus.UNSETTLED;
        } else return EyeStatus.ALIVE;
    }

    /**
     * I suppose, in very rare cases, there could be a same side stone among the enemy filled spaces in the eye.
     *
     * @return the eye spaces that have enemy stones in them.
     */
    GoBoardPositionList findFilledSpaces(IGoEye eye) {
        GoBoardPositionList filledSpaces = new GoBoardPositionList();
        for (GoBoardPosition space : eye.getMembers()) {
            if (space.isOccupied()) {
                assert eye.isOwnedByPlayer1() != space.getPiece().isOwnedByPlayer1();
                filledSpaces.add(space);
            }
        }
        return filledSpaces;
    }

    /**
     * @return the set of special spaces (vital or end) that have enemy stones in them.
     */
    GoBoardPositionList findSpecialFilledSpaces(EyeNeighborMap nbrMap, float[] specialPoints, IGoEye eye) {
        GoBoardPositionList specialFilledSpaces = new GoBoardPositionList();
        for (GoBoardPosition space : eye.getMembers()) {
            if (space.isOccupied()) {
                assert eye.isOwnedByPlayer1() != space.getPiece().isOwnedByPlayer1();
                if (nbrMap.isSpecialPoint(space, specialPoints)) {
                    specialFilledSpaces.add(space);
                }
            }
        }
        return specialFilledSpaces;
    }

    /**
     * When the eye type has the life property, we can only be alive or alive in atari.
     *
     * @return either alive or alive in atari (rare)
     */
    EyeStatus handleSubtypeWithLifeProperty(IGoEye eye, GoBoard board) {
        GoBoardPositionList filledSpaces = findFilledSpaces(eye);
        if (eye.size() - filledSpaces.size() == 1 && eye.getGroup().getLiberties(board).size() == 1) {
            return EyeStatus.ALIVE_IN_ATARI;
        }
        return EyeStatus.ALIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEyeSubtypeInformation that = (AbstractEyeSubtypeInformation) o;

        if (!getTypeName().equals(that.getTypeName())) return false;
        if (life != that.life) return false;
        if (size != that.size) return false;
        if (!Arrays.equals(endPoints, that.endPoints)) return false;
        return Arrays.equals(vitalPoints, that.vitalPoints);
    }

    @Override
    public int hashCode() {
        int result = (life ? 1 : 0);
        result = 31 * result + (int) size;
        result = 31 * result + (vitalPoints != null ? Arrays.hashCode(vitalPoints) : 0);
        result = 31 * result + (endPoints != null ? Arrays.hashCode(endPoints) : 0);
        result = 31 * result + getTypeName().hashCode();
        return result;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append(this.getTypeName());
        bldr.append(" lifeProp=").append(life);
        bldr.append(" size=").append(size);
        return bldr.toString();
    }
}