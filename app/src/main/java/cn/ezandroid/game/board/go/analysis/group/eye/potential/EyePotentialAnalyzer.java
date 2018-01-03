/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group.eye.potential;

import cn.ezandroid.game.board.common.geometry.Box;
import cn.ezandroid.game.board.common.geometry.ByteLocation;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * Figure out how likely (the potential) that a group can form two eyes.
 *
 * @author Barry Becker
 */
public class EyePotentialAnalyzer {

    /** The group of go stones that we are analyzing. */
    private IGoGroup group_;

    private GoBoard board_;

    /** bounding box around our group that we are analyzing. */
    private Box boundingBox_;

    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor.
     */
    public EyePotentialAnalyzer(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        group_ = group;
        analyzerMap_ = analyzerMap;
    }

    public void setBoard(GoBoard board) {
        board_ = board;
        boundingBox_ = group_.findBoundingBox();
    }

    /**
     * 计算做出两个眼的潜力
     *
     * @return 在0~2间取值，2表示可以轻松做出两眼
     */
    public float calculateEyePotential() {
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        boundingBox_.expandGloballyBy(1, numRows, numCols);
        boundingBox_.expandBordersToEdge(1, numRows, numCols);

        return findTotalEyePotential();
    }

    /**
     * Make sure that every internal enemy stone is really an enemy and not just dead.
     * compare it with one of the group strings.
     *
     * @return eyePotential - a measure of how easily this group can make 2 eyes (0 - 2; 2 meaning has 2 eyes).
     */
    private float findTotalEyePotential() {
        if (group_.getMembers().isEmpty()) return 0;
        IGoString groupString = group_.getMembers().iterator().next();

        int rMin = boundingBox_.getMinRow();
        int rMax = boundingBox_.getMaxRow();
        int cMin = boundingBox_.getMinCol();
        int cMax = boundingBox_.getMaxCol();

        float totalPotential = 0;
        totalPotential += getTotalRowPotentials(groupString, rMin, rMax, cMin, cMax);
        totalPotential += getTotalColumnPotentials(groupString, rMin, rMax, cMin, cMax);

        return (float) Math.min(1.9, Math.sqrt(totalPotential) / 1.3);
    }

    /**
     * @return total of all the row run potentials.
     */
    private float getTotalRowPotentials(IGoString groupString, int rMin, int rMax, int cMin, int cMax) {
        float totalPotential = 0;
        RunPotentialAnalyzer runAnalyzer = new RunPotentialAnalyzer(groupString, board_, analyzerMap_);

        for (int r = rMin; r <= rMax; r++) {
            totalPotential += runAnalyzer.getRunPotential(new ByteLocation(r, cMin), 0, 1, rMax, cMax);
        }
        return totalPotential;
    }

    /**
     * @return total of all the column run potentials.
     */
    private float getTotalColumnPotentials(IGoString groupString, int rMin, int rMax, int cMin, int cMax) {

        float totalPotential = 0;
        RunPotentialAnalyzer runAnalyzer = new RunPotentialAnalyzer(groupString, board_, analyzerMap_);

        for (int c = cMin; c <= cMax; c++) {
            totalPotential += runAnalyzer.getRunPotential(new ByteLocation(rMin, c), 1, 0, rMax, cMax);
        }
        return totalPotential;
    }
}