/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements;

import java.util.Set;

/**
 * 围棋构件集抽象类
 *
 * @author Barry Becker
 */
public abstract class GoSet implements IGoSet {

    protected boolean mIsOwnedByPlayer1;

    protected GoSet() {
        initializeMembers();
    }

    @Override
    public final boolean isOwnedByPlayer1() {
        return mIsOwnedByPlayer1;
    }

    @Override
    public final int size() {
        return getMembers().size();
    }

    @Override
    public abstract Set<? extends IGoMember> getMembers();

    /**
     * 初始化围棋构件集
     */
    protected abstract void initializeMembers();
}
