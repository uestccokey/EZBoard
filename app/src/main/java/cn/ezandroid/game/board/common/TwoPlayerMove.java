/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common;

import cn.ezandroid.game.board.common.board.GamePiece;
import cn.ezandroid.game.board.common.geometry.ByteLocation;
import cn.ezandroid.game.board.common.geometry.Location;

/**
 * 两人游戏落子模型
 *
 * @author Barry Becker
 */
public class TwoPlayerMove extends Move {

    // 这手棋下的位置
    protected Location mToLocation;

    // 这手棋下的棋子
    private GamePiece mPiece;

    // 是否玩家1的落子
    private boolean mIsPlayer1;

    // 是否Pass
    protected boolean mIsPass = false;

    // 是否认输
    protected boolean mIsResignation = false;

    protected TwoPlayerMove() {}

    protected TwoPlayerMove(Location destination, int val, GamePiece p) {
        mToLocation = destination;

        setValue(val);
        mPiece = p;
        if (p != null) {
            mIsPlayer1 = p.isOwnedByPlayer1();
        }
        mIsPass = false;
    }

    protected TwoPlayerMove(TwoPlayerMove move) {
        this(move.getToLocation(), move.getValue(), (move.getPiece() != null) ? move.getPiece().copy() : null);
        this.mIsPass = move.mIsPass;
        this.mIsResignation = move.mIsResignation;
        this.setPlayer1(move.isPlayer1());
    }

    @Override
    public TwoPlayerMove copy() {
        return new TwoPlayerMove(this);
    }

    public static TwoPlayerMove createMove(int destinationRow, int destinationCol,
                                           int val, GamePiece piece) {
        return new TwoPlayerMove(new ByteLocation(destinationRow, destinationCol), val, piece);
    }

    public static TwoPlayerMove createMove(Location destinationLocation,
                                           int val, GamePiece piece) {
        return new TwoPlayerMove(destinationLocation, val, piece);
    }

    public final byte getToRow() {
        return (byte) mToLocation.getRow();
    }

    public final byte getToCol() {
        return (byte) mToLocation.getCol();
    }

    public final Location getToLocation() {
        return mToLocation;
    }

    @Override
    public int compareTo(Move m) {
        int result = super.compareTo(m);
        if (result != 0) {
            return result;
        }

        // break tie by row position
        TwoPlayerMove move = (TwoPlayerMove) m;
        if (this.getToRow() < move.getToRow())
            return -1;
        else if (this.getToRow() > move.getToRow())
            return 1;
        else {
            // if still tie, break using col position.
            if (this.getToCol() < move.getToCol()) {
                return -1;
            } else if (this.getToCol() > move.getToCol()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TwoPlayerMove)) return false;
        TwoPlayerMove that = (TwoPlayerMove) o;

        return mIsPlayer1 == that.mIsPlayer1 && mToLocation != null && mToLocation.equals(that.mToLocation);
    }

    @Override
    public int hashCode() {
        int result = mToLocation != null ? mToLocation.hashCode() : 0;
        result = 31 * result + getValue();
        result = 31 * result + (mIsPlayer1 ? 1 : 0);
        return result;
    }

    public final boolean isPassingMove() {
        return mIsPass;
    }

    public final boolean isResignationMove() {
        return mIsResignation;
    }

    public final boolean isPassOrResignation() {
        return mIsPass || mIsResignation;
    }

    public boolean isPlayer1() {
        return mIsPlayer1;
    }

    public void setPlayer1(boolean player1) {
        this.mIsPlayer1 = player1;
    }

    public GamePiece getPiece() {
        return mPiece;
    }

    public void setPiece(GamePiece piece) {
        this.mPiece = piece;
    }
}

