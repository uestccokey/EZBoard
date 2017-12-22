package cn.ezandroid.goboard.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.HashSet;
import java.util.Set;

import cn.ezandroid.goboard.BoardView;
import cn.ezandroid.goboard.Chain;
import cn.ezandroid.goboard.Game;
import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Move;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;

public class MainActivity extends AppCompatActivity {

    private BoardView mBoardView;
    private Button mCreateButton;
    private Button mUndoButton;
    private Button mPassButton;
    private Button mResignButton;
    private int mBoardSize = 19;

    private Game mGame;

    private Roc57Policy mRoc57Policy;
    private AQValue mAQValue;
    private FeatureBoard mFeatureBoard;

    private StoneColor mCurrentColor = StoneColor.BLACK;

    private boolean mIsThinking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new Game(mBoardSize);

        mRoc57Policy = new Roc57Policy(this);
        mAQValue = new AQValue(this);
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

        mCreateButton = findViewById(R.id.create);
        mCreateButton.setOnClickListener(v -> create());
        mUndoButton = findViewById(R.id.undo);
        mUndoButton.setOnClickListener(v -> undo());
        mPassButton = findViewById(R.id.pass);
        mPassButton.setOnClickListener(v -> pass());
        mResignButton = findViewById(R.id.resign);
        mResignButton.setOnClickListener(v -> resign());
    }

    private void create() {
        if (mIsThinking) {
            return;
        }
    }

    private void undo() {
        if (mIsThinking) {
            return;
        }
        Pair<Move, Chain> pair = mGame.undo();

        if (pair != null) {
            Move move = pair.first;
            Set<Chain> captured = move.getCaptured();

            mBoardView.removeStone(move.getStone());
            for (Chain chain : captured) {
                for (Stone stone : chain.getStones()) {
                    mBoardView.addStone(stone);
                }
            }
            mFeatureBoard.undo();

            // 双重撤销
            Pair<Move, Chain> pair2 = mGame.undo();
            if (pair2 != null) {
                Move move2 = pair2.first;
                Set<Chain> captured2 = move2.getCaptured();

                mBoardView.removeStone(move2.getStone());
                for (Chain chain : captured2) {
                    for (Stone stone : chain.getStones()) {
                        mBoardView.addStone(stone);
                    }
                }
                mFeatureBoard.undo();
            }

            Move latest = mGame.getHistory().readLatest();
            if (latest != null) {
                mBoardView.setHighlightStone(latest.getStone());
            }
        }
    }

    private void pass() {
        if (mIsThinking) {
            return;
        }
    }

    private void resign() {
        if (mIsThinking) {
            return;
        }
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
            mBoardView.setHighlightStone(stone);

            if (user) {
                mIsThinking = true;
                new Thread() {
                    public void run() {
                        long time = System.currentTimeMillis();

                        byte[][] feature49 = mFeatureBoard.generateFeatures49(); // 生成价值网络需要的特征数组
                        float[] values = mAQValue.getOutput(new byte[][][]{feature49},
                                mCurrentColor == StoneColor.BLACK ? 1 : 0); // 使用价值网络生成当前局面胜率

                        byte[][] features48 = mFeatureBoard.generateFeatures48(); // 生成策略网络需要的特征数组
                        float[][] policies = mRoc57Policy.getOutput(new byte[][][]{features48}); // 使用策略网络生成落子几率数组
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
                        Log.e("MainActivity", "Value Rate:" + (1 - values[0]) / 2);
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
