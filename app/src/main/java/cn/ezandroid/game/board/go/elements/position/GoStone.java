/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.position;

import cn.ezandroid.game.board.common.board.GamePiece;
import cn.ezandroid.game.board.go.elements.IGoMember;

/**
 * 围棋棋子模型
 *
 * @author Barry Becker
 */
public class GoStone extends GamePiece implements IGoMember {

    // 棋子的健康评分，取值范围是(-1,1)，一个活着的黑棋评分为正值，死去的黑棋的评分为负值，白棋相反
    private float mHealth;

    // 是否当前棋子是死子
    private boolean mIsDead;

    public GoStone(boolean player1) {
        super(player1, REGULAR_PIECE);
        mHealth = 0.0f;
    }

    protected GoStone(GoStone stone) {
        super(stone);
        this.mHealth = stone.mHealth;
        this.mIsDead = stone.mIsDead;
    }

    @Override
    public GoStone copy() {
        return new GoStone(this);
    }

    public GoStone(boolean player1, float health) {
        super(player1, REGULAR_PIECE);
        mHealth = health;
    }

    public void setHealth(float health) {
        mHealth = health;
    }

    public float getHealth() {
        return mHealth;
    }

    public String getLabel() {
        return this.isOwnedByPlayer1() ? "B" : "W";
    }

    public boolean isDead() {
        return mIsDead;
    }

    public void setDead(boolean dead) {
        mIsDead = dead;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(mIsOwnedByPlayer1 ? 'B' : 'W');
        return sb.toString();
    }
}
