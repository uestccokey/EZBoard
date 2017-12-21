package cn.ezandroid.goboard;

/**
 * 棋子模型
 *
 * @author like
 */
public class Stone implements Cloneable {

    public StoneColor color; // 颜色
    public Intersection intersection; // 位置
    public int number; // 手数

    @Override
    public Stone clone() throws CloneNotSupportedException {
        Stone clone = (Stone) super.clone();
        clone.color = color;
        clone.intersection = intersection.clone();
        return clone;
    }

    @Override
    public String toString() {
        return "Stone{" +
                "color=" + color +
                ", intersection=" + intersection +
                ", number=" + number +
                '}';
    }
}
