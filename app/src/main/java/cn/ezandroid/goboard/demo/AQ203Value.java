package cn.ezandroid.goboard.demo;

import android.content.Context;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/**
 * AQ的价值网络
 *
 * @author like
 * @date 2017-10-31
 */
public class AQ203Value implements IValueNetwork {

    public static final int BLACK = 1;
    public static final int WHITE = 0;

    private static final int FEATURE_LENGTH = 49;
    private static final int BOARD_SIZE = 19;
    private static final String VN_X_NAME = "vn_x";
    private static final String VN_C_NAME = "vn_c";
    private static final String MODEL_FILE = "AQ203Value.pb";
    private static final String OUTPUT_NAME = "fc2/yfc";
    private static String[] mOutputNames;
    private TensorFlowInferenceInterface mTensorFlow;

    private final Object mLock = new Object();

    private long mTotalUsedTime;
    private int mTotalUsedCount;

    public AQ203Value(Context context) {
        mTensorFlow = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
        mOutputNames = new String[]{OUTPUT_NAME};
    }

    public float[] getOutput(byte[][][] input, int player) {
        float[] in = new float[input.length * input[0].length * input[0][0].length];
        float[] output = new float[input.length];
        for (int n = 0; n < input.length; n++) {
            for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
                for (int k = 0; k < FEATURE_LENGTH; k++) {
                    in[n * BOARD_SIZE * BOARD_SIZE * FEATURE_LENGTH + i * FEATURE_LENGTH + k] = input[n][i][k];
                }
            }
        }
        float[] color = new float[input.length * BOARD_SIZE * BOARD_SIZE];
        for (int i = 0; i < color.length; i++) {
            color[i] = player;
        }

        synchronized (mLock) {
            long time = System.currentTimeMillis();
            mTensorFlow.feed(VN_X_NAME, in, input.length, BOARD_SIZE * BOARD_SIZE, FEATURE_LENGTH);
            mTensorFlow.feed(VN_C_NAME, color, input.length, BOARD_SIZE * BOARD_SIZE, 1L);
            mTensorFlow.run(mOutputNames);
            mTensorFlow.fetch(OUTPUT_NAME, output);
            mTotalUsedTime += (System.currentTimeMillis() - time);
        }

        mTotalUsedCount++;
        return output;
    }

    public long getTotalUsedTime() {
        return mTotalUsedTime;
    }

    public void resetTotalUsedTime() {
        mTotalUsedTime = 0;
    }

    public long getTotalUsedCount() {
        return mTotalUsedCount;
    }

    public void resetTotalUsedCount() {
        mTotalUsedCount = 0;
    }
}
