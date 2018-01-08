/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import java.util.Arrays;

/**
 * 眼位信息抽像类
 *
 * @author Barry Becker
 */
public abstract class AbstractEyeInformation implements EyeInformation {

    @Override
    public float[] getVitalPoints() {
        return new float[0];
    }

    @Override
    public float[] getEndPoints() {
        return new float[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEyeInformation that = (AbstractEyeInformation) o;

        return Arrays.equals(getEndPoints(), that.getEndPoints())
                && Arrays.equals(getVitalPoints(), that.getVitalPoints());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (getVitalPoints() != null ? Arrays.hashCode(getVitalPoints()) : 0);
        result = 31 * result + (getEndPoints() != null ? Arrays.hashCode(getEndPoints()) : 0);
        return result;
    }
}