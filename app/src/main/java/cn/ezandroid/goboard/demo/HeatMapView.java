package cn.ezandroid.goboard.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 热力图控件
 *
 * @author like
 * @date 2017-12-23
 */
public class HeatMapView extends View {

    private int mSquareSize; // 格子尺寸

    private Paint mHeatMapPaint;

    private int mBoardSize = 19; // 棋盘大小

    private float[] mHeatMap;

    public HeatMapView(Context context) {
        super(context);
        initHeatMap();
    }

    public HeatMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHeatMap();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        int minSize = Math.min(childWidthSize, childHeightSize);
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(minSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mSquareSize = Math.round(minSize / (mBoardSize + 1));
    }

    private void initHeatMap() {
        mHeatMapPaint = new Paint();
        mHeatMapPaint.setAntiAlias(true);
    }

    /**
     * 获取格子大小
     *
     * @return
     */
    public int getSquareSize() {
        return mSquareSize;
    }

    /**
     * 设置热力图
     *
     * @param heatMap
     */
    public void setHeatMap(float[] heatMap) {
        if (mHeatMap != heatMap) {
            mHeatMap = heatMap;
            invalidate();
        }
    }

    /**
     * 设置棋盘大小
     *
     * @param boardSize
     */
    public void setBoardSize(int boardSize) {
        if (mBoardSize != boardSize) {
            mBoardSize = boardSize;
            invalidate();
        }
    }

    /**
     * 获取棋盘大小
     *
     * @return
     */
    public int getBoardSize() {
        return mBoardSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制热力图
        drawHeatMap(canvas);
    }

    /**
     * 绘制热力图
     *
     * @param canvas
     */
    private void drawHeatMap(Canvas canvas) {
        if (mHeatMap != null) {
            mHeatMapPaint.setColor(Color.BLUE);
            mHeatMapPaint.setStyle(Paint.Style.FILL);

            canvas.translate(mSquareSize / 2, mSquareSize / 2);

            for (int i = 0; i < mHeatMap.length; i++) {
                mHeatMapPaint.setAlpha(Math.round(mHeatMap[i] * 192));

                int left = Math.round((i % mBoardSize) * mSquareSize);
                int top = Math.round((i / mBoardSize) * mSquareSize);
                int right = Math.round((i % mBoardSize + 1) * mSquareSize);
                int bottom = Math.round((i / mBoardSize + 1) * mSquareSize);
                canvas.drawRect(left, top, right, bottom, mHeatMapPaint);
            }

            canvas.translate(-mSquareSize / 2, -mSquareSize / 2);
        }
    }
}
