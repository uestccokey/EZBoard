/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.move;

import java.util.List;

import cn.ezandroid.game.board.common.board.Board;
import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.common.board.CaptureList;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.neighbor.NeighborAnalyzer;
import cn.ezandroid.game.board.go.elements.group.GoGroup;
import cn.ezandroid.game.board.go.elements.group.IGoGroup;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionLists;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionSet;
import cn.ezandroid.game.board.go.elements.string.GoString;
import cn.ezandroid.game.board.go.elements.string.IGoString;

/**
 * 被吃的围棋棋子列表
 *
 * @author Barry Becker
 */
public class GoCaptureList extends CaptureList {

    public GoCaptureList() {
    }

    public GoCaptureList(CaptureList captureList) {
        super(captureList);
    }

    @Override
    public GoCaptureList copy() {
        return new GoCaptureList(this);
    }

    public boolean addCaptures(GoBoardPositionSet set) {
        if (set == null) {
            return false;
        }

        for (GoBoardPosition capture : set) {
            // make sure none of the captures are blanks
            assert capture.isOccupied();
            add(capture.copy());
        }
        return (!set.isEmpty());
    }

    /**
     * 从棋盘上移除
     */
    @Override
    public void removeFromBoard(Board board) {
        GoBoard goBoard = (GoBoard) board;
        for (BoardPosition c : this) {
            GoBoardPosition capStone = (GoBoardPosition) c;
            GoBoardPosition stoneOnBoard =
                    (GoBoardPosition) goBoard.getPosition(capStone.getLocation());
            stoneOnBoard.clear(goBoard);
        }

        adjustStringLiberties(goBoard);
    }

    /**
     * 恢复到棋盘上
     */
    @Override
    public void restoreOnBoard(Board board) {
        GoBoard goBoard = (GoBoard) board;
        super.modifyCaptures(goBoard, false);

        GoBoardPositionLists strings = getRestoredStringLists(goBoard); // XXX should remove
        adjustStringLiberties(goBoard);

        // XXX should remove next lines
        IGoGroup group = getRestoredGroup(strings, goBoard);

        assert (group != null) : "no group was formed when restoring "
                + this + " the list of strings was " + strings;
        goBoard.getGroups().add(group);
    }

    /**
     * 更新周围棋串的气
     *
     * @param board
     */
    public void adjustStringLiberties(GoBoard board) {
        for (BoardPosition capture : this) {
            GoBoardPosition captured = (GoBoardPosition) capture;
            GoBoardPosition newLiberty = (GoBoardPosition) board.getPosition(captured.getLocation());
            board.adjustLiberties(newLiberty);
        }
    }

    private GoBoardPositionLists getRestoredStringLists(GoBoard b) {
        GoBoardPositionList restoredList = getRestoredList(b);
        GoBoardPositionLists strings = new GoBoardPositionLists();
        NeighborAnalyzer nbrAnalyzer = new NeighborAnalyzer(b);
        for (GoBoardPosition s : restoredList) {
            if (!s.isVisited()) {
                GoBoardPositionList string1 = nbrAnalyzer.findStringFromInitialPosition(s, false);
                strings.add(string1);
            }
        }
        return strings;
    }

    private GoBoardPositionList getRestoredList(GoBoard b) {
        GoBoardPositionList restoredList = new GoBoardPositionList();
        for (BoardPosition pos : this) {
            GoBoardPosition capStone = (GoBoardPosition) pos;
            GoBoardPosition stoneOnBoard =
                    (GoBoardPosition) b.getPosition(capStone.getRow(), capStone.getCol());
            stoneOnBoard.setVisited(false); // make sure in virgin unvisited state

            // --adjustLiberties(stoneOnBoard, board);
            restoredList.add(stoneOnBoard);
        }
        return restoredList;
    }

    /**
     * @return the group that was restored when the captured stones were replaced on the board.
     */
    private IGoGroup getRestoredGroup(List<GoBoardPositionList> strings, GoBoard b) {
        // ?? form new group, or check group nbrs to see if we can add to an existing one.
        boolean firstString = true;
        IGoGroup group = null;
        for (GoBoardPositionList stringList : strings) {
            IGoString string = new GoString(stringList, b);
            if (firstString) {
                group = new GoGroup(string);
                firstString = false;
            } else {
                group.addMember(string);
            }
            string.setVisited(false);
        }
        return group;
    }
}

