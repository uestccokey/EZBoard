/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go;

import java.util.ArrayList;
import java.util.List;

import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoBoardPositionList;
import cn.ezandroid.game.board.go.elements.position.GoStone;
import cn.ezandroid.game.board.go.move.GoMove;

/**
 * 让子
 *
 * @author Barry Becker
 */
class HandicapStones {

    // 让子的默认健康评分
    private static final float HANDICAP_STONE_HEALTH = 0.8f;

    // 默认不让子
    private int mNumHandicapStones = 0;

    // 通常最多只让9子
    private GoBoardPositionList mStarPoints;

    HandicapStones(int num, int boardSize) {
        initStarPoints(boardSize);
        mNumHandicapStones = num;
    }

    /**
     * 获取让子数
     *
     * @return
     */
    public int getNumber() {
        return mNumHandicapStones;
    }

    /**
     * 获取星位点
     *
     * @return
     */
    public List getStarPoints() {
        return mStarPoints;
    }

    /**
     * 将让子也作为正常着手，获取让子着手列表
     *
     * @return
     */
    public List<GoMove> getHandicapMoves() {
        assert mNumHandicapStones <= mStarPoints.size();
        List<GoMove> handicapMoves = new ArrayList<>(mNumHandicapStones);

        for (int i = 0; i < mNumHandicapStones; i++) {
            GoBoardPosition hpos = mStarPoints.get(i);

            GoMove m = new GoMove(hpos.getLocation(), 0, (GoStone) hpos.getPiece());
            handicapMoves.add(m);
        }
        return handicapMoves;
    }

    private void initStarPoints(int boardSize) {
        // initialize the list of handicap stones.
        // The number of these that actually get placed on the board
        // depends on the handicap
        mStarPoints = new GoBoardPositionList();
        int min = 4;
        // on a really small board we put the corner star points at 3-3.
        if (boardSize < 13)
            min = 3;
        int max = boardSize - (min - 1);
        int mid = (boardSize >> 1) + 1;

        // add the star points
        GoStone handicapStone = new GoStone(true, HANDICAP_STONE_HEALTH);
        mStarPoints.add(new GoBoardPosition(min, min, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(max, max, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(min, max, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(max, min, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(min, mid, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(max, mid, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(mid, min, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(mid, max, null, handicapStone.copy()));
        mStarPoints.add(new GoBoardPosition(mid, mid, null, handicapStone));
    }
}