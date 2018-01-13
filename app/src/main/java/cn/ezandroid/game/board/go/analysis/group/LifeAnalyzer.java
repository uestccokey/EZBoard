/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.eye.GoEyeList;
import cn.ezandroid.game.board.go.elements.eye.GoEyeSet;
import cn.ezandroid.game.board.go.elements.eye.IGoEye;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoStringSet;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 使用Benson's算法来确定一个棋群是否无条件活棋
 * <p>
 * http://senseis.xmp.net/?BensonSAlgorithm
 *
 * @author Barry Becker
 */
public class LifeAnalyzer {

    private IGoGroup mGroup;
    private GoBoard mBoard;

    // 活眼邻接的棋串列表映射图
    private Map<IGoEye, List<IGoString>> mEyeStringNbrMap;

    // 棋串邻接的关键眼位列表映射图
    private Map<IGoString, GoEyeList> mStringEyeNbrMap;

    private NeighborAnalyzer mNeighborAnalyzer;
    private GroupAnalyzerMap mAnalyzerMap;

    protected LifeAnalyzer() {}

    public LifeAnalyzer(IGoGroup group, GoBoard board, GroupAnalyzerMap analyzerMap) {
        mGroup = group;
        mBoard = board;
        mAnalyzerMap = analyzerMap;
        mNeighborAnalyzer = new NeighborAnalyzer(board);
    }

    /**
     * 使用Benson's算法来确定一个棋群是否无条件活棋
     *
     * @return
     */
    public boolean isUnconditionallyAlive() {
        initMaps();

        GoEyeSet eyes = mAnalyzerMap.getAnalyzer(mGroup).getEyes(mBoard);
        findNeighborStringSetsForEyes(eyes);
        createVitalEyeSets(eyes);

        return determineUnconditionalLife();
    }

    private void initMaps() {
        mEyeStringNbrMap = new HashMap<>();
        mStringEyeNbrMap = new HashMap<>();
    }

    private void findNeighborStringSetsForEyes(GoEyeSet eyes) {
        for (IGoEye eye : eyes) {
            List<IGoString> stringNbrs = findNeighborStringsForEye(eye);
            mEyeStringNbrMap.put(eye, stringNbrs);
        }
    }

    private List<IGoString> findNeighborStringsForEye(IGoEye eye) {
        List<IGoString> nbrStrings = new LinkedList<>();
        for (GoBoardPosition pos : eye.getMembers()) {
            if (pos.isUnoccupied()) {
                findNeighborStringsForEyeSpace(eye, pos, nbrStrings);
            }
        }
        return nbrStrings;
    }

    /**
     * 获取一个眼位中指定空白点的邻接棋串列表
     *
     * @param eye
     * @param pos
     * @param nbrStrings
     */
    private void findNeighborStringsForEyeSpace(IGoEye eye, GoBoardPosition pos, List<IGoString> nbrStrings) {
        GoBoardPositionSet nbrs =
                mNeighborAnalyzer.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
        for (GoBoardPosition nbr : nbrs) {
            if (nbr.getString().getGroup() != mGroup) {
                // this eye is not unconditionally alive (UA).
                nbrStrings.clear();
                return;
            } else {
                if (!nbrStrings.contains(nbr.getString())) {
                    // assume its alive at first.
                    nbr.getString().setUnconditionallyAlive(true);
                    nbrStrings.add(nbr.getString());
                }
            }
        }
    }

    private void createVitalEyeSets(GoEyeSet eyes) {
        for (IGoEye eye : eyes) {
            updateVitalEyesForStringNeighbors(eye);
        }
//        GameContext.log(3, "num strings with vital eye nbrs = " + mStringEyeNbrMap.size());
    }

    /**
     * 更新指定眼位的邻接棋串的关键眼位点
     *
     * @param eye
     */
    private void updateVitalEyesForStringNeighbors(IGoEye eye) {
        for (IGoString str : mEyeStringNbrMap.get(eye)) {
            // only add the eye if every unoccupied position in the eye is adjacent to the string
            GoEyeList vitalEyes;
            if (mStringEyeNbrMap.containsKey(str)) {
                vitalEyes = mStringEyeNbrMap.get(str);
            } else {
                vitalEyes = new GoEyeList();
                mStringEyeNbrMap.put(str, vitalEyes);
            }

            if (allUnoccupiedAdjacentToString(eye, str)) {
                eye.setUnconditionallyAlive(true);
                vitalEyes.add(eye);
            }
        }
    }

    /**
     * 判断是否指定眼位的所有空点与指定棋串相邻
     *
     * @param eye
     * @param string
     * @return
     */
    private boolean allUnoccupiedAdjacentToString(IGoEye eye, IGoString string) {
        for (GoBoardPosition pos : eye.getMembers()) {
            if (pos.isUnoccupied()) {
                GoBoardPositionSet nbrs =
                        mNeighborAnalyzer.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
                // verify that at least one of the nbrs is in this string
                boolean thereIsaNbr = false;
                for (GoBoardPosition nbr : nbrs) {
                    if (string.getMembers().contains(nbr)) {
                        thereIsaNbr = true;
                        break;
                    }
                }
                if (!thereIsaNbr) {
                    //GameContext.log(2, "pos:"+pos+" was found to not be adjacent to the bordering string : "+this);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断棋群是否无条件活棋
     * <p>
     * 通过棋群中是否有无条件活棋的棋串来判断
     *
     * @return
     */
    private boolean determineUnconditionalLife() {
        GoStringSet livingStrings = findPassAliveStrings();
        return !livingStrings.isEmpty();
    }

    /**
     * 获取处于PassAlive状态的无条件活棋棋串集合
     *
     * @return
     */
    private GoStringSet findPassAliveStrings() {
        GoStringSet candidateStrings = new GoStringSet(mGroup.getMembers());
        boolean done;
        do {
            initializeEyeLife();
            Iterator<IGoString> it = candidateStrings.iterator();

            done = true;
            while (it.hasNext()) {
                IGoString str = it.next();
                int numLivingAdjacentEyes = findNumLivingAdjacentEyes(str);
                if (numLivingAdjacentEyes < 2) {
                    str.setUnconditionallyAlive(false);
                    it.remove();
                    done = false; // something changed
                }
            }
        } while (!(done || candidateStrings.isEmpty()));
        return candidateStrings;
    }

    /**
     * 对棋群中的眼位，通过验证它的邻接棋串是否是无条件活棋来判定它是否是无条件活棋眼位
     */
    private void initializeEyeLife() {
        for (IGoEye eye : mAnalyzerMap.getAnalyzer(mGroup).getEyes(mBoard)) {
            eye.setUnconditionallyAlive(true);
            for (IGoString nbrStr : mEyeStringNbrMap.get(eye)) {
                if (!(nbrStr.isUnconditionallyAlive())) {
                    eye.setUnconditionallyAlive(false);
                }
            }
        }
    }

    /**
     * 获取指定棋串无条件活棋的邻接眼位数量
     *
     * @param str
     * @return
     */
    private int findNumLivingAdjacentEyes(IGoString str) {
        int numLivingAdjacentEyes = 0;
        GoEyeList vitalEyeNbrs = mStringEyeNbrMap.get(str);
        if (vitalEyeNbrs != null) {
            for (IGoEye eye : vitalEyeNbrs) {
                if (eye.isUnconditionallyAlive()) {
                    numLivingAdjacentEyes++;
                }
            }
        }
        return numLivingAdjacentEyes;
    }
}