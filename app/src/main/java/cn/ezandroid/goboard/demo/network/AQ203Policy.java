package cn.ezandroid.goboard.demo.network;

import android.content.Context;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/**
 * AQ203Policy
 *
 * @author like
 * @date 2018-01-24
 */
public class AQ203Policy implements IPolicyNetwork {

    private static final int FEATURE_LENGTH = 49;
    private static final int BOARD_SIZE = 19;
    private static final String INPUT_NAME = "x_input";
    private static final String TEMP_NAME = "temp";
    private static final String MODEL_FILE = "AQ203Policy.pb";
    private static final String OUTPUT_NAME = "fc/yfc";
    private static String[] mOutputNames;
    private TensorFlowInferenceInterface mTensorFlow;

    private final Object mLock = new Object();

    private long mTotalUsedTime;
    private int mTotalUsedCount;

    public AQ203Policy(Context context) {
        mTensorFlow = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
        mOutputNames = new String[]{OUTPUT_NAME};
    }

    @Override
    public float[][] getOutput(FeatureBoard featureBoard) {
        return getOutput(new byte[][][]{featureBoard.generateFeatures49()});
    }

    public float[][] getOutput(byte[][][] input) {
//        Log.e("AQPolicy", Thread.currentThread().getName() + " getOutput enter");
        float[] in = new float[input.length * input[0].length * input[0][0].length];
        float[] output = new float[input.length * BOARD_SIZE * BOARD_SIZE];
        for (int n = 0; n < input.length; n++) {
            for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
                for (int k = 0; k < FEATURE_LENGTH; k++) {
                    in[n * BOARD_SIZE * BOARD_SIZE * FEATURE_LENGTH + i * FEATURE_LENGTH + k] = input[n][i][k];
                }
            }
        }
        float[] temp = new float[input.length];
        for (int n = 0; n < input.length; n++) {
            temp[n] = 0.7f;
        }

        synchronized (mLock) {
            long time = System.currentTimeMillis();
            mTensorFlow.feed(INPUT_NAME, in, input.length, BOARD_SIZE * BOARD_SIZE, FEATURE_LENGTH);
            mTensorFlow.feed(TEMP_NAME, temp);
            mTensorFlow.run(mOutputNames);
            mTensorFlow.fetch(OUTPUT_NAME, output);
            mTotalUsedTime += (System.currentTimeMillis() - time);
        }

//        Log.e("AQPolicy", Thread.currentThread().getName() + " getOutput exit:" + (System.currentTimeMillis() - time));
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