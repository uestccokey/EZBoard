package cn.ezandroid.goboard.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 形势图控件
 *
 * @author like
 * @date 2018-01-01
 */
public class TerrainMapView extends View {

    private int mSquareSize; // 格子尺寸

    private Paint mTerrainMapPaint;

    private int mBoardSize = 19; // 棋盘大小

    private float[] mTerrainMap;

    private float mThreshold = 0.35f; // 阈值

    public TerrainMapView(Context context) {
        super(context);
        initHeatMap();
    }

    public TerrainMapView(Context context, @Nullable AttributeSet attrs) {
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
        mTerrainMapPaint = new Paint();
        mTerrainMapPaint.setAntiAlias(true);
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
     * 设置形势图
     *
     * @param terrainMap
     */
    public void setTerrainMap(float[] terrainMap) {
        if (mTerrainMap != terrainMap) {
            mTerrainMap = terrainMap;
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

    /**
     * 设置阈值
     *
     * @param threshold
     */
    public void setThreshold(float threshold) {
        mThreshold = threshold;
    }

    /**
     * 获取阈值
     *
     * @return
     */
    public float getThreshold() {
        return mThreshold;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制形势图
        drawTerrainMap(canvas);
    }

    /**
     * 绘制形势图
     *
     * @param canvas
     */
    private void drawTerrainMap(Canvas canvas) {
        if (mTerrainMap != null) {
            mTerrainMapPaint.setStyle(Paint.Style.FILL);

            canvas.translate(mSquareSize / 2, mSquareSize / 2);

            for (int i = 0; i < mTerrainMap.length; i++) {
                float value = mTerrainMap[i];
                if (value < -1) {
                    value = -1;
                } else if (value > 1) {
                    value = 1;
                }
                if (value < -mThreshold) {
                    mTerrainMapPaint.setColor(Color.BLUE);
                } else if (value > mThreshold) {
                    mTerrainMapPaint.setColor(Color.RED);
                } else {
                    continue;
                }

                mTerrainMapPaint.setAlpha(Math.round(Math.abs(value) * 192));

                int left = Math.round((i % mBoardSize) * mSquareSize);
                int top = Math.round((i / mBoardSize) * mSquareSize);
                int right = Math.round((i % mBoardSize + 1) * mSquareSize);
                int bottom = Math.round((i / mBoardSize + 1) * mSquareSize);
                canvas.drawRect(left, top, right, bottom, mTerrainMapPaint);
            }

            canvas.translate(-mSquareSize / 2, -mSquareSize / 2);
        }
    }
}
