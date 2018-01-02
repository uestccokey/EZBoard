/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import java.util.Arrays;

import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 */
public abstract class AbstractEyeInformation implements EyeInformation {

    @Override
    public boolean hasLifeProperty() {
        return false;
    }

    @Override
    public float[] getVitalPoints() {
        return new float[0];
    }

    @Override
    public float[] getEndPoints() {
        return new float[0];
    }

    @Override
    public boolean isInCorner(IGoEye eye) {
        return eye.getNumCornerPoints() == 3;
    }

    @Override
    public boolean isOnEdge(IGoEye eye) {
        return eye.getNumEdgePoints() >= 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEyeInformation that = (AbstractEyeInformation) o;

        return hasLifeProperty() == that.hasLifeProperty()
                && Arrays.equals(getEndPoints(), that.getEndPoints())
                && Arrays.equals(getVitalPoints(), that.getVitalPoints());
    }

    @Override
    public int hashCode() {
        int result = (hasLifeProperty() ? 1 : 0);
        result = 31 * result + (getVitalPoints() != null ? Arrays.hashCode(getVitalPoints()) : 0);
        result = 31 * result + (getEndPoints() != null ? Arrays.hashCode(getEndPoints()) : 0);
        return result;
    }
}