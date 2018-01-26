package cn.ezandroid.goboard.demo.network;

/**
 * 策略网络接口
 *
 * @author like
 * @date 2017-12-23
 */
public interface IPolicyNetwork {

    float[][] getOutput(FeatureBoard featureBoard);
}
