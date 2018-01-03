/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.eye;

import cn.ezandroid.game.board.go.analysis.eye.information.EyeInformation;
import cn.ezandroid.game.board.go.analysis.eye.information.EyeStatus;
import cn.ezandroid.game.board.go.elements.IGoSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 眼位模型
 * <p>
 * 眼位由一组强连接的空点组成（有时也包含敌方死子）
 *
 * @author Barry Becker
 */
public interface IGoEye extends IGoSet {

    @Override
    GoBoardPositionSet getMembers();

    /**
     * 获取该眼位所属的棋群
     *
     * @return
     */
    IGoGroup getGroup();

    EyeStatus getStatus();

    EyeInformation getInformation();

    String getEyeTypeName();

    int getNumCornerPoints();

    int getNumEdgePoints();

    void clear();

    boolean isUnconditionallyAlive();

    void setUnconditionallyAlive(boolean unconditionallyAlive);
}