package cn.ezandroid.goboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 棋盘显示控件
 *
 * @author like
 * @date 2017-12-20
 */
public class BoardView extends RelativeLayout {

    private int mCubicSize; // 格子尺寸

    private Paint mBoardPaint;

    private int mBoardSize = 19; // 棋盘大小

    private boolean mIsShowCoordinate = true; // 是否显示坐标

    public BoardView(Context context) {
        super(context);
        initBoard();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBoard();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        int minSize = Math.min(childWidthSize, childHeightSize);
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(minSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCubicSize = Math.round(minSize / (mBoardSize + 1));
    }

    private void initBoard() {
        this.mBoardPaint = new Paint();
        this.mBoardPaint.setAntiAlias(true);
        this.mBoardPaint.setStyle(Paint.Style.FILL);

        setWillNotDraw(false);
    }

    /**
     * 设置棋盘大小
     *
     * @param boardSize
     */
    public void setBoardSize(int boardSize) {
        mBoardSize = boardSize;
        invalidate();
    }

    /**
     * 获取棋盘大小
     *
     * @return
     */
    public int getBoardSize() {
        return mBoardSize;
    }

    /**
     * 设置是否显示坐标
     *
     * @param isShowCoordinate
     */
    public void setShowCoordinate(boolean isShowCoordinate) {
        mIsShowCoordinate = isShowCoordinate;
        invalidate();
    }

    /**
     * 获取是否显示坐标
     *
     * @return
     */
    public boolean isShowCoordinate() {
        return mIsShowCoordinate;
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 绘制棋盘
        drawBoard(canvas);

        super.onDraw(canvas);
    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int coordinateTextSize = mCubicSize / 2;

        mBoardPaint.setColor(Color.BLACK);
        mBoardPaint.setTextSize(coordinateTextSize);

        canvas.translate(mCubicSize, mCubicSize);

        // 1，绘制棋盘坐标
        if (mIsShowCoordinate) {
            // 横坐标
            char c = 'A';
            for (int i = 0; i < mBoardSize; i++) {
                char cc = (char) (c + i);
                String abscissa;
                float width;
                if (cc >= 'I') {
                    abscissa = "" + (char) (cc + 1);
                } else {
                    abscissa = "" + cc;
                }
                width = mBoardPaint.measureText(abscissa);
                canvas.drawText(abscissa, i * mCubicSize - width / 2, -coordinateTextSize / 2, mBoardPaint);
            }
            // 纵坐标
            for (int i = 0; i < mBoardSize; i++) {
                String ordinate = "" + (i + 1);
                float width = mBoardPaint.measureText(ordinate);
                canvas.drawText(ordinate, -mCubicSize / 2 - width / 2, i * mCubicSize + coordinateTextSize / 2, mBoardPaint);
            }
        }

        // 2，绘制棋盘线
        for (int i = 0; i < mBoardSize; i++) {
            if (i == 0 || i == mBoardSize - 1) {
                mBoardPaint.setStrokeWidth(3.0f);
            } else {
                mBoardPaint.setStrokeWidth(1.5f);
            }
            canvas.drawLine(0, i * mCubicSize, mCubicSize * (mBoardSize - 1),
                    i * mCubicSize, mBoardPaint);
            canvas.drawLine(i * mCubicSize, 0, i * mCubicSize,
                    mCubicSize * (mBoardSize - 1), mBoardPaint);
        }

        // 3，绘制星位
        for (int i = 0; i < mBoardSize; i++) {
            for (int j = 0; j < mBoardSize; j++) {
                switch (mBoardSize) {
                    case 9:
                        if ((i == 2 || i == 6) && (j == 2 || j == 6) || (i == 4 && j == 4)) {
                            canvas.drawCircle(i * mCubicSize, j * mCubicSize, 8, mBoardPaint);
                        }
                        break;
                    case 13:
                        if ((i == 3 || i == 9) && (j == 3 || j == 9) || (i == 6 && j == 6)) {
                            canvas.drawCircle(i * mCubicSize, j * mCubicSize, 8, mBoardPaint);
                        }
                        break;
                    case 19:
                        if ((i == 3 || i == 9 || i == 15) && (j == 3 || j == 9 || j == 15)) {
                            canvas.drawCircle(i * mCubicSize, j * mCubicSize, 8, mBoardPaint);
                        }
                        break;
                }
            }
        }

        canvas.translate(-mCubicSize, -mCubicSize);
    }
}
