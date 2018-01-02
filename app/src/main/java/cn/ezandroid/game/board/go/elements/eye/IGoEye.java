/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.eye;

import cn.ezandroid.game.board.go.analysis.eye.information.EyeInformation;
import cn.ezandroid.game.board.go.analysis.eye.information.EyeStatus;
import cn.ezandroid.game.board.go.elements.IGoSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * A GoEye is composed of a strongly connected set of empty spaces (and possibly some dead enemy stones).
 *
 * @author Barry Becker
 */
public interface IGoEye extends IGoSet {

    EyeStatus getStatus();

    EyeInformation getInformation();

    String getEyeTypeName();

    int getNumCornerPoints();

    int getNumEdgePoints();

    /**
     * @return the group that this eye belongs to.
     */
    IGoGroup getGroup();

    /**
     * @return the hashSet containing the members
     */
    @Override
    GoBoardPositionSet getMembers();

    /**
     * empty the positions from the eye.
     */
    void clear();

    /**
     * @return true if unconditionally alive.
     */
    boolean isUnconditionallyAlive();

    void setUnconditionallyAlive(boolean unconditionallyAlive);
}