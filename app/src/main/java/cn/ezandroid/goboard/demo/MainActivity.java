package cn.ezandroid.goboard.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import cn.ezandroid.goboard.BoardView;
import cn.ezandroid.goboard.Chain;
import cn.ezandroid.goboard.Game;
import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;

public class MainActivity extends AppCompatActivity {

    private BoardView mBoardView;
    private int mBoardSize = 19;

    private Game mGame;

    private Roc57Policy mRoc57Policy;
    private FeatureBoard mFeatureBoard;

    private StoneColor mCurrentColor = StoneColor.BLACK;

    private boolean mIsThinking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new Game(mBoardSize);

        mRoc57Policy = new Roc57Policy(this);
        mFeatureBoard = new FeatureBoard();

        mBoardView = findViewById(R.id.board);
        mBoardView.setBoardSize(mBoardSize);
        mBoardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intersection intersection = mBoardView.getNearestIntersection(event.getX(), event.getY());
                if (intersection != null && !mGame.taken(intersection)) {
                    putStone(intersection, mCurrentColor, true);
                }
                return false;
            }
        });
    }

    private void putStone(Intersection intersection, StoneColor color, boolean user) {
        if (mIsThinking) {
            return;
        }
        Set<Chain> captured = new HashSet<>();
        Stone stone = new Stone();
        stone.color = color;
        stone.intersection = intersection;
        boolean add = mGame.addStone(stone, captured);
        if (add) {
            for (Chain chain : captured) {
                for (Stone stone1 : chain.getStones()) {
                    mBoardView.removeStone(stone1);
                }
            }
            mBoardView.addStone(stone);
            mFeatureBoard.playMove(intersection.x, intersection.y,
                    mCurrentColor == StoneColor.BLACK ? FeatureBoard.BLACK : FeatureBoard.WHITE);
            mCurrentColor = mCurrentColor.getOther();

            if (user) {
                mIsThinking = true;
                new Thread() {
                    public void run() {
                        long time = System.currentTimeMillis();

                        byte[][] features = mFeatureBoard.generateFeatures48(); // 生成策略网络需要的特征数组
                        float[][] policies = mRoc57Policy.getOutput(new byte[][][]{features}); // 使用策略网络生成落子几率数组
                        Debug.printRate(policies[0]);

                        float maxRate = -1;
                        int maxPos = 0;
                        for (int i = 0; i < policies[0].length; i++) {
                            if (policies[0][i] > maxRate) {
                                maxRate = policies[0][i];
                                maxPos = i;
                            }
                        }

                        mIsThinking = false;

                        final int pos = maxPos;
                        Log.e("MainActivity", "Policy"
                                + " Choose:(" + pos % 19 + "," + pos / 19 + ")"
                                + " Rate:" + maxRate
                                + " UseTime:" + (System.currentTimeMillis() - time));
                        runOnUiThread(() -> putStone(new Intersection(pos % 19, pos / 19), mCurrentColor, false));
                    }
                }.start();
            }
        }
    }
}
