package cn.ezandroid.goboard.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.ezandroid.goboard.BoardView;

public class MainActivity extends AppCompatActivity {

    private BoardView mBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardView = findViewById(R.id.board);
    }
}
