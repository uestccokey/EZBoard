package cn.ezandroid.goboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 棋子显示控件
 *
 * @author like
 * @date 2017-12-21
 */
public class StoneView extends ImageView {

    private Paint mStonePaint;

    private Stone mStone;

    private int mStoneSpace = 4; // 棋子间距

    private boolean mIsHighlight; // 棋子是否高亮

    public StoneView(Context context) {
        super(context);
        initStone();
    }

    public StoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStone();
    }

    private void initStone() {
        this.mStonePaint = new Paint();
        this.mStonePaint.setAntiAlias(true);
    }

    /**
     * 设置棋子是否高亮
     *
     * @param highlight
     */
    public void setHighlight(boolean highlight) {
        if (mIsHighlight != highlight) {
            mIsHighlight = highlight;
            invalidate();
        }
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
     * 设置棋子
     *
     * @param stone
     */
    public void setStone(Stone stone) {
        mStone = stone;
    }

    /**
     * 获取棋子
     *
     * @return
     */
    public Stone getStone() {
        return mStone;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制棋子
        drawStone(canvas);

        // 绘制高亮状态
        drawHighlight(canvas);
    }

    /**
     * 绘制棋子
     *
     * @param canvas
     */
    private void drawStone(Canvas canvas) {
        if (mStone.color == StoneColor.BLACK) {
            mStonePaint.setColor(Color.BLACK);
            mStonePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mStoneSpace, mStonePaint);
        } else {
            mStonePaint.setColor(Color.WHITE);
            mStonePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mStoneSpace, mStonePaint);

            mStonePaint.setColor(Color.BLACK);
            mStonePaint.setStyle(Paint.Style.STROKE);
            mStonePaint.setStrokeWidth(3);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mStoneSpace, mStonePaint);
        }
    }

    /**
     * 绘制高亮状态
     *
     * @param canvas
     */
    private void drawHighlight(Canvas canvas) {
        if (mIsHighlight) {
            mStonePaint.setColor(Color.RED);
            mStonePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 6, mStonePaint);
        }
    }
}
