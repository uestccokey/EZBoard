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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stone)) return false;

        Stone stone = (Stone) o;

        if (number != stone.number) return false;
        if (color != stone.color) return false;
        return intersection != null ? intersection.equals(stone.intersection) : stone.intersection == null;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + (intersection != null ? intersection.hashCode() : 0);
        result = 31 * result + number;
        return result;
    }
}
