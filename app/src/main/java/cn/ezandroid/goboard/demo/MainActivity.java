package cn.ezandroid.goboard.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import cn.ezandroid.game.board.common.GameContext;
import cn.ezandroid.game.board.common.board.BoardPosition;
import cn.ezandroid.game.board.common.board.GamePiece;
import cn.ezandroid.game.board.common.geometry.IntLocation;
import cn.ezandroid.game.board.go.GoBoard;
import cn.ezandroid.game.board.go.analysis.BoardEvaluator;
import cn.ezandroid.game.board.go.elements.position.GoBoardPosition;
import cn.ezandroid.game.board.go.elements.position.GoStone;
import cn.ezandroid.game.board.go.move.GoMove;
import cn.ezandroid.game.board.go.update.DeadStoneUpdater;
import cn.ezandroid.goboard.BoardView;
import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;
import cn.ezandroid.goboard.demo.core.Chain;
import cn.ezandroid.goboard.demo.core.Move;
import cn.ezandroid.goboard.demo.network.AQ211Policy;
import cn.ezandroid.goboard.demo.network.AQ211Value;
import cn.ezandroid.goboard.demo.network.FeatureBoard;
import cn.ezandroid.goboard.demo.network.IValueNetwork;
import cn.ezandroid.goboard.demo.network.Roc57Policy;
import cn.ezandroid.goboard.demo.player.AIPlayer;
import cn.ezandroid.goboard.demo.player.HumanPlayer;
import cn.ezandroid.goboard.demo.util.Debug;
import cn.ezandroid.goboard.demo.util.TerrainAnalyze;
import cn.ezandroid.goboard.demo.view.HeatMapView;
import cn.ezandroid.goboard.demo.view.TerrainMapView;

public class MainActivity extends AppCompatActivity {

    private BoardView mBoardView;
    private HeatMapView mHeatMapView;
    private TerrainMapView mTerrainMapView;

    private Button mCreateButton;
    private Button mUndoButton;
    private Button mPassButton;
    private Button mResignButton;
    private Button mScoreButton;
    private Button mHintButton;
    private Button mShareButton;

    private TextView mDetailView;

    private IValueNetwork mValueNetwork;
    private FeatureBoard mFeatureBoard;

    private StoneColor mCurrentColor = StoneColor.BLACK;

    private boolean mIsThinking = false;

    private GoBoard mGoBoard;
    private BoardEvaluator mBoardEvaluator;
    private DeadStoneUpdater mDeadStoneUpdater;

    private int mBlackScore;
    private int mWhiteScore;
    private float mBlackWinRatio = 0.5f;

    private HumanPlayer mPlayer1;
    //    private AIPlayer mPlayer1;
    private AIPlayer mPlayer2;

    private AQ211Policy mAQ211Policy;
    private Roc57Policy mRoc57Policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFeatureBoard = new FeatureBoard();
        mValueNetwork = new AQ211Value(this);
        mAQ211Policy = new AQ211Policy(this);
        mRoc57Policy = new Roc57Policy(this);

        mPlayer1 = new HumanPlayer();
//        mPlayer1 = new AIPlayer(mRoc57Policy, mValueNetwork);
        mPlayer2 = new AIPlayer(mAQ211Policy, mValueNetwork);

        mBoardView = findViewById(R.id.board);
        mBoardView.setOnTouchListener((v, event) -> {
            Intersection intersection = mBoardView.getNearestIntersection(event.getX(), event.getY());
            if (intersection != null) {
                Intersection highlight = mBoardView.getHighlightIntersection();
                if (intersection.equals(highlight) && !mFeatureBoard.taken(intersection)) {
                    mPlayer1.setIntersection(intersection);
                    putStone(mPlayer1.genMove(true), true);
                } else {
                    mBoardView.setHighlightIntersection(intersection);
                }
            }
            return false;
        });

        mHeatMapView = findViewById(R.id.heat_map);

        mTerrainMapView = findViewById(R.id.terrain_map);

        mDetailView = findViewById(R.id.detail);

        mCreateButton = findViewById(R.id.create);
        mCreateButton.setOnClickListener(v -> create());
        mUndoButton = findViewById(R.id.undo);
        mUndoButton.setOnClickListener(v -> undo());
        mPassButton = findViewById(R.id.pass);
        mPassButton.setOnClickListener(v -> pass());
        mResignButton = findViewById(R.id.resign);
        mResignButton.setOnClickListener(v -> resign());
        mScoreButton = findViewById(R.id.score);
        mScoreButton.setOnClickListener(v -> score());
        mHintButton = findViewById(R.id.hint);
        mHintButton.setOnClickListener(v -> hint());
        mShareButton = findViewById(R.id.share);
        mShareButton.setOnClickListener(v -> share());

        GameContext.setDebugMode(1);
        mGoBoard = new GoBoard(19, 0);
        mBoardEvaluator = new BoardEvaluator(mGoBoard);
        mDeadStoneUpdater = new DeadStoneUpdater(mGoBoard);
    }

    private void create() {
        if (mIsThinking) {
            return;
        }

        mFeatureBoard.reset();
        mGoBoard.reset();
        mBoardView.reset();

        mTerrainMapView.setTerrainMap(null);
        mHeatMapView.setHeatMap(null);

        mBlackScore = 0;
        mWhiteScore = 0;
        mBlackWinRatio = 0.5f;

        updateDetail();

//        botVSBot();
    }

    private void undo() {
        if (mIsThinking) {
            return;
        }
        mTerrainMapView.setTerrainMap(null);
        mHeatMapView.setHeatMap(null);

        Pair<Move, Chain> pair = mFeatureBoard.undo();
        mGoBoard.undoMove();

        if (pair != null) {
            mBoardView.setHighlightIntersection(null);

            Move move = pair.first;
            Set<Chain> captured = move.getCaptured();

            mBoardView.removeStone(move.getStone());
            for (Chain chain : captured) {
                for (Stone stone : chain.getStones()) {
                    mBoardView.addStone(stone);
                }
            }

            // 双重撤销
            Pair<Move, Chain> pair2 = mFeatureBoard.undo();
            mGoBoard.undoMove();

            if (pair2 != null) {
                Move move2 = pair2.first;
                Set<Chain> captured2 = move2.getCaptured();

                mBoardView.removeStone(move2.getStone());
                for (Chain chain : captured2) {
                    for (Stone stone : chain.getStones()) {
                        mBoardView.addStone(stone);
                    }
                }
            }

            Move latest = mFeatureBoard.getCurrentMove();
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

    private void score() {
        if (mIsThinking) {
            return;
        }
        mHeatMapView.setHeatMap(null);

        mBoardEvaluator.updateTerritoryAtEndOfGame();
        mDeadStoneUpdater.determineDeadStones();

        LinkedList<GoBoardPosition> blackDeads = mDeadStoneUpdater.getDeadStonesOnBoard(true);
        LinkedList<GoBoardPosition> whiteDeads = mDeadStoneUpdater.getDeadStonesOnBoard(false);

        int[][] board = new int[19][19];
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                BoardPosition position = mGoBoard.getPosition(i + 1, j + 1);
                if (position != null) {
                    GamePiece piece = position.getPiece();
                    if (piece != null) {
                        if (piece.isOwnedByPlayer1() && !blackDeads.contains(position)) {
                            board[i][j] = 1;
                        } else if (!piece.isOwnedByPlayer1() && !whiteDeads.contains(position)) {
                            board[i][j] = -1;
                        }
                    }
                }
            }
        }
//        int[] boardP = new int[mBoardSize * mBoardSize];
//        for (int i = 0; i < mBoardSize; i++) {
//            System.arraycopy(board[i], 0, boardP, i * 19, mBoardSize);
//        }
//        printBoard(boardP);

        float[][] state = TerrainAnalyze.terrainAnalyze(board);
        int blackScore = 0;
        int whiteScore = 0;
        float[] rate = new float[19 * 19];
        for (int i = 0; i < 19; i++) {
            System.arraycopy(state[i], 0, rate, i * 19, 19);
        }
        printRate(rate);

        for (float aRate : rate) {
            int r = Math.round(aRate * 100);
            if (r < -25) {
                whiteScore++;
            } else if (r > 25) {
                blackScore++;
            }
        }
        mBlackScore = blackScore;
        mWhiteScore = whiteScore;

        mTerrainMapView.setTerrainMap(rate);

        updateDetail();
    }

    private void hint() {
        if (mIsThinking) {
            return;
        }
        mTerrainMapView.setTerrainMap(null);

        float[][] policies = mPlayer2.getPolicies();

        float maxRate = -1;
        for (int i = 0; i < policies[0].length; i++) {
            if (policies[0][i] > maxRate) {
                maxRate = policies[0][i];
            }
        }

        maxRate = (float) Math.sqrt(maxRate);
        for (int i = 0; i < policies[0].length; i++) {
            policies[0][i] = (float) Math.sqrt(policies[0][i]) / maxRate;
        }
        Debug.printRate(policies[0]);

        mHeatMapView.setHeatMap(policies[0]);
    }

    private void share() {
        mBoardView.setDrawingCacheEnabled(true);
        mBoardView.buildDrawingCache();

        saveJPG(mBoardView.getDrawingCache());

        mBoardView.destroyDrawingCache();
        mBoardView.setDrawingCacheEnabled(false);
    }

    private void saveJPG(Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/board.jpg";
        File saveFile = new File(path);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(saveFile));
            Bitmap.CompressFormat format = path.endsWith(".png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
            bitmap.compress(format, 100, bos);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveGIF() {
        // TODO
    }

    private void printBoard(int[] board) {
        System.err.println("Board:");
        System.err.print("|-");
        for (int i = 0; i < 19; i++) {
            System.err.print("--");
        }
        System.err.print("|");
        System.err.println();
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 21; j++) {
                if (j == 0 || j == 20) {
                    System.err.print("| ");
                } else {
                    int player = board[i * 19 + (j - 1)];
                    if (player == FeatureBoard.BLACK) {
                        System.err.print("B");
                    } else if (player == FeatureBoard.WHITE) {
                        System.err.print("W");
                    } else {
                        System.err.print("+");
                    }
                    System.err.print(" ");
                }
            }
            System.err.println();
        }
        System.err.print("|-");
        for (int i = 0; i < 19; i++) {
            System.err.print("--");
        }
        System.err.print("|");
        System.err.println();
    }

    private void printRate(float[] rate) {
        System.err.println("Rate:");
        System.err.print("|");
        for (int i = 0; i < 19; i++) {
            System.err.print("----");
        }
        System.err.print("|");
        System.err.println();
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 21; j++) {
                if (j == 0 || j == 20) {
                    System.err.print("|");
                } else {
                    int value = Math.round(rate[i * 19 + (j - 1)] * 1000 / 10f);
                    if (value < 0) {
                        if (value <= -99) {
                            System.err.print("-99");
                        } else if (value > -10) {
                            System.err.print(" " + value);
                        } else {
                            System.err.print(value);
                        }
                    } else {
                        if (value >= 99) {
                            System.err.print(" 99");
                        } else if (value < 10) {
                            System.err.print("  " + value);
                        } else {
                            System.err.print(" " + value);
                        }
                    }
                    System.err.print(" ");
                }
            }
            System.err.println();
        }
        System.err.print("|");
        for (int i = 0; i < 19; i++) {
            System.err.print("----");
        }
        System.err.print("|");
        System.err.println();
    }
//
//    private static final int PLAYER1_MOVE = 1;
//    private static final int PLAYER2_MOVE = 2;
//
//    private Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case PLAYER1_MOVE:
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            mPlayer1.setFeatureBoard(mFeatureBoard);
//                            Stone player1Stone = mPlayer1.genMove(true);
//                            runOnUiThread(() -> {
//                                putStone(player1Stone);
//                                if (isEnd()) {
//                                    printEnd();
//                                } else {
//                                    mHandler.sendEmptyMessage(PLAYER2_MOVE);
//                                }
//                            });
//                        }
//                    }.start();
//                    break;
//                case PLAYER2_MOVE:
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            mPlayer2.setFeatureBoard(mFeatureBoard);
//                            Stone player2Stone = mPlayer2.genMove(false);
//
//                            float[] values = mValueNetwork.getOutput(mFeatureBoard, AQ211Value.WHITE);
//                            mBlackWinRatio = (1 - values[0]) / 2;
//
//                            runOnUiThread(() -> {
//                                updateDetail();
//
//                                putStone(player2Stone);
//                                if (isEnd()) {
//                                    printEnd();
//                                } else {
//                                    mHandler.sendEmptyMessage(PLAYER1_MOVE);
//                                }
//                            });
//                        }
//                    }.start();
//                    break;
//            }
//            return false;
//        }
//    });
//
//    private boolean isEnd() {
//        int number = mFeatureBoard.getCurrentMoveNumber();
//        if (mBlackWinRatio < 0.05 || mBlackWinRatio > 0.95 || number > 361 * 2) {
//            return true;
//        }
//        return false;
//    }
//
//    private void printEnd() {
//        if (mBlackWinRatio < 0.5) {
//            Log.e("MainActivity", "白胜");
//        } else if (mBlackWinRatio > 0.5) {
//            Log.e("MainActivity", "黑胜");
//        } else {
//            Log.e("MainActivity", "平局");
//        }
//    }
//
//    private void botVSBot() {
//        mHandler.sendEmptyMessage(PLAYER1_MOVE);
//    }
//
//    private void putStone(Stone stone) {
//        Intersection intersection = stone.intersection;
//
//        mTerrainMapView.setTerrainMap(null);
//        mHeatMapView.setHeatMap(null);
//
//        Set<Chain> captured = new HashSet<>();
//        boolean add = mFeatureBoard.playMove(intersection.x, intersection.y,
//                stone.color == StoneColor.BLACK ? FeatureBoard.BLACK : FeatureBoard.WHITE, captured);
//        if (add) {
//            stone.number = mFeatureBoard.getCurrentMoveNumber();
//
//            GoMove goMove = new GoMove(new IntLocation(intersection.y + 1, intersection.x + 1)
//                    , 0, new GoStone(stone.color == StoneColor.BLACK));
//            mGoBoard.makeMove(goMove);
////            Log.e("MainActivity", mGoBoard.getGroups().toString());
//
//            mBoardView.setHighlightIntersection(null);
//            for (Chain chain : captured) {
//                for (Stone stone1 : chain.getStones()) {
//                    mBoardView.removeStone(stone1);
//                }
//            }
//            mBoardView.addStone(stone);
//            mBoardView.setHighlightStone(stone);
//
//            mCurrentColor = mCurrentColor.getOther();
//        }
//    }

    private void putStone(Stone stone, boolean user) {
        if (mIsThinking) {
            return;
        }

        Intersection intersection = stone.intersection;

        mTerrainMapView.setTerrainMap(null);
        mHeatMapView.setHeatMap(null);

        Set<Chain> captured = new HashSet<>();
        boolean add = mFeatureBoard.playMove(intersection.x, intersection.y,
                stone.color == StoneColor.BLACK ? FeatureBoard.BLACK : FeatureBoard.WHITE, captured);
        if (add) {
            stone.number = mFeatureBoard.getCurrentMoveNumber();

            GoMove goMove = new GoMove(new IntLocation(intersection.y + 1, intersection.x + 1)
                    , 0, new GoStone(stone.color == StoneColor.BLACK));
            mGoBoard.makeMove(goMove);
//            Log.e("MainActivity", mGoBoard.getGroups().toString());

            mBoardView.setHighlightIntersection(null);
            for (Chain chain : captured) {
                for (Stone stone1 : chain.getStones()) {
                    mBoardView.removeStone(stone1);
                }
            }
            mBoardView.addStone(stone);
            mBoardView.setHighlightStone(stone);

            mCurrentColor = mCurrentColor.getOther();

            if (user) {
                mIsThinking = true;

                new Thread() {
                    public void run() {
                        float[] values = mValueNetwork.getOutput(mFeatureBoard,
                                mCurrentColor == StoneColor.BLACK ? AQ211Value.BLACK : AQ211Value.WHITE);

                        mBlackWinRatio = (1 - values[0]) / 2;

                        runOnUiThread(() -> {
                            updateDetail();

                            mIsThinking = false;

                            mPlayer2.setFeatureBoard(mFeatureBoard);
                            putStone(mPlayer2.genMove(false), false);
                        });
                    }
                }.start();
            }
        }
    }

    private void updateDetail() {
        mDetailView.setText("形势 黑:" + mBlackScore + " 白:" + mWhiteScore + "+7.5"
                + " 黑胜率:" + mBlackWinRatio);
    }
}
