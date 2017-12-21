package cn.ezandroid.goboard.demo;

import android.content.Context;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/**
 * RocAlphaGo的策略网络，正确率57%
 *
 * @author like
 * @date 2017-11-02
 */
public class Roc57Policy {

    private static final int FEATURE_LENGTH = 48;
    private static final int BOARD_SIZE = 19;
    private static final String INPUT_NAME = "conv2d_1_input";
    private static final String MODEL_FILE = "RocAlphaGo57.pb";
    private static final String OUTPUT_NAME = "activation_1/Softmax";
    private static String[] mOutputNames;
    private TensorFlowInferenceInterface mTensorFlow;

    private final Object mLock = new Object();

    private long mTotalUsedTime;
    private int mTotalUsedCount;

    public Roc57Policy(Context context) {
        mTensorFlow = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
        mOutputNames = new String[]{OUTPUT_NAME};
    }

    public float[][] getOutput(byte[][][] input) {
        float[] in = new float[input.length * input[0].length * input[0][0].length];
        float[] output = new float[input.length * BOARD_SIZE * BOARD_SIZE];
        for (int n = 0; n < input.length; n++) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    for (int k = 0; k < FEATURE_LENGTH; k++) {
                        in[n * BOARD_SIZE * BOARD_SIZE * FEATURE_LENGTH + j * BOARD_SIZE + i + k * BOARD_SIZE * BOARD_SIZE] =
                                input[n][j * BOARD_SIZE + i][k];
                    }
                }
            }
        }

        synchronized (mLock) {
            long time = System.currentTimeMillis();
            mTensorFlow.feed(INPUT_NAME, in, input.length, FEATURE_LENGTH, BOARD_SIZE, BOARD_SIZE);
            mTensorFlow.run(mOutputNames);
            mTensorFlow.fetch(OUTPUT_NAME, output);
            mTotalUsedTime += (System.currentTimeMillis() - time);
        }

        float[][] result = new float[input.length][BOARD_SIZE * BOARD_SIZE];
        for (int n = 0; n < input.length; n++) {
            System.arraycopy(output, n * BOARD_SIZE * BOARD_SIZE, result[n], 0, BOARD_SIZE * BOARD_SIZE);
        }
        mTotalUsedCount++;
        return result;
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
