/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * 眼位信息接口
 *
 * @author Barry Becker
 */
public interface EyeInformation {

    /**
     * A list of vital points described by indices.
     *
     * @return list of vital points where a point is described by an index created by adding the number of
     * neighbors to (those neighbor's neighbors)/100. so for example, 2.03 means the space has 2 nobi neighbors
     * and those 2 neighbors have a total of 3 neighbors.
     * Returns an empty array if we have no vitals or the type has the life property.
     */
    float[] getVitalPoints();

    /**
     * A list of end points described by indices.  End points are bad to play by either side until all the other eye
     * spaces have been played. If the opponent plays them they are not playing in the nakade (big eye) shape, and
     * hence missing a likely opportunity to kill the group. If the same color plays them they are helping to create a
     * nakade eye shape.  End points are the spaces left after the nakade shape of size n-1 fills the eye.
     *
     * @return list of end points where a point is described by an index created by adding the number of
     * neighbors to (those neighbor's neighbors)/100. so for example, 2.03 means the space has 2 nobi neighbors
     * and those 2 neighbors have a total of 3 neighbors.
     * Returns an empty array if we have no vitals or the type has the life property.
     */
    float[] getEndPoints();

    /**
     * @return eye status
     */
    EyeStatus determineStatus(IGoEye eye, GoBoard board);

    /**
     * @return Name of the eye type (a descriptive string line like E112233 where the numbers are the eye
     * neighbors for each space int eh eye in sorted order.
     */
    String getTypeName();
}