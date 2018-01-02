/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.group.eye;

import java.util.Iterator;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzer;
import cn.ezandroid.game.board.go.analysis.group.GroupAnalyzerMap;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborType;
import cn.ezandroid.game.board.go.elements.eye.GoEye;
import cn.ezandroid.game.board.go.elements.eye.GoEyeSet;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionLists;
import cn.ezandroid.game.common.geometry.Box;

/**
 * Analyzes the eye spaces within a group to determine if they are real eyes.
 *
 * @author Barry Becker
 */
class EyeSpaceAnalyzer {

    /** The group of go stones that we are analyzing eyespace for. */
    private IGoGroup group_;

    private GoBoard board_;

    /** bounding box around our group that we are analyzing. */
    private Box boundingBox_;

    private NeighborAnalyzer nbrAnalyzer_;
    private GroupAnalyzerMap analyzerMap_;

    /**
     * Constructor.
     */
    public EyeSpaceAnalyzer(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        group_ = group;
        analyzerMap_ = analyzerMap;
    }

    public void setBoard(GoBoard board) {
        board_ = board;
        nbrAnalyzer_ = new NeighborAnalyzer(board);
        boundingBox_ = group_.findBoundingBox();
    }

    /**
     * Determine the set of eyes within a group
     *
     * @return the set of eyes that are in this group.
     */
    public GoEyeSet determineEyes() {
        assert (board_ != null) : "The board must be set before determining eyes.";
        GoBoardPositionLists excludedSpaceLists = createExcludedLists();
        return findEyesFromCandidates(excludedSpaceLists);
    }

    /**
     * Eliminate all the stones and spaces that are in the bounding rectangle,
     * but not in the group. We do this by marching around the perimeter cutting out
     * the strings of empty or opponent spaces that do not belong.
     * Note: we do not go all the way to the edge. If the border of a group includes an edge of the board,
     * then empty spaces there are most likely eyes (but not necessarily).
     *
     * @return list of lists of eye space spaces find real eye from (and to unvisit at the end)
     */
    private GoBoardPositionLists createExcludedLists() {
        GoBoardPositionLists lists = new GoBoardPositionLists();
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();

        if (boundingBox_.getArea() == 0) return lists;
        int rMin = boundingBox_.getMinRow();
        int rMax = boundingBox_.getMaxRow();
        int cMin = boundingBox_.getMinCol();
        int cMax = boundingBox_.getMaxCol();

        if (cMin > 1) {
            for (int r = rMin; r <= rMax; r++) {
                excludeSeed((GoBoardPosition) board_.getPosition(r, cMin),
                        ownedByPlayer1, lists);
            }
        }
        if (cMax < board_.getNumCols()) {
            for (int r = rMin; r <= rMax; r++) {
                excludeSeed((GoBoardPosition) board_.getPosition(r, cMax),
                        ownedByPlayer1, lists);
            }
        }
        if (rMin > 1) {
            for (int c = cMin; c <= cMax; c++) {
                excludeSeed((GoBoardPosition) board_.getPosition(rMin, c),
                        ownedByPlayer1, lists);
            }
        }
        if (rMax < board_.getNumRows()) {
            for (int c = cMin; c <= cMax; c++) {
                excludeSeed((GoBoardPosition) board_.getPosition(rMax, c),
                        ownedByPlayer1, lists);
            }
        }

        clearEyes(rMin, rMax, cMin, cMax);
        return lists;
    }

    /**
     * Do a paint fill on each of the empty unvisited spaces.
     * Most of these remaining empty spaces are connected to an eye of some type.
     * There will be some that fill spaces between black and white stones.
     * Don't count these as eyes unless the stones of the opposite color are much weaker -
     * in which case they are assumed dead and hence part of the eye.
     *
     * @param excludedSpaceLists space lists to exclude from consideration because they are outside the group.
     * @return set of eyes in this group
     */
    private GoEyeSet findEyesFromCandidates(GoBoardPositionLists excludedSpaceLists) {
        GoEyeSet eyes = new GoEyeSet();
        boolean ownedByPlayer1 = group_.isOwnedByPlayer1();
        GroupAnalyzer groupAnalyzer = analyzerMap_.getAnalyzer(group_);

        Box innerBox = createBoxExcludingBorder(boundingBox_);
        for (int r = innerBox.getMinRow(); r < innerBox.getMaxRow(); r++) {
            for (int c = innerBox.getMinCol(); c < innerBox.getMaxCol(); c++) {

                // if the empty space is already marked as being an eye, skip
                GoBoardPosition space = (GoBoardPosition) board_.getPosition(r, c);
                assert space != null : "pos r=" + r + " c=" + c;
                if (!space.isVisited() && space.isUnoccupied() && !space.isInEye()) {
                    GoBoardPositionList eyeSpaces =
                            nbrAnalyzer_.findStringFromInitialPosition(space, ownedByPlayer1,
                                    false, NeighborType.NOT_FRIEND,
                                    boundingBox_);
                    excludedSpaceLists.add(eyeSpaces);
                    // make sure this is a real eye.
                    if (confirmEye(eyeSpaces)) {
                        GoEye eye = new GoEye(eyeSpaces, board_, group_, groupAnalyzer);
                        eyes.add(eye);
                    } else {
                        GameContext.log(3, eyeSpaces.toString("This list of stones was rejected as being an eye: "));
                    }
                }
            }
        }
        excludedSpaceLists.unvisitPositionsInLists();
        return eyes;
    }

    /**
     * @param box to reduce by the outside edge.
     * @return A new bounding box where we shave off the outer edge, unless on the edge of the board.
     */
    private Box createBoxExcludingBorder(Box box) {
        int maxRow = board_.getNumRows();
        int maxCol = board_.getNumCols();

        int innerMinRow = (box.getMinRow() > 1) ? Math.min(box.getMinRow() + 1, maxRow) : 1;
        int innerMinCol = (box.getMinCol() > 1) ? Math.min(box.getMinCol() + 1, maxCol) : 1;

        return new Box(
                innerMinRow,
                innerMinCol,
                (box.getMaxRow() < maxRow) ? Math.max(box.getMaxRow(), innerMinRow) : maxRow + 1,
                (box.getMaxCol() < maxCol) ? Math.max(box.getMaxCol(), innerMinCol) : maxCol + 1
        );
    }

    /**
     * Make sure all the positions do not cache their eye
     */
    private void clearEyes(int rMin, int rMax, int cMin, int cMax) {
        for (int r = rMin; r <= rMax; r++) {
            for (int c = cMin; c <= cMax; c++) {
                ((GoBoardPosition) board_.getPosition(r, c)).setEye(null);
            }
        }
    }

    /**
     * Mark as visited all the non-friend (empty or enemy) spaces connected to the specified seed.
     *
     * @param space seed
     * @param lists list of stones connected to the seed stone
     */
    private void excludeSeed(GoBoardPosition space, boolean groupOwnership,
                             GoBoardPositionLists lists) {
        if (!space.isVisited()
                && (space.isUnoccupied() || space.getPiece().isOwnedByPlayer1() != group_.isOwnedByPlayer1())) {
            // this will leave stones outside the group visited
            GoBoardPositionList exclusionList =
                    nbrAnalyzer_.findStringFromInitialPosition(space, groupOwnership, false,
                            NeighborType.NOT_FRIEND, boundingBox_);

            Iterator it = exclusionList.iterator();
            GroupAnalyzer groupAnalyzer = analyzerMap_.getAnalyzer(group_);

            while (it.hasNext()) {
                GoBoardPosition p = (GoBoardPosition) it.next();
                if (p.isOccupied()) {
                    // if its a very weak opponent (ie dead) then don't exclude it from the list
                    if (!groupAnalyzer.isTrueEnemy(p)) {
                        p.setVisited(false);
                        it.remove();  // remove it from the list
                    }
                }
            }

            if (exclusionList.size() > 0) {
                lists.add(exclusionList);
            }
        }
    }

    /**
     * Check this list of stones to confirm that enemy stones don't border it.
     * If they do, then it is not an eye - return false.
     * <p>
     * I used to attempt to compare the health of the position relative to the surrounding group
     * to see if it is dead enough to still consider an eye, but then realized there is a chicken-egg
     * problem in that we don't really know the liveness until we know the eyes.
     *
     * @param eyeList the candidate string of stones to test for eye status
     * @return true if the list of stones is an eye
     */
    private boolean confirmEye(GoBoardPositionList eyeList) {
        if (eyeList == null)
            return false;

        for (GoBoardPosition position : eyeList) {

            if (boundingBox_.isOnEdge(position.getLocation()) && !withinBorderEdge(position)) {
                // then the potential eye breaks through to the outside of the group bounds,
                //so we really cannot consider it eyeList yet, though it likely will be.
                return false;
            }
        }

        // if we make it here, its a bonafied eye.
        return true;
    }

    /**
     * Positions marked E are considered on edge of edge.
     * Note that we are within the edge border if the position
     * is both on the bounding box corner and the board corner.
     * <p>
     * E****        ******
     * *    or  *    *
     * E        E    E
     *
     * @param position the position to check.
     * @return true if on edge of border edge
     */
    private boolean withinBorderEdge(GoBoardPosition position) {
        boolean isOnbboxCorner = boundingBox_.isOnCorner(position.getLocation());
        boolean isInCorner = board_.isInCorner(position);
        boolean edgeOfEdge = isOnbboxCorner ^ isInCorner;
        return board_.isOnEdge(position) && !edgeOfEdge;
    }
}
