/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common;

import java.util.ArrayList;

/**
 * 落子历史列表
 *
 * @author Barry Becker
 */
public class MoveList<M extends Move> extends ArrayList<M> {

    private final Object mLock = new Object();

    public MoveList() {}

    public MoveList(MoveList<M> list) {
        super(list);
    }

    public MoveList<M> copy() {
        MoveList<M> copiedList = new MoveList<>();
        synchronized (mLock) {
            for (M m : this) {
                copiedList.add((M) m.copy());
            }
        }
        return copiedList;
    }

    @Override
    public boolean add(M m) {
        synchronized (mLock) {
            return super.add(m);
        }
    }

    @Override
    public void add(int index, M move) {
        synchronized (mLock) {
            super.add(index, move);
        }
    }

    public M getFirstMove() {
        return get(0);
    }

    public synchronized M getLastMove() {
        if (isEmpty()) {
            return null;
        }
        return get(size() - 1);
    }

    public M removeLast() {
        synchronized (mLock) {
            return remove(this.size() - 1);
        }
    }

    public int getNumMoves() {
        return size();
    }

    @Override
    public MoveList<M> subList(int first, int last) {
        MoveList<M> subList = new MoveList<>();
        synchronized (mLock) {
            subList.addAll(super.subList(first, last));
        }
        return subList;
    }
}