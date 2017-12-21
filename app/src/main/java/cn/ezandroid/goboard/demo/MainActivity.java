package cn.ezandroid.goboard.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private Game mGame;

    private StoneColor mLastColor = StoneColor.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new Game(19);

        mBoardView = findViewById(R.id.board);
        mBoardView.setBoardSize(19);
        mBoardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intersection intersection = mBoardView.getNearestIntersection(event.getX(), event.getY());
                if (intersection != null && !mGame.taken(intersection)) {
                    putStone(intersection, mLastColor);
                }
                return false;
            }
        });
    }

    private void putStone(Intersection intersection, StoneColor color) {
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
            mLastColor = mLastColor.getOther();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
