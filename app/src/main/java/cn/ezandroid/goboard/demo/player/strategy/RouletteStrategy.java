package cn.ezandroid.goboard.demo.player.strategy;

/**
 * 轮盘赌策略
 * <p>
 * 过程:
 * 1 策略网络计算当前局面落子概率
 * 2 随机选择进行落子，落子概率越大的被选中的几率越大
 * 支持设置最低选择概率阈值，比如设置为0.05，即0.05以下的概率不会被选中进行落子
 * 支持设置直接落子概率阈值，比如设置为0.95，即有0.95以上的概率时，直接选择该位置进行落子
 *
 * @author like
 * @date 2018-01-29
 */
public class RouletteStrategy {

    // TODO
}
