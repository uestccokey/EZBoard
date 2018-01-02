/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.board;

import java.io.Serializable;

/**
 * 棋子模型
 *
 * @author Barry Becker
 */
public class GamePiece implements Serializable {

    private static final long serialVersionUID = 1;

    public static final char REGULAR_PIECE = 'x';

    // 是否Player1的棋子
    protected boolean mIsOwnedByPlayer1;

    // 棋子类型（比如象棋里的车马炮等）
    protected char mPieceType;

    // 关联字符串（用来显示一些额外信息）
    private String mAnnotation;

    protected GamePiece() {
        mIsOwnedByPlayer1 = false;
        mPieceType = REGULAR_PIECE;
    }

    public GamePiece(boolean player1) {
        mIsOwnedByPlayer1 = player1;
        mPieceType = REGULAR_PIECE;
    }

    protected GamePiece(boolean player1, char type) {
        mIsOwnedByPlayer1 = player1;
        mPieceType = type;
    }

    protected GamePiece(GamePiece piece) {
        this(piece.isOwnedByPlayer1());
        this.mPieceType = piece.getType();
        mAnnotation = piece.mAnnotation;
    }

    public GamePiece copy() {
        return new GamePiece(this);
    }

    public final char getType() {
        return mPieceType;
    }

    public final boolean isOwnedByPlayer1() {
        return mIsOwnedByPlayer1;
    }

    /**
     * 设置关联字符串信息
     *
     * @param annotation
     */
    public final void setAnnotation(String annotation) {
        this.mAnnotation = annotation;
    }

    /**
     * 获取关联字符串信息
     *
     * @return
     */
    public String getAnnotation() {
        return mAnnotation;
    }
}

