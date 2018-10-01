package cn.ezandroid.lib.board;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import cn.ezandroid.lib.board.theme.GoTheme;
import cn.ezandroid.lib.board.theme.MonochromeTheme;

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

    private boolean mIsShowHighlightCoordinates = true;

    private boolean mIsDrawNumber = true; // 是否绘制棋子手数

    private GoTheme mGoTheme;

    private Bitmap mShadowBitmap; // 阴影图

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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMeasureSpec == 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.AT_MOST);

            widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (heightMeasureSpec == 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.AT_MOST);

            heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        }

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int min = Math.min(width, height);
            setMeasuredDimension(min, min);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(heightSpecSize, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, widthSpecSize);
        } else {
            int min = Math.min(widthSpecSize, heightSpecSize);
            setMeasuredDimension(min, min);
        }

        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        int minSize = Math.min(childWidthSize, childHeightSize);
        mSquareSize = Math.round(minSize * 1f / (mBoardSize + 1));
    }

    private void initBoard() {
        mBoardPaint = new Paint();
        mBoardPaint.setAntiAlias(true);
        mBoardPaint.setFilterBitmap(true);

        setWillNotDraw(false);

        GoTheme.DrawableCache drawableCache = new GoTheme.DrawableCache(getContext(), (int) (Runtime.getRuntime().maxMemory() / 32));
        mGoTheme = new MonochromeTheme(drawableCache); // 默认使用极简主题
        mShadowBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shadow);
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
     * 设置主题样式
     *
     * @param goTheme
     */
    public void setGoTheme(GoTheme goTheme) {
        if (mGoTheme != goTheme) {
            mGoTheme = goTheme;
            for (StoneView view : mStoneViewMap.values()) {
                if (view.getStone().color == StoneColor.BLACK) {
                    view.setStoneTheme(mGoTheme.mBlackStoneTheme);
                } else {
                    view.setStoneTheme(mGoTheme.mWhiteStoneTheme);
                }
                view.setMarkTheme(mGoTheme.mMarkTheme);
            }
            postInvalidate();
        }
    }

    /**
     * 获取主题样式
     *
     * @return
     */
    public GoTheme getGoTheme() {
        return mGoTheme;
    }

    /**
     * 设置棋子间距
     *
     * @param stoneSpace
     */
    public void setStoneSpace(int stoneSpace) {
        if (mStoneSpace != stoneSpace) {
            mStoneSpace = stoneSpace;
            postInvalidate();
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
            postInvalidate();
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
            postInvalidate();
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
            postInvalidate();
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
        postInvalidate();
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
     * 设置显示高亮坐标
     *
     * @param showHighlightCoordinates
     */
    public void setShowHighlightCoordinates(boolean showHighlightCoordinates) {
        mIsShowHighlightCoordinates = showHighlightCoordinates;
    }

    /**
     * 是否显示高亮坐标
     *
     * @return
     */
    public boolean isShowHighlightCoordinates() {
        return mIsShowHighlightCoordinates;
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
            stoneView.setStoneTheme(stone.color == StoneColor.BLACK ? mGoTheme.mBlackStoneTheme : mGoTheme.mWhiteStoneTheme);
            stoneView.setMarkTheme(mGoTheme.mMarkTheme);
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
    public void draw(Canvas canvas) {
        // 绘制棋盘
        drawBoard(canvas);

        // 绘制棋子阴影
        drawStoneShadow(canvas);

        super.draw(canvas);

        // 绘制高亮交叉点
        drawHighlightIntersection(canvas);
    }

    private void drawTopCoordinate(Canvas canvas, int i) {
        int coordinateTextSize = mSquareSize / 2;
        char c = 'A';
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

    private void drawBottomCoordinate(Canvas canvas, int i) {
        int coordinateTextSize = mSquareSize / 2;
        char c = 'A';
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

    private void drawLeftCoordinate(Canvas canvas, int i) {
        int coordinateTextSize = mSquareSize / 2;
        String ordinate = "" + (i + 1);
        float width = mBoardPaint.measureText(ordinate);
        canvas.drawText(ordinate, -mSquareSize / 2 - width / 2,
                (mBoardSize - i - 1) * mSquareSize + coordinateTextSize / 2, mBoardPaint);
    }

    private void drawRightCoordinate(Canvas canvas, int i) {
        int coordinateTextSize = mSquareSize / 2;
        String ordinate = "" + (i + 1);
        float width = mBoardPaint.measureText(ordinate);
        canvas.drawText(ordinate, mBoardSize * mSquareSize - mSquareSize / 2 - width / 2,
                (mBoardSize - i - 1) * mSquareSize + coordinateTextSize / 2, mBoardPaint);
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

        Drawable drawable = mGoTheme.mBoardTheme.getBackground();
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);

        canvas.translate(mSquareSize, mSquareSize);

        // 1，绘制棋盘坐标
        if (mIsShowCoordinate) {
            mBoardPaint.setColor(mGoTheme.mBoardTheme.getBorderColor());
            // 横坐标
            for (int i = 0; i < mBoardSize; i++) {
                drawTopCoordinate(canvas, i);
            }
            for (int i = 0; i < mBoardSize; i++) {
                drawBottomCoordinate(canvas, i);
            }

            // 纵坐标
            for (int i = 0; i < mBoardSize; i++) {
                drawLeftCoordinate(canvas, i);
            }
            for (int i = 0; i < mBoardSize; i++) {
                drawRightCoordinate(canvas, i);
            }
        }

        // 绘制高亮坐标
        if (mIsShowHighlightCoordinates && mHighlightIntersection != null) {
            mBoardPaint.setColor(Color.RED);
            // 横坐标
            drawTopCoordinate(canvas, mHighlightIntersection.x);
            drawBottomCoordinate(canvas, mHighlightIntersection.x);
            // 纵坐标
            drawLeftCoordinate(canvas, mBoardSize - mHighlightIntersection.y - 1);
            drawRightCoordinate(canvas, mBoardSize - mHighlightIntersection.y - 1);
        }

        // 2，绘制棋盘线
        for (int i = 0; i < mBoardSize; i++) {
            if (i == 0 || i == mBoardSize - 1) {
                mBoardPaint.setColor(mGoTheme.mBoardTheme.getBorderColor());
                mBoardPaint.setStrokeWidth(mGoTheme.mBoardTheme.mBorderWidth);
            } else {
                mBoardPaint.setColor(mGoTheme.mBoardTheme.getLineColor());
                mBoardPaint.setStrokeWidth(mGoTheme.mBoardTheme.mLineWidth);
            }
            canvas.drawLine(0, i * mSquareSize, mSquareSize * (mBoardSize - 1), i * mSquareSize, mBoardPaint);
            canvas.drawLine(i * mSquareSize, 0, i * mSquareSize, mSquareSize * (mBoardSize - 1), mBoardPaint);
        }

        // 3，绘制星位
        mBoardPaint.setColor(mGoTheme.mBoardTheme.getLineColor());
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
        if (!mGoTheme.mBoardTheme.mStoneShadowOn) {
            return;
        }
        for (StoneView view : mStoneViewMap.values()) {
            if (mShadowBitmap != null && !mShadowBitmap.isRecycled()) {
                int shadowOffsetX = Math.round((mSquareSize - mStoneSpace) / 3f);
                int shadowOffsetY = Math.round((mSquareSize - mStoneSpace) / 3f);
                canvas.drawBitmap(mShadowBitmap,
                        new Rect(0, 0, mShadowBitmap.getWidth(), mShadowBitmap.getHeight()),
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
