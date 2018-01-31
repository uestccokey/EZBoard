package cn.ezandroid.goboard.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.ezandroid.goboard.BoardView;
import cn.ezandroid.goboard.Intersection;

public class MainActivity extends AppCompatActivity {

    private BoardView mBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardView = findViewById(R.id.board);
        mBoardView.setOnTouchListener((v, event) -> {
            Intersection intersection = mBoardView.getNearestIntersection(event.getX(), event.getY());
            if (intersection != null) {
                mBoardView.setHighlightIntersection(intersection);
            }
            return false;
        });
    }
}
