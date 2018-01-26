package cn.ezandroid.goboard.demo.player;

import cn.ezandroid.goboard.Intersection;
import cn.ezandroid.goboard.Stone;
import cn.ezandroid.goboard.StoneColor;

/**
 * HumanPlayer
 *
 * @author like
 * @date 2018-01-26
 */
public class HumanPlayer implements IPlayer {

    private Intersection mIntersection;

    public void setIntersection(Intersection intersection) {
        mIntersection = intersection;
    }

    @Override
    public Stone genMove(boolean player1) {
        Stone stone = new Stone();
        stone.color = player1 ? StoneColor.BLACK : StoneColor.WHITE;
        stone.intersection = mIntersection;
        return stone;
    }
}
