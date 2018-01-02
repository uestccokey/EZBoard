/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;

/**
 * Meta data about the eye type.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 */
public interface EyeInformation {

    /**
     * The life property should be regarded as a property slightly below Benson's definition
     * of unconditional life, because if we have an AliveInAtari status for an eye, it might be
     * necessary to play inside the eye, but with the great advantage that detecting it
     * is just a matter of counting neighbours as it will be shown in Section 4.
     *
     * @return true if the shape has the life property
     */
    boolean hasLifeProperty();

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
     * The 3 points closed to a corner are considered a corner point triple
     * If an eye is in the corner, it is also on the edge.
     *
     * @return true if three of the eye points are one of the corner point triples.
     */
    boolean isInCorner(IGoEye eye);

    /**
     * An eye can be both in the corner and on the edge.
     *
     * @return true if at least three points line on the edge (corner triples are considered on edge too).
     */
    boolean isOnEdge(IGoEye eye);

    /**
     * @return Name of the eye type (a descriptive string line like E112233 where the numbers are the eye
     * neighbors for each space int eh eye in sorted order.
     */
    String getTypeName();
}