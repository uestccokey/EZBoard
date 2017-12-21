package cn.ezandroid.goboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private boolean mIsShowCoordinate = true; // 是否显示坐标

    private Map<Stone, StoneView> mStoneViewMap = new HashMap<>(); // StoneView映射图，用来根据Stone快速查找对应的StoneView

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

        mSquareSize = Math.round(minSize / (mBoardSize + 1));
    }

    private void initBoard() {
        this.mBoardPaint = new Paint();
        this.mBoardPaint.setAntiAlias(true);
        this.mBoardPaint.setStyle(Paint.Style.FILL);

        setWillNotDraw(false);
    }

    /**
     * 重置棋盘
     */
    public void reset() {
        mStoneViewMap.clear();
        removeAllViews();
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

    /**
     * 添加棋子
     *
     * @param stone
     */
    public void addStone(Stone stone) {
        for (StoneView view : mStoneViewMap.values()) {
            view.setHighlight(false);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mSquareSize, mSquareSize);
        StoneView stoneView = new StoneView(getContext());
        stoneView.setStone(stone);
        params.leftMargin = Math.round((stone.intersection.x + 0.5f) * mSquareSize);
        params.topMargin = Math.round((stone.intersection.y + 0.5f) * mSquareSize);
        stoneView.setLayoutParams(params);
        stoneView.setHighlight(true);
        addView(stoneView);

        mStoneViewMap.put(stone, stoneView);
    }

    /**
     * 删除棋子
     *
     * @param stone
     */
    public void removeStone(Stone stone) {
        StoneView stoneView = mStoneViewMap.get(stone);
        if (stoneView != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(stoneView, "alpha", stoneView.getAlpha(), 0f);
            animator.setDuration(200);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    removeView(stoneView);
                    mStoneViewMap.remove(stone);
                }
            });
            animator.start();
        }
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
        int coordinateTextSize = mSquareSize / 2;

        mBoardPaint.setColor(Color.BLACK);
        mBoardPaint.setTextSize(coordinateTextSize);

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
            // 纵坐标
            for (int i = 0; i < mBoardSize; i++) {
                String ordinate = "" + (i + 1);
                float width = mBoardPaint.measureText(ordinate);
                canvas.drawText(ordinate, -mSquareSize / 2 - width / 2, (mBoardSize - i - 1) * mSquareSize + coordinateTextSize / 2, mBoardPaint);
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
                            canvas.drawCircle(i * mSquareSize, j * mSquareSize, 8, mBoardPaint);
                        }
                        break;
                    case 13:
                        if ((i == 3 || i == 9) && (j == 3 || j == 9) || (i == 6 && j == 6)) {
                            canvas.drawCircle(i * mSquareSize, j * mSquareSize, 8, mBoardPaint);
                        }
                        break;
                    case 19:
                        if ((i == 3 || i == 9 || i == 15) && (j == 3 || j == 9 || j == 15)) {
                            canvas.drawCircle(i * mSquareSize, j * mSquareSize, 8, mBoardPaint);
                        }
                        break;
                }
            }
        }

        canvas.translate(-mSquareSize, -mSquareSize);
    }
}
