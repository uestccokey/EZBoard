package cn.ezandroid.goboard;

import android.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 落子管理器
 *
 * @author like
 */
public class MoveManager implements Cloneable {

    /**
     * 棋盘上的所有棋串
     */
    private Set<Chain> mChains;

    /**
     * 坐标与棋串的映射图
     */
    private Map<Intersection, Chain> mFilled;

    /**
     * 落子历史
     */
    private History<Move> mHistory;

    /**
     * 棋盘大小
     */
    private int mBoardSize;

    /**
     * 白棋提子数
     */
    private int mWhitesCaptures;

    /**
     * 黑棋提子数
     */
    private int mBlacksCaptures;

    MoveManager(int size) {
        mChains = new HashSet<>();
        mFilled = new HashMap<>();
        mHistory = new History<>();
        mBoardSize = size;
        mWhitesCaptures = 0;
        mBlacksCaptures = 0;
    }

    @Override
    protected MoveManager clone() throws CloneNotSupportedException {
        MoveManager moveManager = (MoveManager) super.clone();
        moveManager.mChains = new HashSet<>();
        moveManager.mFilled = new HashMap<>();
        for (Chain chain : mChains) {
            Chain clone = chain.clone();
            moveManager.mChains.add(clone);
            moveManager.updateFilled(clone, clone.getStones());
        }
        moveManager.mHistory = new History<>();
        for (Move move : mHistory) {
            moveManager.mHistory.add(move.clone());
        }
        return moveManager;
    }

    public Set<Chain> getChains() {
        return mChains;
    }

    public Chain getChain(Intersection intersection) {
        return mFilled.get(intersection);
    }

    public History<Move> getHistory() {
        return mHistory;
    }

    /**
     * 检查该点是否已有棋子
     *
     * @param intersection
     * @return
     */
    public boolean taken(Intersection intersection) {
        return mFilled.containsKey(intersection);
    }

    /**
     * 是否能撤销
     */
    public boolean hasPast() {
        return mHistory.hasPast();
    }

    /**
     * 是否能重做
     */
    public boolean hasFuture() {
        return mHistory.hasFuture();
    }

    /**
     * 撤销
     */
    public Pair<Move, Chain> undo() {
        Move move = mHistory.stepBack();
        if (move == null) {
            return null;
        }

        Stone stone = move.getStone();
        Set<Chain> captured = move.getCaptured();
        Chain chain = mFilled.get(stone.intersection);

        // 邻接敌串气数增加
        addToOpposingLiberties(stone);

        // 删除或重建当前链，因为它可能已经分裂
        if (chain == null) {
            throw new IllegalStateException("Popped stone keyed to null chain");
        }
        mChains.remove(chain);
        Set<Stone> stones = chain.getStones();
        for (Stone s : stones) {
            mFilled.remove(s.intersection);
        }
        for (Stone s : stones) {
            if (s != stone) {
                incorporateIntoChains(s);
            }
        }

        // 还原提走的棋子
        for (Chain c : captured) {
            for (Stone s : c.getStones()) {
                incorporateIntoChains(s);
            }
        }

        // 更新提子数
        updateCaptureCount(stone.color, captured.size(), false);

        return new Pair<>(move, chain);
    }

    /**
     * 重做
     *
     * @return
     */
    public Move redo() {
        Move move = mHistory.stepForward();
        if (move == null) {
            return null;
        }

        addStoneWithHistory(move.getStone(), move.getCaptured(), false);
        return move;
    }

    /**
     * 添加棋子到棋盘
     *
     * @param stone
     * @param captured
     * @return 无效落子（如自杀）返回false
     */
    public boolean addStone(Stone stone, Set<Chain> captured) {
        return addStoneWithHistory(stone, captured, true);
    }

    private boolean addStoneWithHistory(Stone stone, Set<Chain> captured, boolean modifyHistory) {
        capture(stone, captured);
        if (!isLikeSuicide(stone)) {
            incorporateIntoChains(stone);
            if (modifyHistory) {
                mHistory.add(new Move(stone, captured));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回与指定坐标相邻的指定颜色的棋串集合
     *
     * @param intersection
     * @param color
     * @return
     */
    private Set<Chain> getNeighborChains(Intersection intersection, StoneColor color) {
        return new NeighborChecker<Chain>().getMatchingNeighbors((found, intersection1, criterion) -> {
            if (taken(intersection1) && mFilled.get(intersection1).getStoneColor() == criterion) {
                found.add(mFilled.get(intersection1));
            }
        }, intersection, color);
    }

    /**
     * 当落子是其周围敌串的最后气点时，移除这些敌串
     *
     * @param stone
     * @param captured
     */
    private void capture(Stone stone, Set<Chain> captured) {
        Set<Chain> opposingChains = getNeighborChains(stone.intersection, stone.color.getOther());
        for (Chain chain : opposingChains) {
            if (chain.isLastLiberty(stone.intersection)) {
                captured.add(chain);
                captureChain(chain);
            }
        }
    }

    /**
     * 移除指定棋串
     *
     * @param chain
     */
    private void captureChain(Chain chain) {
        Set<Stone> stones = chain.getStones();
        updateCaptureCount(chain.getStoneColor(), chain.size(), true);
        for (Stone stone : stones) {
            mFilled.remove(stone.intersection);
            addToOpposingLiberties(stone);
        }
        mChains.remove(chain);
    }

    /**
     * 更新提子数
     *
     * @param color
     * @param count
     * @param increment
     */
    private void updateCaptureCount(StoneColor color, int count, boolean increment) {
        if (color == StoneColor.BLACK) {
            mWhitesCaptures += increment ? count : count * -1;
        } else {
            mBlacksCaptures += increment ? count : count * -1;
        }
    }

    /**
     * 是否自杀着
     *
     * @param stone
     * @return
     */
    public boolean isSuicide(Stone stone) {
        if (!isLikeSuicide(stone)) {
            return false;
        }
        // 检查是否敌方棋串的最后一口气，是则返回false
        for (Chain chain : getNeighborChains(stone.intersection, stone.color.getOther())) {
            if (chain.isLastLiberty(stone.intersection)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否像自杀着
     *
     * @param stone
     * @return
     */
    private boolean isLikeSuicide(Stone stone) {
        // 检查是否己方棋串的最后一口气，不是则返回false
        for (Chain chain : getNeighborChains(stone.intersection, stone.color)) {
            if (!chain.isLastLiberty(stone.intersection)) {
                return false;
            }
        }
        return getNeighborLiberties(stone.intersection).size() == 0;
    }

    /**
     * 返回与指定坐标的相邻的气点集合
     *
     * @param intersection
     * @return
     */
    private Set<Intersection> getNeighborLiberties(Intersection intersection) {
        return new HashSet<>(new NeighborChecker<Intersection>().getMatchingNeighbors((neighbors, intersection1, dummy) -> {
            if (!taken(intersection1)) {
                neighbors.add(intersection1);
            }
        }, intersection, null));
    }

    /**
     * 归并棋串
     * <p>
     * 1. 当落子周围有一个同色棋串时，添加到该棋串中
     * 2. 当落子周围有多个同色棋串时，合并同色棋串，并添加到该棋串中
     * 3. 当落子周围没有同色棋串时，创建一个仅包含该落子的棋串
     *
     * @param stone
     */
    private void incorporateIntoChains(Stone stone) {
        Set<Chain> friends = getNeighborChains(stone.intersection, stone.color);
        Chain merged;
        Iterator<Chain> iterator = friends.iterator();
        if (!iterator.hasNext()) {
            merged = new Chain(stone.color);
            mChains.add(merged);
        } else {
            Chain friend;
            merged = iterator.next();
            while (iterator.hasNext()) {
                friend = iterator.next();
                merged.merge(friend);
                updateFilled(merged, friend.getStones());
                mChains.remove(friend);
            }
        }
        addToChain(merged, stone);
    }

    /**
     * 添加棋子到棋串
     *
     * @param chain
     * @param stone
     */
    private void addToChain(Chain chain, Stone stone) {
        chain.add(stone, getNeighborLiberties(stone.intersection));
        // 邻接敌串气数减少
        removeFromOpposingLiberties(stone);
        mFilled.put(stone.intersection, chain);
    }

    /**
     * 更新坐标与棋串映射图
     *
     * @param chain
     * @param stones
     */
    private void updateFilled(Chain chain, Set<Stone> stones) {
        for (Stone stone : stones) {
            mFilled.put(stone.intersection, chain);
        }
    }

    /**
     * 邻接敌串气数增加
     *
     * @param stone
     */
    private void addToOpposingLiberties(Stone stone) {
        for (Chain chain : getNeighborChains(stone.intersection, stone.color.getOther())) {
            chain.addLiberty(stone.intersection);
        }
    }

    /**
     * 邻接敌串气数减少
     *
     * @param stone
     */
    private void removeFromOpposingLiberties(Stone stone) {
        for (Chain chain : getNeighborChains(stone.intersection, stone.color.getOther())) {
            chain.removeLiberty(stone.intersection);
        }
    }

    /**
     * 重置棋盘
     */
    public void reset() {
        mChains.clear();
        mHistory.clear();
        mFilled.clear();
        mWhitesCaptures = 0;
        mBlacksCaptures = 0;
    }

    /**
     * 基于某些规则的相邻坐标检查
     */
    private class NeighborChecker<T> {

        /**
         * 返回符合标准的相邻坐标集合
         *
         * @param checker
         * @param intersection
         * @param criterion
         * @return
         */
        private Set<T> getMatchingNeighbors(CheckIntersection<T> checker, Intersection intersection,
                                            Object criterion) {
            Set<T> neighbors = new HashSet<>();
            if (intersection.x - 1 > -1) {
                checker.check(neighbors, new Intersection(intersection.x - 1, intersection.y), criterion);
            }
            if (intersection.x + 1 < mBoardSize) {
                checker.check(neighbors, new Intersection(intersection.x + 1, intersection.y), criterion);
            }
            if (intersection.y - 1 > -1) {
                checker.check(neighbors, new Intersection(intersection.x, intersection.y - 1), criterion);
            }
            if (intersection.y + 1 < mBoardSize) {
                checker.check(neighbors, new Intersection(intersection.x, intersection.y + 1), criterion);
            }
            return neighbors;
        }
    }

    private interface CheckIntersection<T> {

        void check(Set<T> found, Intersection intersection, Object criterion);
    }
}

