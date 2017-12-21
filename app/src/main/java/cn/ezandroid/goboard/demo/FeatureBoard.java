package cn.ezandroid.goboard.demo;

import android.util.Pair;
import android.util.SparseArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cn.ezandroid.goboard.Chain;
import cn.ezandroid.goboard.Game;
import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Move;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;

/**
 * 特征棋盘，支持AlphaGo的48特征及AQ的49特征两种
 *
 * @author like
 * @date 2017-07-23
 */
public class FeatureBoard implements Cloneable {

    public static byte BLACK = 1;
    public static byte WHITE = -1;
    public static byte EMPTY = 0;

    /********************FeatureBoard Start*******************************/
    private static final byte BOARD = 0;
    private static final byte ONES = 1;
    private static final byte TURNS_SINCE = 2;
    private static final byte LIBERTIES = 3;
    private static final byte CAPTURE_SIZE = 4;
    private static final byte SELF_ATARI_SIZE = 5;
    private static final byte LIBERTIES_AFTER = 6;
    private static final byte LADDER_CAPTURE = 7;
    private static final byte LADDER_ESCAPE = 8;
    private static final byte SENSIBLENESS = 9;
    private static final byte ZEROS = 10;

    private static final byte[] FEATURE_COUNT = {3, 1, 8, 8, 8, 8, 8, 1, 1, 1, 1};
    private static final byte[] FEATURE_START = {0, 3, 4, 12, 20, 28, 36, 44, 45, 46, 47};

    private static final byte BOARD_49 = 0;
    private static final byte ZEROS_49 = 1;
    private static final byte ONES_49 = 2;
    private static final byte TURNS_SINCE_49 = 3;
    private static final byte LIBERTIES_49 = 4;
    private static final byte CAPTURE_SIZE_49 = 5;
    private static final byte SELF_ATARI_SIZE_49 = 6;
    private static final byte LIBERTIES_AFTER_49 = 7;
    private static final byte LADDER_CAPTURE_49 = 8;
    private static final byte LADDER_ESCAPE_49 = 9;
    private static final byte SENSIBLENESS_49 = 10;
    private static final byte FALSE_EYE_49 = 11;

    private static final byte[] FEATURE_COUNT_49 = {3, 1, 1, 8, 8, 8, 8, 8, 1, 1, 1, 1};
    private static final byte[] FEATURE_START_49 = {0, 3, 4, 5, 13, 21, 29, 37, 45, 46, 47, 48};

    private byte[] mBoardFeature;
    private byte[] mHistoryFeature;
    private byte[] mLibertyFeature;
    private byte[] mCaptureSizeFeature;
    private byte[] mSelfAtariSizeFeature;
    private byte[] mLibertyAfterFeature;
    private byte[] mLadderCaptureFeature;
    private byte[] mLadderEscapeFeature;
    /********************FeatureBoard End*******************************/

    private byte mActivePlayer;

    private final int mBoardSize;

    private static final SparseArray<List<Integer>> NEIGHBOR_CACHE = new SparseArray<>();

    private static final SparseArray<List<Integer>> DIAGONAL_CACHE = new SparseArray<>();

    static {
        for (int i = 0; i < 361; i++) {
            // 初始化邻接棋子缓存
            List<Integer> neighbors = new ArrayList<>();
            if (isOnBoard(top(i))) {
                neighbors.add(top(i));
            }
            if (isOnBoard(bottom(i))) {
                neighbors.add(bottom(i));
            }
            if (isOnBoard(left(i)) && (i % 19 != 0)) {
                neighbors.add(left(i));
            }
            if (isOnBoard(right(i)) && (i % 19 != 18)) {
                neighbors.add(right(i));
            }
            NEIGHBOR_CACHE.put(i, neighbors);

            // 初始化斜接棋子缓存
            List<Integer> diagonals = new ArrayList<>();
            if (isOnBoard(topLeft(i))) {
                diagonals.add(topLeft(i));
            }
            if (isOnBoard(bottomLeft(i))) {
                diagonals.add(bottomLeft(i));
            }
            if (isOnBoard(topRight(i)) && (i % 19 != 0)) {
                diagonals.add(topRight(i));
            }
            if (isOnBoard(bottomRight(i)) && (i % 19 != 18)) {
                diagonals.add(bottomRight(i));
            }
            DIAGONAL_CACHE.put(i, diagonals);
        }
    }

    private int mKOPos;

    private Game mGame;

    // 用于减少updateFeature的耗时
    private Set<Intersection> mShouldUpdatePos = new HashSet<>();

    public FeatureBoard() {
        mBoardSize = 361;
        mBoardFeature = new byte[361];
        mHistoryFeature = new byte[361];
        mLibertyFeature = new byte[361];
        mCaptureSizeFeature = new byte[361];
        mSelfAtariSizeFeature = new byte[361];
        mLibertyAfterFeature = new byte[361];
        mLadderCaptureFeature = new byte[361];
        mLadderEscapeFeature = new byte[361];
        mGame = new Game(19);

        for (int i = 0; i < 361; i++) {
            mSelfAtariSizeFeature[i] = -1;
            mHistoryFeature[i] = -1;
            if (i == 0 || i == 18 || i == 342 || i == 360) {
                mLibertyAfterFeature[i] = 2;
            } else if (i / 19 == 0 || i / 19 == 18 || i % 19 == 0 || i % 19 == 18) {
                mLibertyAfterFeature[i] = 3;
            } else {
                mLibertyAfterFeature[i] = 4;
            }
        }
    }

    @Override
    public FeatureBoard clone() throws CloneNotSupportedException {
        FeatureBoard featureBoard = (FeatureBoard) super.clone();
        featureBoard.mGame = mGame.clone();
        featureBoard.mBoardFeature = mBoardFeature.clone();
        featureBoard.mHistoryFeature = mHistoryFeature.clone();
        featureBoard.mLibertyFeature = mLibertyFeature.clone();
        featureBoard.mCaptureSizeFeature = mCaptureSizeFeature.clone();
        featureBoard.mSelfAtariSizeFeature = mSelfAtariSizeFeature.clone();
        featureBoard.mLibertyAfterFeature = mLibertyAfterFeature.clone();
        featureBoard.mLadderCaptureFeature = mLadderCaptureFeature.clone();
        featureBoard.mLadderEscapeFeature = mLadderEscapeFeature.clone();
        featureBoard.mShouldUpdatePos = new HashSet<>();
        for (Intersection intersection : mShouldUpdatePos) {
            featureBoard.mShouldUpdatePos.add(intersection.clone());
        }
        return featureBoard;
    }

    private boolean isSensibleness(int pos) {
        return isLegal(pos, mActivePlayer) && !isEye(pos, mActivePlayer, new Stack<Integer>());
    }

    private byte getNextPlayer(byte player) {
        return player == BLACK ? WHITE : BLACK;
    }

    private StoneColor getStoneColor(byte player) {
        return player == BLACK ? StoneColor.BLACK : StoneColor.WHITE;
    }

    private byte getPlayer(StoneColor color) {
        return color == StoneColor.BLACK ? BLACK : WHITE;
    }

    private void updateFeature() {
        updateHistoryFeature();
        try {
            Game moveManager = mGame.clone();
            for (Intersection intersection : mShouldUpdatePos) {
                int i = intersection.x + intersection.y * 19;
                if (mBoardFeature[i] == EMPTY) {
                    if (isLegal(i, mActivePlayer)) {
                        Set<Chain> captured = new HashSet<>();
                        Stone stone = new Stone();
                        stone.color = getStoneColor(mActivePlayer);
                        stone.intersection = new Intersection(i % 19, i / 19);
                        moveManager.addStone(stone, captured);

                        Chain chain = moveManager.getChain(new Intersection(i % 19, i / 19));
                        mLibertyAfterFeature[i] = (byte) chain.getLiberties().size();
                        if (!captured.isEmpty()) {
                            int temp = 0;
                            for (Chain cCapture : captured) {
                                temp += cCapture.size();
                            }
                            mCaptureSizeFeature[i] = (byte) temp;
                        } else {
                            mCaptureSizeFeature[i] = 0;
                        }
                        if (chain.getLiberties().size() == 1) {
                            mSelfAtariSizeFeature[i] = (byte) chain.getStones().size();
                        } else {
                            mSelfAtariSizeFeature[i] = -1;
                        }
                        moveManager.undo();
                    }
                } else {
                    Chain chain = mGame.getChain(new Intersection(i % 19, i / 19));
                    mLibertyFeature[i] = (byte) chain.getLiberties().size();
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public Move undo() {
        Pair<Move, Chain> pair = mGame.undo();
        if (pair != null) {
            Move move = pair.first;
            Chain chain = pair.second;
            Stone stone = move.getStone();
            int pos = stone.intersection.x + stone.intersection.y * 19;

            Intersection ko = move.getKO();
            if (ko != null) {
                mKOPos = ko.x + ko.y * 19;
            } else {
                mKOPos = -1;
            }

            mBoardFeature[pos] = EMPTY;
            mLibertyFeature[pos] = 0;

            Set<Chain> captured = move.getCaptured();

            // 当前落子所在串及周围的点加入待更新列表
            mShouldUpdatePos.addAll(chain.getLiberties());
            for (Stone s : chain.getStones()) {
                mShouldUpdatePos.add(s.intersection);
            }

            for (Chain cCapture : captured) {
                mShouldUpdatePos.addAll(cCapture.getLiberties());
                for (Stone sCapture : cCapture.getStones()) {
                    int p = sCapture.intersection.x + sCapture.intersection.y * 19;
                    mBoardFeature[p] = getPlayer(sCapture.color);
                    mLibertyAfterFeature[p] = 0;
                    mCaptureSizeFeature[p] = 0;
                    mSelfAtariSizeFeature[p] = -1;

                    // 死子串及周围的点加入待更新列表
                    mShouldUpdatePos.add(sCapture.intersection);
                }
            }

            updateFeature();

            Debug.printBoard(mBoardFeature);
            return move;
        }
        return null;
    }

    public Move redo() {
        Move move = mGame.redo();
        if (move != null) {
            Stone stone = move.getStone();
            int pos = stone.intersection.x + stone.intersection.y * 19;

            Chain chain = mGame.getChain(stone.intersection);
            Set<Chain> captured = move.getCaptured();
            if (captured.size() == 1 && chain.size() == 1 && chain.getLiberties().size() == 1) {
                Iterator<Chain> cit = captured.iterator();
                if (cit.hasNext()) {
                    Iterator<Stone> sit = cit.next().getStones().iterator();
                    if (sit.hasNext()) {
                        Stone sto = sit.next();
                        mKOPos = sto.intersection.x + sto.intersection.y * 19;
                    }
                }
            }

            // 当前落子所在串及周围的点加入待更新列表
            mShouldUpdatePos.addAll(chain.getLiberties());
            for (Stone s : chain.getStones()) {
                mShouldUpdatePos.add(s.intersection);
            }

            for (Chain cCapture : captured) {
                mShouldUpdatePos.addAll(cCapture.getLiberties());
                for (Stone sCapture : cCapture.getStones()) {
                    int p = sCapture.intersection.x + sCapture.intersection.y * 19;
                    mBoardFeature[p] = EMPTY;
                    mLibertyFeature[p] = 0;

                    // 死子串及周围的点加入待更新列表
                    mShouldUpdatePos.add(sCapture.intersection);
                }
            }

            mBoardFeature[pos] = getPlayer(stone.color);
            mLibertyAfterFeature[pos] = 0;
            mCaptureSizeFeature[pos] = 0;
            mSelfAtariSizeFeature[pos] = -1;

            updateFeature();

            Debug.printBoard(mBoardFeature);
        }
        return move;
    }

    public Game getGame() {
        return mGame;
    }

    public boolean playMove(int x, int y, byte player) {
        return playMove(x + y * 19, player);
    }

    public boolean playMove(int pos, byte player) {
        if (!isLegal(pos, player)) {
            return false;
        }

        mActivePlayer = getNextPlayer(player);

        Set<Chain> captured = new HashSet<>();
        Stone stone = new Stone();
        stone.color = getStoneColor(player);
        stone.intersection = new Intersection(pos % 19, pos / 19);
        mGame.addStone(stone, captured);
        stone.number = mGame.getHistory().getHead();

        // 劫位置更新
        Intersection intersection = mGame.getHistory().readLatest().getKO();
        if (intersection != null) {
            mKOPos = intersection.x + intersection.y * 19;
        } else {
            mKOPos = -1;
        }

        // 当前落子所在串及周围的点加入待更新列表
        Chain chain = mGame.getChain(stone.intersection);
        mShouldUpdatePos.addAll(chain.getLiberties());
        for (Stone s : chain.getStones()) {
            mShouldUpdatePos.add(s.intersection);
        }

        for (Chain cCapture : captured) {
            mShouldUpdatePos.addAll(cCapture.getLiberties());
            for (Stone sCapture : cCapture.getStones()) {
                int p = sCapture.intersection.x + sCapture.intersection.y * 19;
                mBoardFeature[p] = EMPTY;
                mLibertyFeature[p] = 0;

                // 死子串及周围的点加入待更新列表
                mShouldUpdatePos.add(sCapture.intersection);
            }
        }

        mBoardFeature[pos] = player;
        mLibertyAfterFeature[pos] = 0;
        mCaptureSizeFeature[pos] = 0;
        mSelfAtariSizeFeature[pos] = -1;

        updateFeature();

        Debug.printBoard(mBoardFeature);
        return true;
    }

    private void updateHistoryFeature() {
        for (int i = 0; i < 361; i++) {
            mHistoryFeature[i] = -1;
        }
        for (Chain chain : mGame.getChains()) {
            for (Stone stone : chain.getStones()) {
                int pos = stone.intersection.x + stone.intersection.y * 19;
                int turnSince = mGame.getHistory().getHead() - stone.number;
                mHistoryFeature[pos] = turnSince > Byte.MAX_VALUE ?
                        Byte.MAX_VALUE : (byte) turnSince;
            }
        }
    }

    public boolean isLegal(int pos, byte player) {
        if (!isOnBoard(pos)) {
            return false;
        }
        if (mBoardFeature[pos] != EMPTY) {
            return false;
        }
        if (mKOPos == pos) {
            return false;
        }
        if (isSuicide(pos, player)) {
            return false;
        }
        return true;
    }

    private boolean isSuicide(int pos, byte player) {
        Stone stone = new Stone();
        stone.color = getStoneColor(player);
        stone.intersection = new Intersection(pos % 19, pos / 19);
        return mGame.isSuicide(stone);
    }

    private boolean isLikeEye(int pos, int owner) {
        if (mBoardFeature[pos] != EMPTY) {
            return false;
        }
        for (int n : NEIGHBOR_CACHE.get(pos)) {
            if (mBoardFeature[n] != owner) {
                return false;
            }
        }
        return true;
    }

    private boolean isEye(int pos, int owner, Stack<Integer> stack) {
        if (!isLikeEye(pos, owner)) {
            return false;
        }
        int numBadDiagonal = 0;
        int allowableBadDiagonal = NEIGHBOR_CACHE.get(pos).size() == 4 ? 1 : 0;
        for (int d : DIAGONAL_CACHE.get(pos)) {
            if (mBoardFeature[d] == -owner) {
                numBadDiagonal += 1;
            } else if (mBoardFeature[d] == EMPTY && !stack.contains(d)) {
                stack.push(pos);
                if (!isEye(d, owner, stack)) {
                    numBadDiagonal += 1;
                }
                stack.pop();
            }
            if (numBadDiagonal > allowableBadDiagonal) {
                return false;
            }
        }
        return true;
    }

    private static boolean isOnBoard(int pos) {
        return pos >= 0 && pos <= 360;
    }

    private static int topLeft(int pos) {
        return pos - 19 - 1;
    }

    private static int topRight(int pos) {
        return pos - 19 + 1;
    }

    private static int bottomLeft(int pos) {
        return pos + 19 - 1;
    }

    private static int bottomRight(int pos) {
        return pos + 19 + 1;
    }

    private static int top(int pos) {
        return pos - 19;
    }

    private static int bottom(int pos) {
        return pos + 19;
    }

    private static int left(int pos) {
        return pos - 1;
    }

    private static int right(int pos) {
        return pos + 1;
    }

    private void setOneHot(byte features[][], int pos, int i, int j) {
        features[pos][FEATURE_START[i] + Math.min(j, FEATURE_COUNT[i] - 1)] = 1;
    }

    private void setOneHot49(byte features[][], int pos, int i, int j) {
        features[pos][FEATURE_START_49[i] + Math.min(j, FEATURE_COUNT_49[i] - 1)] = 1;
    }

    public byte[] getBoard() {
        return mBoardFeature;
    }

    public byte[][] generateFeatures48() {
        byte features[][] = (byte[][]) Array.newInstance(Byte.TYPE, mBoardSize, 48);
        int pos = 0;
        while (pos < mBoardFeature.length) {
            if (mBoardFeature[pos] == 0) {
                features[pos][FEATURE_START[BOARD] + 2] = 1; // 空001
                if (isLegal(pos, mActivePlayer)) {
                    setOneHot(features, pos, CAPTURE_SIZE, mCaptureSizeFeature[pos]);
                    if (mSelfAtariSizeFeature[pos] > 0) {
                        setOneHot(features, pos, SELF_ATARI_SIZE, mSelfAtariSizeFeature[pos] - 1);
                    }
                    setOneHot(features, pos, LIBERTIES_AFTER, mLibertyAfterFeature[pos] - 1);
                    features[pos][FEATURE_START[LADDER_CAPTURE]] = mLadderCaptureFeature[pos];
                    features[pos][FEATURE_START[LADDER_ESCAPE]] = mLadderEscapeFeature[pos];
                    if (isSensibleness(pos)) {
                        features[pos][FEATURE_START[SENSIBLENESS]] = 1;
                    }
                }
            } else {
                if (mBoardFeature[pos] == mActivePlayer) {
                    features[pos][FEATURE_START[BOARD]] = 1; // 己方棋子100
                } else {
                    features[pos][FEATURE_START[BOARD] + 1] = 1; // 敌方棋子010
                }
                setOneHot(features, pos, TURNS_SINCE, mHistoryFeature[pos]);
                setOneHot(features, pos, LIBERTIES, mLibertyFeature[pos] - 1);
            }
            features[pos][FEATURE_START[ONES]] = 1;
            pos++;
        }
        return features;
    }

    public byte[][] generateFeatures49() {
        byte features[][] = (byte[][]) Array.newInstance(Byte.TYPE, mBoardSize, 49);
        int pos = 0;
        while (pos < mBoardFeature.length) {
            if (mBoardFeature[pos] == 0) {
                features[pos][FEATURE_START_49[BOARD_49]] = 1; // 空100
                if (isLegal(pos, mActivePlayer)) {
                    features[pos][FEATURE_START_49[CAPTURE_SIZE_49]] = mCaptureSizeFeature[pos];
                    if (mSelfAtariSizeFeature[pos] > 0) {
                        setOneHot49(features, pos, SELF_ATARI_SIZE_49, mSelfAtariSizeFeature[pos] - 1);
                    }
                    setOneHot49(features, pos, LIBERTIES_AFTER_49, mLibertyAfterFeature[pos] - 1);
                    features[pos][FEATURE_START_49[LADDER_CAPTURE_49]] = mLadderCaptureFeature[pos];
                    features[pos][FEATURE_START_49[LADDER_ESCAPE_49]] = mLadderEscapeFeature[pos];
                    if (isSensibleness(pos)) {
                        features[pos][FEATURE_START_49[SENSIBLENESS_49]] = 1;
                    }
                }
            } else {
                if (mBoardFeature[pos] == mActivePlayer) {
                    features[pos][FEATURE_START_49[BOARD_49] + 1] = 1; // 己方棋子010
                } else {
                    features[pos][FEATURE_START_49[BOARD_49] + 2] = 1; // 敌方棋子001
                }
                setOneHot49(features, pos, TURNS_SINCE_49, mHistoryFeature[pos]);
                setOneHot49(features, pos, LIBERTIES_49, mLibertyFeature[pos] - 1);
            }
            features[pos][FEATURE_START_49[ONES_49]] = 1;
            pos++;
        }
        return features;
    }
}
