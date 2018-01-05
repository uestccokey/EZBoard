/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.group;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.elements.GoSet;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 棋群模型
 * <p>
 * 棋群由一些松散连接的同色棋子组成，比如通过尖，一间跳，飞等可以连接的棋子
 * 如图
 * __***
 * _**S**
 * _*SXS*
 * _**S**
 * __***
 * X与周围20个点属于棋群关系
 *
 * @author Barry Becker
 */
public final class GoGroup extends GoSet implements IGoGroup {

    // 棋群中所有棋串集合
    private GoStringSet mMembers;

    // 缓存的气点集合
    private GoBoardPositionSet mCachedLiberties;

    // 棋群变化监听器列表
    private List<GoGroupChangeListener> mChangeListeners;

    public GoGroup(IGoString string) {
        mIsOwnedByPlayer1 = string.isOwnedByPlayer1();

        getMembers().add(string);
        string.setGroup(this);
        commonInit();
    }

    public GoGroup(GoBoardPositionList stones) {
        mIsOwnedByPlayer1 = (stones.getFirst()).getPiece().isOwnedByPlayer1();
        for (GoBoardPosition stone : stones) {
            assimilateStone(stone);
        }
        commonInit();
    }

    private void commonInit() {
        mChangeListeners = new LinkedList<>();
    }

    @Override
    public void addChangeListener(GoGroupChangeListener listener) {
        mChangeListeners.add(listener);
    }

    public void removeChangeListener(GoGroupChangeListener listener) {
        mChangeListeners.remove(listener);
    }

    /**
     * 将一个棋子及所在的棋串加入到该棋群中
     *
     * @param stone
     */
    private void assimilateStone(GoBoardPosition stone) {
        assert stone.getPiece().isOwnedByPlayer1() == mIsOwnedByPlayer1 :
                "Stones in group must all be owned by the same player.";
        // actually this is ok - sometimes happens legitimately
        // assert isFalse(stone.isVisited(), stone+" is marked visited in "+stones+" when it should not be.");
        IGoString string = stone.getString();
        assert (string != null) : "There is no owning string for " + stone;
        if (!getMembers().contains(string)) {
            assert (mIsOwnedByPlayer1 == string.isOwnedByPlayer1()) : string + "ownership not the same as " + this;
            //string.confirmOwnedByOnlyOnePlayer();
            getMembers().add(string);
        }
        string.setGroup(this);
    }

    @Override
    protected void initializeMembers() {
        mMembers = new GoStringSet();
    }

    @Override
    public GoStringSet getMembers() {
        return mMembers;
    }

    @Override
    public void setVisited(boolean visited) {
        for (IGoString str : getMembers()) {
            str.setVisited(visited);
        }
    }

    /**
     * 添加棋串到该棋群中
     *
     * @param string
     */
    @Override
    public void addMember(IGoString string) {
        assert (string.isOwnedByPlayer1() == mIsOwnedByPlayer1) :
                "strings added to a group must have like ownership. String=" + string
                        + ". Group we are trying to add it to: " + this;
        if (getMembers().contains(string)) {
            assert (string.getGroup() == this) :
                    "The " + this + " already contains the string, but the " + string
                            + " says its owning group is " + string.getGroup();
            return;
        }
        // remove it from the old group
        IGoGroup oldGroup = string.getGroup();
        if (oldGroup != null && oldGroup != this) {
            oldGroup.remove(string);
        }
        string.setGroup(this);
        getMembers().add(string);
        broadcastChange();
    }

    /**
     * 删除该棋群中的指定棋串
     *
     * @param string
     */
    @Override
    public void remove(IGoString string) {
        if (string == null) {
            GameContext.log(2, "attempting to remove " + string + " string from group. " + this);
            return;
        }
        if (getMembers().isEmpty()) {
            GameContext.log(2, "attempting to remove " + string + " from already empty group.");
            return;
        }
        getMembers().remove(string);
        broadcastChange();
    }

    @Override
    public GoBoardPositionSet getLiberties(GoBoard board) {
        if (board == null) {
            return mCachedLiberties;
        }

        GoBoardPositionSet liberties = new GoBoardPositionSet();
        for (IGoString str : getMembers()) {
            liberties.addAll(str.getLiberties(board));
        }
        mCachedLiberties = liberties;
        return liberties;
    }

    @Override
    public int getNumLiberties(GoBoard board) {
        return getLiberties(board).size();
    }

    @Override
    public GoBoardPositionSet getStones() {
        GoBoardPositionSet stones = new GoBoardPositionSet();
        for (IGoString string : getMembers()) {
            stones.addAll(string.getMembers());
        }
        return stones;
    }

    @Override
    public int getNumStones() {
        return getStones().size();
    }

    /**
     * 更新棋群成员的健康评分为指定值
     *
     * @param health 取值[-1~1]
     */
    @Override
    public void updateTerritory(float health) {
        for (IGoString string : getMembers()) {
            if (string.isUnconditionallyAlive()) {
                string.updateTerritory(mIsOwnedByPlayer1 ? 1.0f : -1.0f);
            } else {
                string.updateTerritory(health);
            }
        }
    }

    @Override
    public boolean containsStone(GoBoardPosition stone) {
        for (IGoString string : getMembers()) {
            if (string.getMembers().contains(stone))
                return true;
        }
        return false;
    }

    /**
     * 获取能包围棋群中所有棋子的最小包围框
     *
     * @return
     */
    @Override
    public Box findBoundingBox() {
        int rMin = 10000; // something huge ( more than max rows)
        int rMax = 0;
        int cMin = 10000; // something huge ( more than max cols)
        int cMax = 0;

        // first determine a bounding rectangle for the group.
        for (IGoString string : this.getMembers()) {
            for (GoBoardPosition stone : string.getMembers()) {
                int row = stone.getRow();
                int col = stone.getCol();
                if (row < rMin) rMin = row;
                if (row > rMax) rMax = row;
                if (col < cMin) cMin = col;
                if (col > cMax) cMax = col;
            }
        }
        return (rMin > rMax) ? new Box(0, 0, 0, 0) : new Box(rMin, cMin, rMax, cMax);
    }

    private void broadcastChange() {
        for (GoGroupChangeListener listener : mChangeListeners) {
            listener.onGoGroupChanged();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GROUP {" + "\n");
        Iterator it = getMembers().iterator();
        // print the member strings
        if (it.hasNext()) {
            IGoString p = (IGoString) it.next();
            sb.append("    ").append(p.toString());
        }
        while (it.hasNext()) {
            IGoString p = (IGoString) it.next();
            sb.append(',').append("\n").append("    ").append(p.toString());
        }
        sb.append("\n").append('}');
        return sb.toString();
    }
}
