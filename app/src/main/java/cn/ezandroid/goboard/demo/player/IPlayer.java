package cn.ezandroid.goboard.demo.player;

import cn.ezandroid.goboard.Stone;

/**
 * 玩家接口
 *
 * @author like
 * @date 2018-01-26
 */
public interface IPlayer {

    Stone genMove(boolean player1);
}
