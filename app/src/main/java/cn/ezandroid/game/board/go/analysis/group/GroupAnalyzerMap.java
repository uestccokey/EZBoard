/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import java.util.Map;
import java.util.WeakHashMap;

import cn.ezandroid.game.board.go.elements.group.IGoGroup;

/**
 * 棋群与棋群分析器的映射图
 *
 * @author Barry Becker
 */
public class GroupAnalyzerMap {

    private Map<IGoGroup, GroupAnalyzer> mAnalyzerMap;

    public GroupAnalyzerMap() {
        mAnalyzerMap = new WeakHashMap<>();
    }

    /**
     * 获取指定棋群的棋群分析器
     * <p>
     * 如果不存在，则创建一个新的棋群分析器，并添加到该映射图中
     *
     * @param group
     * @return
     */
    public GroupAnalyzer getAnalyzer(IGoGroup group) {
        GroupAnalyzer cachedAnalyzer = mAnalyzerMap.get(group);
        if (cachedAnalyzer != null) {
            return cachedAnalyzer;
        }
        GroupAnalyzer analyzer = new GroupAnalyzer(group, this);
        mAnalyzerMap.put(group, analyzer);
        return analyzer;
    }

    public void clear() {
        mAnalyzerMap.clear();
    }

    public String toString() {
        return mAnalyzerMap.toString();
    }
}
