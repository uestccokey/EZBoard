/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.update;

import cn.ezandroid.game.board.go.move.GoMove;

/**
 * 保存每个玩家的提子数
 *
 * @author Barry Becker
 */
public class CaptureCounts {

    private int mNumWhiteStonesCaptured = 0;
    private int mNumBlackStonesCaptured = 0;

    public CaptureCounts copy() {
        CaptureCounts countsCopy = new CaptureCounts();
        countsCopy.mNumBlackStonesCaptured = this.mNumBlackStonesCaptured;
        countsCopy.mNumWhiteStonesCaptured = this.mNumWhiteStonesCaptured;
        return countsCopy;
    }

    /**
     * 获取指定玩家的提子数
     *
     * @param player1StonesCaptured
     * @return
     */
    public int getNumCaptures(boolean player1StonesCaptured) {
        return player1StonesCaptured ? mNumBlackStonesCaptured : mNumWhiteStonesCaptured;
    }

    /**
     * 更新提子数
     *
     * @param move
     * @param increment
     */
    public void updateCaptures(GoMove move, boolean increment) {
        int numCaptures = move.getNumCaptures();
        int num = increment ? move.getNumCaptures() : -move.getNumCaptures();

        if (numCaptures > 0) {
            if (move.isPlayer1()) {
                mNumWhiteStonesCaptured += num;
                assert mNumWhiteStonesCaptured >= 0 :
                        "The number of captured white stones became less than 0 :  " + mNumWhiteStonesCaptured;
            } else {
                mNumBlackStonesCaptured += num;
                assert mNumBlackStonesCaptured >= 0 :
                        "The number of captured black stones became less than 0 :  " + mNumBlackStonesCaptured;
            }
        }
    }
}
