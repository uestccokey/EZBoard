/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board.analysis.group;

import com.ezandroid.board.common.GameContext;
import com.ezandroid.board.go.board.GoBoard;
import com.ezandroid.board.go.board.analysis.neighbor.NeighborAnalyzer;
import com.ezandroid.board.go.board.analysis.neighbor.NeighborType;
import com.ezandroid.board.go.board.elements.eye.GoEyeList;
import com.ezandroid.board.go.board.elements.eye.GoEyeSet;
import com.ezandroid.board.go.board.elements.eye.IGoEye;
import com.ezandroid.board.go.board.elements.group.IGoGroup;
import com.ezandroid.board.go.board.elements.position.GoBoardPosition;
import com.ezandroid.board.go.board.elements.position.GoBoardPositionSet;
import com.ezandroid.board.go.board.elements.string.GoStringSet;
import com.ezandroid.board.go.board.elements.string.IGoString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Determine if group is pass-alive using
 * Benson's algorithm for unconditional life.
 * see http://senseis.xmp.net/?BensonSAlgorithm
 *
 * @author Barry Becker
 */
public class LifeAnalyzer {

    private IGoGroup group_;
    private GoBoard board_;

    /** Keep track of living strings neighboring eyes. */
    private Map<IGoEye, List<IGoString>> eyeStringNbrMap;

    /** Keep track of vital eyes neighboring living string. */
    private Map<IGoString, GoEyeList> stringEyeNbrMap;

    private NeighborAnalyzer nbrAnalyzer_;
    private GroupAnalyzerMap analyzerMap_;

    /** called only by derived classes */
    protected LifeAnalyzer() {}

    /**
     * Constructor.
     *
     * @param group the group to analyze for unconditional life.
     * @param board board on which the group exists.
     */
    public LifeAnalyzer(IGoGroup group, GoBoard board, GroupAnalyzerMap analyzerMap) {
        group_ = group;
        board_ = board;
        analyzerMap_ = analyzerMap;
        nbrAnalyzer_ = new NeighborAnalyzer(board);
    }

    /**
     * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
     * is unconditionally alive.
     *
     * @return true if unconditionally alive
     */
    public boolean isUnconditionallyAlive() {
        initMaps();

        GoEyeSet eyes = analyzerMap_.getAnalyzer(group_).getEyes(board_);
        findNeighborStringSetsForEyes(eyes);
        createVitalEyeSets(eyes);

        return determineUnconditionalLife();
    }

    private void initMaps() {
        eyeStringNbrMap = new HashMap<>();
        stringEyeNbrMap = new HashMap<>();
    }

    /**
     * first find the neighbor string sets for each true eye in the group.
     */
    private void findNeighborStringSetsForEyes(GoEyeSet eyes) {
        for (IGoEye eye : eyes) {
            List<IGoString> stringNbrs = findNeighborStringsForEye(eye);
            eyeStringNbrMap.put(eye, stringNbrs);
        }
    }

    /**
     * Find the neighbor string sets for a specific eye in the group.
     *
     * @param eye eye to find neighboring strings of.
     * @return living neighbor strings. May be empty, but never null.
     */
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
     * Find the neighbor string sets for a specific empty point within an eye.
     *
     * @param eye        eye the eye space string we are currently analyzing.
     * @param pos        empty position within eye.
     * @param nbrStrings the list to add neighboring still living strings to.
     */
    private void findNeighborStringsForEyeSpace(IGoEye eye, GoBoardPosition pos, List<IGoString> nbrStrings) {
        GoBoardPositionSet nbrs =
                nbrAnalyzer_.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
        for (GoBoardPosition nbr : nbrs) {

            if (nbr.getString().getGroup() != group_) {
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

    /**
     * Create the neighbor eye sets for each qualified string.
     */
    private void createVitalEyeSets(GoEyeSet eyes) {
        for (IGoEye eye : eyes) {
            updateVitalEyesForStringNeighbors(eye);
        }
        GameContext.log(3, "num strings with vital eye nbrs = " + stringEyeNbrMap.size());
    }

    /**
     * @param eye update the string neighbors of this eye
     */
    private void updateVitalEyesForStringNeighbors(IGoEye eye) {
        for (IGoString str : eyeStringNbrMap.get(eye)) {
            // only add the eye if every unoccupied position in the eye is adjacent to the string
            GoEyeList vitalEyes;
            if (stringEyeNbrMap.containsKey(str)) {
                vitalEyes = stringEyeNbrMap.get(str);
            } else {
                vitalEyes = new GoEyeList();
                stringEyeNbrMap.put(str, vitalEyes);
            }

            if (allUnocupiedAdjacentToString(eye, str)) {
                eye.setUnconditionallyAlive(true);
                vitalEyes.add(eye);
            }
        }
    }

    /**
     * @return true if all the empty spaces in this eye are touching the specified string.
     */
    private boolean allUnocupiedAdjacentToString(IGoEye eye, IGoString string) {
        for (GoBoardPosition pos : eye.getMembers()) {
            if (pos.isUnoccupied()) {
                GoBoardPositionSet nbrs =
                        nbrAnalyzer_.getNobiNeighbors(pos, eye.isOwnedByPlayer1(), NeighborType.FRIEND);
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
     * @return true if any of the candidateStrings are unconditionally alive (i.e. pass alive).
     */
    private boolean determineUnconditionalLife() {
        GoStringSet livingStrings = findPassAliveStrings();
        return !livingStrings.isEmpty();
    }

    /**
     * @return the set of strings in the group that are unconditionally alive.
     */
    private GoStringSet findPassAliveStrings() {
        GoStringSet candidateStrings = new GoStringSet(group_.getMembers());
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
     * For each eye in the group, determine if it is unconditionally alive by verifying that
     * all its neighbors are unconditional life candidates still.
     */
    private void initializeEyeLife() {
        for (IGoEye eye : analyzerMap_.getAnalyzer(group_).getEyes(board_)) {
            eye.setUnconditionallyAlive(true);
            for (IGoString nbrStr : eyeStringNbrMap.get(eye)) {
                if (!(nbrStr.isUnconditionallyAlive())) {
                    eye.setUnconditionallyAlive(false);
                }
            }
        }
    }

    /**
     * @return the number of unconditionally alive adjacent eyes.
     */
    private int findNumLivingAdjacentEyes(IGoString str) {
        int numLivingAdjacentEyes = 0;

        GoEyeList vitalEyeNbrs = stringEyeNbrMap.get(str);
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