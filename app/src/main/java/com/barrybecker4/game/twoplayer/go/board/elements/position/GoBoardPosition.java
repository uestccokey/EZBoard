/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.elements.position;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.IGoMember;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;

/**
 * The GoBoardPosition describes the physical marker at a location on the board.
 * It can be empty or occupied. If occupied, then it is  either black or white and has a string owner.
 * A GoBoardPosition may have an eye owner if it is part of a group's eye.
 *
 * @author Barry Becker
 */
public final class GoBoardPosition extends BoardPosition
        implements IGoMember {

    /** the string (connected set of stones) to which this stone belongs. */
    private IGoString string_;

    /**
     * if non-null then this position belongs to an eye string.
     * The group owner of the eye is different than the owner of the string.
     */
    private IGoEye eye_;

    /**
     * when true the stone has been visited already during a search.
     * This is a temporary state that is used for some traversal operations.
     */
    private boolean visited_;

    /** the amount this position contributes to the overall score. */
    private double scoreContribution_ = 0.0;


    /**
     * create a new go stone.
     *
     * @param row    location.
     * @param col    location.
     * @param string the string that this stone belongs to.
     * @param stone  the stone at this position if there is one (use null if no stone).
     */
    public GoBoardPosition(int row, int col, IGoString string, GoStone stone) {
        super(row, col, stone);
        string_ = string;
        eye_ = null;
        visited_ = false;
    }

    /**
     * Create a deep copy of this position.
     */
    public GoBoardPosition(GoBoardPosition pos) {
        this(pos.location_.getRow(), pos.location_.getCol(), pos.string_,
                (pos.piece_ == null) ? null : (GoStone) pos.piece_.copy());
        pos.setEye(getEye());
        setVisited(pos.isVisited());
    }

    /**
     * @return copy of this position.
     */
    @Override
    public GoBoardPosition copy() {
        return new GoBoardPosition(this);
    }

    /**
     * @param string the string owner we are assign to this stone.
     */
    public void setString(IGoString string) {
        string_ = string;
    }

    /**
     * @return the string owner for this stone.
     * There may be none if its blank and part of an eye, in that case null is returned.
     */
    public IGoString getString() {
        return string_;
    }

    /**
     * @return the group owner.
     * There is only one group owner that has the same ownership (color) as this stone.
     * The stone may also belong to to an eye in an opponent group, however.
     */
    public IGoGroup getGroup() {
        if (string_ != null)
            return string_.getGroup();
        if (eye_ != null) {
            return eye_.getGroup();
        }
        return null;
    }

    /**
     * @param eye the eye owner this space is to be assigned to
     */
    public void setEye(IGoEye eye) {
        eye_ = eye;
    }

    /**
     * @return the eye that this space belongs to. May be null if no eye owner.
     */
    public IGoEye getEye() {
        return eye_;
    }

    /**
     * @return true if the string this stone belongs to is in atari
     */
    public boolean isInAtari(GoBoard b) {
        return (getString() != null && getString().getNumLiberties(b) == 1);
    }


    public void setVisited(boolean visited) {
        visited_ = visited;
    }

    public boolean isVisited() {
        return visited_;
    }

    /**
     * @return true if this position is part of an eye.
     */
    public boolean isInEye() {
        return eye_ != null;
    }

    /**
     * we must recalculate the number of liberties every time because it changes often.
     *
     * @return the number of liberties the specified position has.
     */
    public int getNumLiberties(GoBoard board) {
        int numLiberties = 0;
        int row = getRow();
        int col = getCol();
        if (row > 1 && board.getPosition(row - 1, col).isUnoccupied())
            numLiberties++;
        if (row < board.getNumRows() && board.getPosition(row + 1, col).isUnoccupied())
            numLiberties++;
        if (col > 1 && board.getPosition(row, col - 1).isUnoccupied())
            numLiberties++;
        if (col < board.getNumCols() && board.getPosition(row, col + 1).isUnoccupied())
            numLiberties++;

        return numLiberties;
    }

    /**
     * make it show an empty board position.
     */
    public void clear(GoBoard board) {
        IGoString string = getString();

        if (string != null) {
            string.remove(this, board);
        } else {
            assert isUnoccupied();
        }
        clear();
    }

    /**
     * make it show an empty board position.
     */
    @Override
    public void clear() {
        super.clear();
        setString(null);
        setEye(null);
        scoreContribution_ = 0;
        setVisited(false);
    }

    /**
     * @return a string representation of the go board position
     */
    @Override
    public String getDescription() {
        return super.getDescription() + " score:" + FormatUtil.formatNumber(scoreContribution_);
    }

    /**
     * @return a string representation of the go board position
     */
    @Override
    public String toString() {
        return super.toString() + " s:" + FormatUtil.formatNumber(scoreContribution_) + " hc:" + hashCode();
    }

    public double getScoreContribution() {
        return scoreContribution_;
    }

    public void setScoreContribution(double scoreContribution) {
        this.scoreContribution_ = scoreContribution;
    }
}
