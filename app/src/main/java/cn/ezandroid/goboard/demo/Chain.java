package cn.ezandroid.goboard.demo;

import java.util.HashSet;
import java.util.Set;

import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;

/**
 * 棋串模型
 *
 * @author like
 */
public class Chain implements Cloneable {

    private StoneColor mStoneColor; // 棋串颜色
    private Set<Stone> mStones; // 棋串棋子集合
    private Set<Intersection> mLiberties; // 棋串气点集合

    public Chain(StoneColor c) {
        mStoneColor = c;
        mStones = new HashSet<>();
        mLiberties = new HashSet<>();
    }

    @Override
    public Chain clone() throws CloneNotSupportedException {
        Chain chain = (Chain) super.clone();
        chain.mStoneColor = mStoneColor;
        chain.mStones = new HashSet<>();
        for (Stone stone : mStones) {
            chain.mStones.add(stone.clone());
        }
        chain.mLiberties = new HashSet<>();
        for (Intersection intersection : mLiberties) {
            chain.mLiberties.add(intersection.clone());
        }
        return chain;
    }

    /**
     * 获取棋串颜色
     *
     * @return
     */
    public StoneColor getStoneColor() {
        return mStoneColor;
    }

    public Set<Stone> getStones() {
        return mStones;
    }

    public Set<Intersection> getLiberties() {
        return mLiberties;
    }

    /**
     * 获取棋串棋子总数
     *
     * @return
     */
    public int size() {
        return mStones.size();
    }

    /**
     * 添加同色棋子到串中，更新气点集合
     *
     * @param stone
     * @param newLiberties
     */
    public void add(Stone stone, Set<Intersection> newLiberties) {
        if (mStoneColor != stone.color) {
            throw new IllegalArgumentException("Can not add stone of color " + stone.color +
                    " to chain of color " + mStoneColor);
        }
        mLiberties.remove(stone.intersection);
        mLiberties.addAll(newLiberties);
        mStones.add(stone);
    }

    /**
     * 合并同色棋串，更新气点集合
     */
    public void merge(Chain chain) {
        if (mStoneColor != chain.mStoneColor) {
            throw new IllegalArgumentException("Can not merge chain of color " + chain
                    .mStoneColor +
                    " to chain of color " + mStoneColor);
        }
        mStones.addAll(chain.mStones);
        mLiberties.addAll(chain.mLiberties);
    }

    /**
     * 棋串气增加
     *
     * @param intersection
     */
    public void addLiberty(Intersection intersection) {
        mLiberties.add(intersection);
    }

    /**
     * 棋串气减少
     *
     * @param intersection
     */
    public void removeLiberty(Intersection intersection) {
        mLiberties.remove(intersection);
    }

    /**
     * 检查该点是否为棋串最后一口气
     *
     * @param intersection
     * @return
     */
    public boolean isLastLiberty(Intersection intersection) {
        return mLiberties.size() == 1 && mLiberties.contains(intersection);
    }
}
