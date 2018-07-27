package cn.ezandroid.lib.board;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * 棋盘显示控件
 *
 * @author like
 * @date 2017-12-20
 */
public class BoardView extends RelativeLayout {

    private int mSquareSize; // 格子尺寸

    private Paint mBoardPaint;

    private int mBoardSize = 19; // 棋盘大小

    private int mStoneSpace = 6; // 棋子间距

    private boolean mIsShowCoordinate = true; // 是否显示坐标

    private Map<Stone, StoneView> mStoneViewMap = new HashMap<>(); // StoneView映射图，用来根据Stone快速查找对应的StoneView

    private Intersection mHighlightIntersection;

    private boolean mIsDrawNumber = true; // 是否绘制棋子手数

    private Bitmap mBoardBitmap; // 棋盘样式

    private Bitmap mBlackStoneBitmap; // 黑子样式
    private Bitmap mWhiteStoneBitmap; // 白子样式

    private Bitmap mBlackStoneShadowBitmap; // 黑子阴影图
    private Bitmap mWhiteStoneShadowBitmap; // 白子阴影图

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

        mSquareSize = Math.round(minSize * 1f / (mBoardSize + 1));
    }

    private void initBoard() {
        mBoardPaint = new Paint();
        mBoardPaint.setAntiAlias(true);

        setWillNotDraw(false);
    }

    /**
     * 重置棋盘
     */
    public void reset() {
        mStoneViewMap.clear();
        mHighlightIntersection = null;
        removeAllViews();
    }

    /**
     * 设置棋盘样式
     *
     * @param bitmap
     */
    public void setBoardBitmap(Bitmap bitmap) {
        mBoardBitmap = bitmap;
        invalidate();
    }

    /**
     * 设置黑子样式
     *
     * @param blackStoneBitmap
     */
    public void setBlackStoneBitmap(Bitmap blackStoneBitmap) {
        mBlackStoneBitmap = blackStoneBitmap;
        for (StoneView view : mStoneViewMap.values()) {
            if (view.getStone().color == StoneColor.BLACK) {
                view.setStoneBitmap(mBlackStoneBitmap);
            }
        }
        invalidate();
    }

    /**
     * 设置白子样式
     *
     * @param whiteStoneBitmap
     */
    public void setWhiteStoneBitmap(Bitmap whiteStoneBitmap) {
        mWhiteStoneBitmap = whiteStoneBitmap;
        for (StoneView view : mStoneViewMap.values()) {
            if (view.getStone().color == StoneColor.WHITE) {
                view.setStoneBitmap(mWhiteStoneBitmap);
            }
        }
        invalidate();
    }

    /**
     * 设置黑子阴影图
     *
     * @param stoneShadowBitmap
     */
    public void setBlackStoneShadowBitmap(Bitmap stoneShadowBitmap) {
        mBlackStoneShadowBitmap = stoneShadowBitmap;
        invalidate();
    }

    /**
     * 设置白子阴影图
     *
     * @param stoneShadowBitmap
     */
    public void setWhiteStoneShadowBitmap(Bitmap stoneShadowBitmap) {
        mWhiteStoneShadowBitmap = stoneShadowBitmap;
        invalidate();
    }

    /**
     * 设置棋子间距
     *
     * @param stoneSpace
     */
    public void setStoneSpace(int stoneSpace) {
        if (mStoneSpace != stoneSpace) {
            mStoneSpace = stoneSpace;
            invalidate();
        }
    }

    /**
     * 获取棋子间距
     *
     * @return
     */
    public int getStoneSpace() {
        return mStoneSpace;
    }

    /**
     * 设置是否绘制棋子手数
     *
     * @param drawNumber
     */
    public void setDrawNumber(boolean drawNumber) {
        if (mIsDrawNumber != drawNumber) {
            mIsDrawNumber = drawNumber;
            for (StoneView view : mStoneViewMap.values()) {
                view.setDrawNumber(mIsDrawNumber);
            }
        }
    }

    /**
     * 获取是否绘制棋子手数
     *
     * @return
     */
    public boolean isDrawNumber() {
        return mIsDrawNumber;
    }

    /**
     * 设置格子大小
     *
     * @param squareSize
     */
    public void setSquareSize(int squareSize) {
        if (mSquareSize != squareSize) {
            mSquareSize = squareSize;
            invalidate();
        }
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
     * 设置是否显示坐标
     *
     * @param isShowCoordinate
     */
    public void setShowCoordinate(boolean isShowCoordinate) {
        if (mIsShowCoordinate != isShowCoordinate) {
            mIsShowCoordinate = isShowCoordinate;
            invalidate();
        }
    }

    /**
     * 获取是否显示坐标
     *
     * @return
     */
    public boolean isShowCoordinate() {
        return mIsShowCoordinate;
    }

    /**
     * 设置高亮棋子
     *
     * @param stone
     */
    public void setHighlightStone(Stone stone) {
        for (StoneView view : mStoneViewMap.values()) {
            if (view.getStone().equals(stone)) {
                view.setHighlight(true);
            } else {
                view.setHighlight(false);
            }
        }
    }

    /**
     * 获取高亮的棋子
     *
     * @return
     */
    public StoneView getHighlightStone() {
        for (StoneView view : mStoneViewMap.values()) {
            if (view.isHighlight()) {
                return view;
            }
        }
        return null;
    }

    /**
     * 设置高亮的交叉点
     *
     * @param intersection
     */
    public void setHighlightIntersection(Intersection intersection) {
        mHighlightIntersection = intersection;
        invalidate();
    }

    /**
     * 获取高亮的交叉点
     *
     * @return
     */
    public Intersection getHighlightIntersection() {
        return mHighlightIntersection;
    }

    /**
     * 添加棋子
     *
     * @param stone
     * @return
     */
    public StoneView addStone(Stone stone) {
        if (!mStoneViewMap.containsKey(stone)) {
            LayoutParams params = new LayoutParams(mSquareSize, mSquareSize);
            StoneView stoneView = new StoneView(getContext());
            stoneView.setStone(stone);
            stoneView.setStoneBitmap(stone.color == StoneColor.BLACK ? mBlackStoneBitmap : mWhiteStoneBitmap);
            stoneView.setDrawNumber(mIsDrawNumber);
            stoneView.setStoneSpace(mStoneSpace);
            params.leftMargin = Math.round((stone.intersection.x + 0.5f) * mSquareSize);
            params.topMargin = Math.round((stone.intersection.y + 0.5f) * mSquareSize);
            stoneView.setLayoutParams(params);
            addView(stoneView);

            mStoneViewMap.put(stone, stoneView);
        }
        return mStoneViewMap.get(stone);
    }

    /**
     * 删除棋子
     *
     * @param stone
     * @return
     */
    public StoneView removeStone(Stone stone) {
        return removeStone(stone, false);
    }

    /**
     * 删除棋子
     *
     * @param stone
     * @param animate
     * @return
     */
    public StoneView removeStone(Stone stone, boolean animate) {
        StoneView stoneView = mStoneViewMap.get(stone);
        if (stoneView != null) {
            mStoneViewMap.remove(stone);
            if (animate) {
                // 棋子消失动画
                stoneView.animate().alpha(0f);
                stoneView.animate().setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        removeView(stoneView);
                    }
                });
            } else {
                removeView(stoneView);
            }
        }
        return stoneView;
    }

    /**
     * 根据传入的坐标查找最近的交叉点
     *
     * @param x
     * @param y
     * @return
     */
    public Intersection getNearestIntersection(float x, float y) {
        int col = Math.round(x / mSquareSize) - 1;
        int row = Math.round(y / mSquareSize) - 1;
        if (col < 0 || col >= mBoardSize
                || row < 0 || row >= mBoardSize) {
            return null;
        }
        return new Intersection(col, row);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制棋盘
        drawBoard(canvas);

        // 绘制棋子阴影
        drawStoneShadow(canvas);

        // 绘制高亮交叉点
        drawHighlightIntersection(canvas);
    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int coordinateTextSize = mSquareSize / 2;
        mBoardPaint.setTextSize(coordinateTextSize);
        mBoardPaint.setStyle(Paint.Style.FILL);

        if (mBoardBitmap != null && !mBoardBitmap.isRecycled()) {
            canvas.drawBitmap(mBoardBitmap,
                    new Rect(0, 0, mBoardBitmap.getWidth(), mBoardBitmap.getHeight()),
                    new Rect(0, 0, getWidth(), getHeight()),
                    mBoardPaint);
        } else {
            mBoardPaint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, getWidth(), getHeight(), mBoardPaint);
        }

        mBoardPaint.setColor(Color.BLACK);

        canvas.translate(mSquareSize, mSquareSize);

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
                canvas.drawText(abscissa, i * mSquareSize - width / 2, -coordinateTextSize / 2, mBoardPaint);
            }
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
                canvas.drawText(abscissa, i * mSquareSize - width / 2, mBoardSize * mSquareSize - coordinateTextSize / 2, mBoardPaint);
            }

            // 纵坐标
            for (int i = 0; i < mBoardSize; i++) {
                String ordinate = "" + (i + 1);
                float width = mBoardPaint.measureText(ordinate);
                canvas.drawText(ordinate, -mSquareSize / 2 - width / 2,
                        (mBoardSize - i - 1) * mSquareSize + coordinateTextSize / 2, mBoardPaint);
            }
            for (int i = 0; i < mBoardSize; i++) {
                String ordinate = "" + (i + 1);
                float width = mBoardPaint.measureText(ordinate);
                canvas.drawText(ordinate, mBoardSize * mSquareSize - mSquareSize / 2 - width / 2,
                        (mBoardSize - i - 1) * mSquareSize + coordinateTextSize / 2, mBoardPaint);
            }
        }

        // 2，绘制棋盘线
        for (int i = 0; i < mBoardSize; i++) {
            if (i == 0 || i == mBoardSize - 1) {
                mBoardPaint.setStrokeWidth(3.0f);
            } else {
                mBoardPaint.setStrokeWidth(1.5f);
            }
            canvas.drawLine(0, i * mSquareSize, mSquareSize * (mBoardSize - 1),
                    i * mSquareSize, mBoardPaint);
            canvas.drawLine(i * mSquareSize, 0, i * mSquareSize,
                    mSquareSize * (mBoardSize - 1), mBoardPaint);
        }

        // 3，绘制星位
        for (int i = 0; i < mBoardSize; i++) {
            for (int j = 0; j < mBoardSize; j++) {
                switch (mBoardSize) {
                    case 9:
                        if ((i == 2 || i == 6) && (j == 2 || j == 6) || (i == 4 && j == 4)) {
                            canvas.drawCircle(i * mSquareSize, j * mSquareSize, mSquareSize / 8, mBoardPaint);
                        }
                        break;
                    case 13:
                        if ((i == 3 || i == 9) && (j == 3 || j == 9) || (i == 6 && j == 6)) {
                            canvas.drawCircle(i * mSquareSize, j * mSquareSize, mSquareSize / 8, mBoardPaint);
                        }
                        break;
                    case 19:
                        if ((i == 3 || i == 9 || i == 15) && (j == 3 || j == 9 || j == 15)) {
                            canvas.drawCircle(i * mSquareSize, j * mSquareSize, mSquareSize / 8, mBoardPaint);
                        }
                        break;
                }
            }
        }

        canvas.translate(-mSquareSize, -mSquareSize);
    }

    /**
     * 绘制棋子阴影
     *
     * @param canvas
     */
    private void drawStoneShadow(Canvas canvas) {
        for (StoneView view : mStoneViewMap.values()) {
            Bitmap shadowBitmap = null;
            if (view.getStone().color == StoneColor.BLACK) {
                if (mBlackStoneShadowBitmap != null && !mBlackStoneShadowBitmap.isRecycled()) {
                    shadowBitmap = mBlackStoneShadowBitmap;
                }
            } else {
                if (mWhiteStoneShadowBitmap != null && !mWhiteStoneShadowBitmap.isRecycled()) {
                    shadowBitmap = mWhiteStoneShadowBitmap;
                }
            }
            if (shadowBitmap != null && !shadowBitmap.isRecycled()) {
                int shadowOffsetX = Math.round((mSquareSize - mStoneSpace) / 3f);
                int shadowOffsetY = Math.round((mSquareSize - mStoneSpace) / 3f);
                canvas.drawBitmap(shadowBitmap,
                        new Rect(0, 0, shadowBitmap.getWidth(), shadowBitmap.getHeight()),
                        new Rect(view.getLeft() - Math.round(shadowOffsetX * 2 / 5f),
                                view.getTop() - Math.round(shadowOffsetY / 5f),
                                view.getRight() + Math.round(shadowOffsetX * 3 / 5f),
                                view.getBottom() + Math.round(shadowOffsetY * 4 / 5f)),
                        mBoardPaint);
            }
        }
    }

    /**
     * 绘制高亮交叉点
     *
     * @param canvas
     */
    private void drawHighlightIntersection(Canvas canvas) {
        if (mHighlightIntersection != null) {
            mBoardPaint.setColor(Color.RED);
            mBoardPaint.setStrokeWidth(3);
            mBoardPaint.setStyle(Paint.Style.STROKE);

            canvas.translate(mSquareSize, mSquareSize);

            int left = Math.round((mHighlightIntersection.x - 0.5f) * mSquareSize);
            int top = Math.round((mHighlightIntersection.y - 0.5f) * mSquareSize);
            int right = Math.round((mHighlightIntersection.x + 0.5f) * mSquareSize);
            int bottom = Math.round((mHighlightIntersection.y + 0.5f) * mSquareSize);
            canvas.drawRect(left, top, right, bottom, mBoardPaint);

            canvas.translate(-mSquareSize, -mSquareSize);
        }
    }
}
