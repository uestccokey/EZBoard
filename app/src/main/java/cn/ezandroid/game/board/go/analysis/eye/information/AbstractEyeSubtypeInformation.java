/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import java.util.Arrays;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;

/**
 * 眼位子类型信息抽像类
 *
 * @author Barry Becker
 */
public abstract class AbstractEyeSubtypeInformation extends AbstractEyeInformation {

    private boolean mLife;
    private byte mSize;
    private float[] mVitalPoints; // 关键点
    private float[] mEndPoints;

    private static final float[] EMPTY_POINTS = new float[]{};

    AbstractEyeSubtypeInformation() {}

    void initialize(boolean life, int eyeSize) {
        initialize(life, eyeSize, EMPTY_POINTS, EMPTY_POINTS);
    }

    /**
     * 初始化眼位子类型信息
     * <p>
     * 关键点被编码后，以<紧邻的棋子数量>.<邻接棋子的邻接棋子的数量之和>的float型数字表示
     * 比如金字塔型的眼位关键点为3.03，因为金字塔中间关键点有3个邻接棋子，并且它的每个邻接棋子只有一个邻接棋子
     * 其他的比如星型眼位关键点为4.04，直三眼位的关键点位1.02
     *
     * @param life     死活属性
     * @param eyeSize  眼位大小
     * @param vitalPts 编码后的关键点数组
     */
    void initialize(boolean life, int eyeSize, float[] vitalPts) {
        initialize(life, eyeSize, vitalPts, EMPTY_POINTS);
    }

    void initialize(boolean life, int eyeSize, final float[] vitalPts, final float[] endPts) {
        this.mLife = life;
        this.mSize = (byte) eyeSize;
        this.mVitalPoints = vitalPts;
        this.mEndPoints = endPts;
    }

    /**
     * 获取眼位空间大小（眼位中敌方棋子也包含在内）
     *
     * @return
     */
    public byte getSize() {
        return mSize;
    }

    @Override
    public float[] getVitalPoints() {
        return mVitalPoints;
    }

    @Override
    public float[] getEndPoints() {
        return mEndPoints;
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
        if (mLife != that.mLife) return false;
        if (mSize != that.mSize) return false;
        if (!Arrays.equals(mEndPoints, that.mEndPoints)) return false;
        return Arrays.equals(mVitalPoints, that.mVitalPoints);
    }

    @Override
    public int hashCode() {
        int result = (mLife ? 1 : 0);
        result = 31 * result + (int) mSize;
        result = 31 * result + (mVitalPoints != null ? Arrays.hashCode(mVitalPoints) : 0);
        result = 31 * result + (mEndPoints != null ? Arrays.hashCode(mEndPoints) : 0);
        result = 31 * result + getTypeName().hashCode();
        return result;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append(this.getTypeName());
        bldr.append(" Life:").append(mLife);
        bldr.append(" Size:").append(mSize);
        return bldr.toString();
    }
}