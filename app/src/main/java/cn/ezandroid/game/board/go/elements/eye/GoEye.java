/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.eye;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.eye.EyeTypeAnalyzer;
import cn.ezandroid.game.board.go.analysis.eye.information.EyeInformation;
import cn.ezandroid.game.board.go.analysis.eye.information.EyeStatus;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzer;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoString;

/**
 * A GoEye is composed of a strongly connected set of empty spaces (and possibly some dead enemy stones).
 * By strongly connected I mean nobi connections only.
 * A GoEye may be one of several different eye types enumerated below
 * A group needs 2 provably true eyes to live.
 *
 * @author Barry Becker
 */
public class GoEye extends GoString implements IGoEye {

    /** A set of positions that are in the eye space. Need not be empty. */
    private GoBoardPositionSet members_;

    /** The kind of eye that this is. */
    private final EyeInformation information_;

    /** In addition to the type, an eye can have a status like nakade, unsettled, or aliveInAtari. */
    private final EyeStatus status_;

    private int numCornerPoints_;
    private int numEdgePoints_;

    /**
     * Constructor.
     * Immutable after construction.
     * Create a new eye shape containing the specified list of stones/spaces.
     * Some of the spaces may be occupied by dead opponent stones.
     */
    public GoEye(GoBoardPositionList spaces, GoBoard board, IGoGroup g, GroupAnalyzer groupAnalyzer) {
        super(spaces, board);
        group_ = g;
        mIsOwnedByPlayer1 = g.isOwnedByPlayer1();

        EyeTypeAnalyzer eyeAnalyzer = new EyeTypeAnalyzer(this, board, groupAnalyzer);
        information_ = eyeAnalyzer.determineEyeInformation();
        status_ = information_.determineStatus(this, board);
        initializePositionCounts(board);
    }

    @Override
    public EyeInformation getInformation() {
        return information_;
    }

    @Override
    public EyeStatus getStatus() {
        return status_;
    }

    @Override
    public String getEyeTypeName() {
        if (information_ == null)
            return "unknown eye type";
        return information_.getTypeName();
    }

    @Override
    public int getNumCornerPoints() {
        return numCornerPoints_;
    }

    @Override
    public int getNumEdgePoints() {
        return numEdgePoints_;
    }

    @Override
    protected void initializeMembers() {
        members_ = new GoBoardPositionSet();
    }

    private void initializePositionCounts(GoBoard board) {
        numCornerPoints_ = 0;
        numEdgePoints_ = 0;
        for (GoBoardPosition pos : getMembers()) {
            if (board.isCornerTriple(pos)) {
                numCornerPoints_++;
            }
            if (board.isOnEdge(pos)) {
                numEdgePoints_++;
            }
        }
    }

    /**
     * @return the hashSet containing the members
     */
    @Override
    public GoBoardPositionSet getMembers() {
        return members_;
    }

    /**
     * Add a space to the eye string.
     * The space is either blank or a dead enemy stone.
     * Called only during initial construction.
     */
    @Override
    protected void addMemberInternal(GoBoardPosition space, GoBoard board) {
        if (getMembers().contains(space)) {
            GameContext.log(1, "Warning: the eye, " + this + ", already contains " + space);
            assert ((space.getString() == null) || (this == space.getString())) :
                    "bad space or bad owning string" + space.getString();
        }
        space.setEye(this);
        getMembers().add(space);
    }

    @Override
    public void clear() {
        for (GoBoardPosition pos : getMembers()) {
            pos.setEye(null);
            pos.setVisited(false);
        }
        setGroup(null);
        getMembers().clear();
    }

    public String toString() {
        return new EyeSerializer(this).serialize();
    }

    @Override
    protected String getPrintPrefix() {
        return " Eye: " + getEyeTypeName() + ": ";
    }
}