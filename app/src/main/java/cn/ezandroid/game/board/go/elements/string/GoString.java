/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.string;

import java.util.Iterator;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.StringLibertyAnalyzer;
import cn.ezandroid.game.board.go.elements.GoSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.position.GoStone;

/**
 * 棋串模型
 * <p>
 * 棋串由邻接的同色棋子组成
 *
 * @author Barry Becker
 */
public class GoString extends GoSet implements IGoString {

    // 棋串中的棋子集合
    private GoBoardPositionSet mMembers;

    // 棋串所属的棋群
    protected IGoGroup mGroup;

    // 是否无条件存活
    private boolean mIsUnconditionallyAlive;

    // 跟踪气的数量，防止每次都进行计算，以提高性能
    private StringLibertyAnalyzer mLibertyAnalyzer;

    public GoString(GoBoardPosition stone, GoBoard board) {
        assert (stone.isOccupied());
        mIsOwnedByPlayer1 = stone.getPiece().isOwnedByPlayer1();
        getMembers().add(stone);
        stone.setString(this);
        mGroup = null;
        mLibertyAnalyzer = new StringLibertyAnalyzer(board, this);
    }

    public GoString(GoBoardPositionList stones, GoBoard board) {
        assert (stones != null && stones.size() > 0) : "Tried to create list from empty list";
        GoStone stone = (GoStone) stones.getFirst().getPiece();
        // GoEye constructor calls this method. For eyes the stone is null.
        if (stone != null)
            mIsOwnedByPlayer1 = stone.isOwnedByPlayer1();
        for (GoBoardPosition pos : stones) {
            addMemberInternal(pos, board);
        }
        mLibertyAnalyzer = new StringLibertyAnalyzer(board, this);
    }

    @Override
    public GoBoardPositionSet getMembers() {
        return mMembers;
    }

    @Override
    public boolean contains(GoBoardPosition pos) {
        return mMembers.contains(pos);
    }

    @Override
    protected void initializeMembers() {
        mMembers = new GoBoardPositionSet();
    }

    @Override
    public final void setGroup(IGoGroup group) {
        mGroup = group;
    }

    @Override
    public IGoGroup getGroup() {
        return mGroup;
    }

    public void addMember(GoBoardPosition stone, GoBoard board) {
        addMemberInternal(stone, board);
        mLibertyAnalyzer.invalidate();
    }

    protected void addMemberInternal(GoBoardPosition stone, GoBoard board) {
        assert (stone.isOccupied()) : "trying to add empty space to string. stone=" + stone;
        assert (stone.getPiece().isOwnedByPlayer1() == this.isOwnedByPlayer1()) :
                "stones added to a string must have like ownership";
        if (getMembers().contains(stone)) {
            // this case can happen sometimes.
            // For example if the new stone completes a loop and self-joins the string to itself
            assert (stone.getString() == null) || (this == stone.getString()) :
                    "bad stone " + stone + " or bad owning string " + stone.getString();
        }
        // if the stone is already owned by another string, we need to verify that that other string has given it up.
        if (stone.getString() != null) {
            stone.getString().remove(stone, board);
        }

        stone.setString(this);
        getMembers().add(stone);
    }

    /**
     * 将该棋串与另外一个棋串合并
     */
    public final void merge(IGoString string, GoBoard board) {
        if (this == string) {
            // its a self join
            return;
        }

        GoBoardPositionSet stringMembers = new GoBoardPositionSet();
        stringMembers.addAll(string.getMembers());
        // must remove these after iterating otherwise we get a ConcurrentModificationException
        string.getGroup().remove(string);
        string.getMembers().clear();

        Iterator it = stringMembers.iterator();
        GoBoardPosition stone;
        while (it.hasNext()) {
            stone = (GoBoardPosition) it.next();
            IGoString myString = stone.getString();
            if (myString != null && myString != string) {
                myString.remove(stone, board);
            }
            stone.setString(null);
            addMemberInternal(stone, board);
        }
        stringMembers.clear();
        mLibertyAnalyzer.invalidate();
    }

    /**
     * 从该棋串中删除一个棋子
     * <p>
     * 删除棋子后，棋串断裂怎么处理？
     * 调用者应该处理这种情况，因为我们不能再这里创建一个新的棋串
     */
    @Override
    public final void remove(GoBoardPosition stone, GoBoard board) {
        removeInternal(stone);
        mLibertyAnalyzer.invalidate();
    }

    void removeInternal(GoBoardPosition stone) {
        boolean removed = getMembers().remove(stone);
        assert (removed) : "failed to remove " + stone + " from" + this;
        stone.setString(null);
        if (getMembers().isEmpty()) {
            mGroup.remove(this);
        }
    }

    @Override
    public final GoBoardPositionSet getLiberties(GoBoard board) {
        return mLibertyAnalyzer.getLiberties();
    }

    @Override
    public int getNumLiberties(GoBoard board) {
        return getLiberties(board).size();
    }

    /**
     * 更新棋串的气点
     *
     * @param libertyPos
     */
    public void changedLiberty(GoBoardPosition libertyPos) {
        mLibertyAnalyzer.invalidate();
    }

    /**
     * 更新棋串成员的健康评分为指定值
     *
     * @param health 取值[-1~1]
     */
    @Override
    public final void updateTerritory(float health) {
        for (GoBoardPosition pos : getMembers()) {
            GoStone stone = (GoStone) pos.getPiece();
            stone.setHealth(health);
        }
    }

    @Override
    public final void setVisited(boolean visited) {
        for (GoBoardPosition stone : getMembers()) {
            stone.setVisited(visited);
        }
    }

    protected String getPrintPrefix() {
        return " STRING(";
    }

    @Override
    public boolean isUnconditionallyAlive() {
        return mIsUnconditionallyAlive;
    }

    @Override
    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
        this.mIsUnconditionallyAlive = unconditionallyAlive;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getPrintPrefix());
        sb.append(" UA=").append(isUnconditionallyAlive()).append(" ");
        Iterator it = getMembers().iterator();
        if (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            sb.append(p.toString());
        }
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition) it.next();
            sb.append(", ");
            sb.append(p.toString());
        }
        sb.append(')');
        return sb.toString();
    }
}