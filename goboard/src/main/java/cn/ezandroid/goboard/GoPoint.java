package cn.ezandroid.goboard;

import java.io.Serializable;

/**
 * 棋子模型类
 *
 * @author like
 */
public class GoPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    public final static int PLAYER_EMPTY = 0;
    public final static int PLAYER_BLACK = 1;
    public final static int PLAYER_WHITE = -1;

    public final static int STYLE_GENERAL = 0;
    public final static int STYLE_HIGHLIGHT = 1;
    public final static int STYLE_READY = 2;

    public final static int NONE = -1;
    public final static int TRIANGLE = -2;
    public final static int SQUARE = -3;
    public final static int CIRCLE = -4;
    public final static int CROSS = -5;

    private int mPlayer = PLAYER_EMPTY;
    private int mStyle = STYLE_GENERAL;
    private int mNumber = NONE;// 棋子上的手数
    private int mMark = NONE;
    private int mLetter = NONE;
    private String mLabel;
    private boolean mMarked;

    private int mCol;

    private int mRow;

    public int getCol() {
        return mCol;
    }

    public void setCol(int col) {
        mCol = col;
    }

    public int getRow() {
        return mRow;
    }

    public void setRow(int row) {
        mRow = row;
    }

    /**
     * 点目的形势.
     */
    private float mTerrain;

    public float getTerrain() {
        return mTerrain;
    }

    public void setTerrain(float terrain) {
        this.mTerrain = terrain;
    }

    public boolean isMarked() {
        return mMarked;
    }

    public void setMarked(boolean marked) {
        this.mMarked = marked;
    }

    public int getPlayer() {
        return mPlayer;
    }

    public void setPlayer(int player) {
        this.mPlayer = player;
    }

    public int getStyle() {
        return mStyle;
    }

    public void setStyle(int style) {
        this.mStyle = style;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        this.mNumber = number;
    }

    public int getMark() {
        return mMark;
    }

    public void setMark(int mark) {
        this.mMark = mark;
    }

    public int getLetter() {
        return mLetter;
    }

    public void setLetter(int letter) {
        this.mLetter = letter;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }
}
