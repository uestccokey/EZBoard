package cn.ezandroid.goboard.demo;

/**
 * 价值网络接口
 *
 * @author like
 * @date 2017-12-23
 */
public interface IValueNetwork {

    float[] getOutput(byte[][][] input, int player);
}
