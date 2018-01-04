/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.eye;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.eye.EyeTypeAnalyzer;
import cn.ezandroid.game.board.go.analysis.eye.information.EyeInformation;
import cn.ezandroid.game.board.go.analysis.eye.information.EyeStatus;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzer;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoString;

/**
 * 眼位模型
 * <p>
 * 眼位由一组强连接的空点组成（有时也包含敌方死子）
 * 一般来说一个棋群需要两个真眼才能保证活棋
 *
 * @author Barry Becker
 */
public class GoEye extends GoString implements IGoEye {

    // 眼位中的位置点集合
    private GoBoardPositionSet mMembers;

    // 眼位类型
    private final EyeInformation mInformation;

    // 眼位状态
    private final EyeStatus mStatus;

    // 眼位包含的角落3角点数量
    private int mNumCornerPoints;

    // 眼位包含的边界点数量
    private int mNumEdgePoints;

    public GoEye(GoBoardPositionList spaces, GoBoard board, IGoGroup g, GroupAnalyzer groupAnalyzer) {
        super(spaces, board);
        mGroup = g;
        mIsOwnedByPlayer1 = g.isOwnedByPlayer1();

        EyeTypeAnalyzer eyeAnalyzer = new EyeTypeAnalyzer(this, board, groupAnalyzer);
        mInformation = eyeAnalyzer.determineEyeInformation();
        mStatus = mInformation.determineStatus(this, board);
        initializePositionCounts(board);
    }

    @Override
    public EyeInformation getInformation() {
        return mInformation;
    }

    @Override
    public EyeStatus getStatus() {
        return mStatus;
    }

    @Override
    public String getEyeTypeName() {
        if (mInformation == null)
            return "unknown eye type";
        return mInformation.getTypeName();
    }

    @Override
    public int getNumCornerPoints() {
        return mNumCornerPoints;
    }

    @Override
    public int getNumEdgePoints() {
        return mNumEdgePoints;
    }

    @Override
    protected void initializeMembers() {
        mMembers = new GoBoardPositionSet();
    }

    private void initializePositionCounts(GoBoard board) {
        mNumCornerPoints = 0;
        mNumEdgePoints = 0;
        for (GoBoardPosition pos : getMembers()) {
            if (board.isCornerTriple(pos)) {
                mNumCornerPoints++;
            }
            if (board.isOnEdge(pos)) {
                mNumEdgePoints++;
            }
        }
    }

    @Override
    public GoBoardPositionSet getMembers() {
        return mMembers;
    }

    @Override
    protected void addMemberInternal(GoBoardPosition space, GoBoard board) {
        if (getMembers().contains(space)) {
            assert ((space.getString() == null) || (this == space.getString())) :
                    "bad space or bad owning string" + space.getString();
        }
        space.setEye(this);
        getMembers().add(space);
    }

    @Override
    public void clear() {
        for (GoBoardPosition pos : getMembers()) {
            pos.setEye(null);
            pos.setVisited(false);
        }
        setGroup(null);
        getMembers().clear();
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder("GoEye: ");
        bldr.append(" ownedByPlayer1=").append(isOwnedByPlayer1());
        bldr.append(" status=").append(getStatus());
        bldr.append(" info=").append("[").append(getInformation()).append("]");
        bldr.append(" num corner pts=").append(getNumCornerPoints());
        bldr.append(" num edge pts=").append(getNumEdgePoints());
        bldr.append(" UnconditionallyAlive=").append(isUnconditionallyAlive());
        bldr.append(" num members=").append(size());
        return bldr.toString();
    }
}