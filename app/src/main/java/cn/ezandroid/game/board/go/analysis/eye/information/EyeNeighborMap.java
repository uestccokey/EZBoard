/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

import java.util.HashMap;
import java.util.Map;

import cn.ezandroid.game.board.go.elements.eye.IGoEye;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;

/**
 * 眼位中所有点位的邻接点位图
 *
 * @author Barry Becker
 */
public class EyeNeighborMap {

    private IGoEye mEye;
    private Map<GoBoardPosition, GoBoardPositionList> mNbrMap;

    public EyeNeighborMap(IGoEye eye) {
        mEye = eye;
        mNbrMap = createMap();
    }

    /**
     * 获取眼位中指定点的邻接点位列表
     *
     * @return
     */
    GoBoardPositionList getEyeNeighbors(GoBoardPosition eyeSpace) {
        return mNbrMap.get(eyeSpace);
    }

    /**
     * 获取眼位中指定点的邻接点位的数量
     *
     * @return
     */
    public int getNumEyeNeighbors(GoBoardPosition eyeSpace) {
        return getEyeNeighbors(eyeSpace).size();
    }

    public GoBoardPositionSet keySet() {
        return new GoBoardPositionSet(mNbrMap.keySet());
    }

    /**
     * 判断眼位中指定点的编码值是否在指定数组中
     *
     * @return
     */
    public boolean isSpecialPoint(GoBoardPosition space, float[] specialPoints) {
        float index = getEyeNeighborIndex(space);
        for (float specialPtIndex : specialPoints) {
            if (index == specialPtIndex) {
                return true;
            }
        }
        return false;
    }

    /**
     * 眼位中指定点的编码值
     *
     * @return Number of eye neighbors + (the sum of all the neighbors neighbors)/100.
     */
    private float getEyeNeighborIndex(GoBoardPosition eyeSpace) {
        float nbrNbrSum = 0;
        for (GoBoardPosition pos : getEyeNeighbors(eyeSpace)) {
            nbrNbrSum += getNumEyeNeighbors(pos);
        }
        return getEyeNeighbors(eyeSpace).size() + nbrNbrSum / 100.0f;
    }

    private Map<GoBoardPosition, GoBoardPositionList> createMap() {
        Map<GoBoardPosition, GoBoardPositionList> nbrMap = new HashMap<>();
        // we should probably be able to assume that the eye spaces_ are unvisited, but apparently not. assert instead?
        mEye.setVisited(false);

        GoBoardPositionList queue = new GoBoardPositionList();
        GoBoardPosition firstPos = mEye.getMembers().iterator().next();
        firstPos.setVisited(true);
        queue.add(firstPos);

        int count = processSearchQueue(queue, nbrMap);

        if (count != mEye.getMembers().size()) {
            throw new IllegalArgumentException("The eye string must not have been nobi connected because " +
                    "not all memebers were searched. " + mEye);
        }
        mEye.setVisited(false);
        return nbrMap;
    }

    /**
     * 广度优先搜索
     *
     * @return
     */
    private int processSearchQueue(GoBoardPositionList queue, Map<GoBoardPosition, GoBoardPositionList> nbrMap) {
        int count = 0;
        while (!queue.isEmpty()) {
            GoBoardPosition current = queue.remove(0);
            GoBoardPositionList nbrs = getEyeNobiNeighbors(current);
            nbrMap.put(current, nbrs);
            count++;
            for (GoBoardPosition space : nbrs) {
                if (!space.isVisited()) {
                    space.setVisited(true);
                    queue.add(space);
                }
            }
        }
        return count;
    }

    /**
     * 获取眼位中指定点邻接点位的列表（要么是空点位，要么是敌方死子）
     *
     * @param space
     * @return
     */
    private GoBoardPositionList getEyeNobiNeighbors(GoBoardPosition space) {
        GoBoardPositionList nbrs = new GoBoardPositionList();
        for (GoBoardPosition eyeSpace : mEye.getMembers()) {
            if (space.isNeighbor(eyeSpace))
                nbrs.add(eyeSpace);
        }
        return nbrs;
    }

    public String toString() {
        return mNbrMap.toString();
    }
}