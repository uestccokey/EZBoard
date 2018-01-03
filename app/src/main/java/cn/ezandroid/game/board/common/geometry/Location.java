/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.geometry;

import java.io.Serializable;

/**
 * 位置点抽象类
 *
 * @author Barry Becker
 */
public abstract class Location implements Serializable {

    /**
     * 获取在第几行
     *
     * @return
     */
    public abstract int getRow();

    /**
     * 获取在第几列
     *
     * @return
     */
    public abstract int getCol();

    /**
     * 深克隆
     *
     * @return
     */
    public abstract Location copy();

    @Override
    public boolean equals(Object location) {
        if (!(location instanceof Location)) return false;
        Location loc = (Location) location;
        return (loc.getRow() == getRow()) && (loc.getCol() == getCol());
    }

    public int hashCode() {
        return (100 * getRow() + getCol());
    }

    /**
     * 获取当前位置到指定位置的欧几里德距离
     *
     * @param loc
     * @return
     */
    public double getDistanceFrom(Location loc) {
        float xDif = Math.abs(getCol() - loc.getCol());
        float yDif = Math.abs(getRow() - loc.getRow());
        return Math.sqrt(xDif * xDif + yDif * yDif);
    }

    public String toString() {
        return "(row=" + getRow() + ", column=" + getCol() + ")"; //NON-NLS
    }
}

