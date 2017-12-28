/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;

import java.util.Map;
import java.util.WeakHashMap;


/**
 * Maintains a map from groups to GroupAnalyzers.
 * If we request an analyzer for a group that is not in the map, we add it.
 *
 * @author Barry Becker
 */
public class GroupAnalyzerMap {

    private Map<IGoGroup, GroupAnalyzer> analyzerMap;

    /**
     * Constructor.
     * Use a weak HashMap because we do not want this to be the only thing
     * keeps it around when it is no longer on the board.
     */
    public GroupAnalyzerMap() {
        analyzerMap = new WeakHashMap<>();
        // new LRUCache<IGoGroup, GroupAnalyzer>(2000);
    }

    /**
     * Get an analyzer for a specific group.
     * If the analyzer is not there yet, it gets added.
     *
     * @param group the group to analyze.
     * @return the analyzer for the specified group
     */
    public GroupAnalyzer getAnalyzer(IGoGroup group) {
        GroupAnalyzer cachedAnalyzer = analyzerMap.get(group);
        if (cachedAnalyzer != null) {
            return cachedAnalyzer;
        }
        GroupAnalyzer analyzer = new GroupAnalyzer(group, this);
        analyzerMap.put(group, analyzer);

        return analyzer;
    }

    public void clear() {
        //for (GroupAnalyzer ga : analyzerMap.values()) {
        //     ga.invalidate();
        //}
        analyzerMap.clear();
    }

    public String toString() {
        return analyzerMap.toString();
    }
}
