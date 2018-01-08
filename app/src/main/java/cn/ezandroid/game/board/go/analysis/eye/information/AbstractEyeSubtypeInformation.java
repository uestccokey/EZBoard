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

    /**
     * 初始化眼位子类型信息
     *
     * @param life    死活属性
     * @param eyeSize 眼位大小
     */
    void initialize(boolean life, int eyeSize) {
        initialize(life, eyeSize, EMPTY_POINTS, EMPTY_POINTS);
    }

    /**
     * 初始化眼位子类型信息
     *
     * @param life     死活属性
     * @param eyeSize  眼位大小
     * @param vitalPts 编码后的关键点数组
     */
    void initialize(boolean life, int eyeSize, float[] vitalPts) {
        initialize(life, eyeSize, vitalPts, EMPTY_POINTS);
    }

    /**
     * 初始化眼位子类型信息
     *
     * @param life     死活属性
     * @param eyeSize  眼位大小
     * @param vitalPts 编码后的关键点数组
     * @param endPts   编码后的坏点数组
     */
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

    @Override
    public EyeStatus determineStatus(IGoEye eye, GoBoard board) {
        return EyeStatus.NAKADE;
    }

    /**
     * 假如所有的关键点被占据，则该眼位设置为可点杀状态（NAKADE）
     * 假如还有一个关键点未被占据，则该眼位设置为不稳定状态（UNSETTLED）
     * 假如有两个或更多关键点未被占据，则该眼位设置为无条件活棋状态（ALIVE）
     *
     * @return
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
     * 处理ALIVE与ALIVE_IN_ATARI状态的区别
     *
     * @return
     */
    EyeStatus handleAliveOrAliveInAtari(IGoEye eye, GoBoard board) {
        GoBoardPositionList filledSpaces = findFilledSpaces(eye);
        if (eye.size() - filledSpaces.size() == 1 && eye.getGroup().getLiberties(board).size() == 1) {
            return EyeStatus.ALIVE_IN_ATARI;
        }
        return EyeStatus.ALIVE;
    }

    /**
     * 获取眼位中已经被棋子占据的点位列表
     *
     * @param eye
     * @return
     */
    private GoBoardPositionList findFilledSpaces(IGoEye eye) {
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
     * 获取眼位中指定位置被棋子占据的点位列表
     *
     * @param nbrMap
     * @param specialPoints
     * @param eye
     * @return
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