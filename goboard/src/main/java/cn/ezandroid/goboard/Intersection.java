package cn.ezandroid.goboard;

/**
 * 交叉点模型
 */
public class Intersection implements Cloneable {

    public int x;
    public int y;

    public Intersection(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected Intersection clone() throws CloneNotSupportedException {
        return (Intersection) super.clone();
    }

    @Override
    public boolean equals(Object other) {
        return !(other == null || this.getClass() != other.getClass())
                && (x == ((Intersection) other).x && y == ((Intersection) other).y);
    }

    @Override
    public int hashCode() { return ((Integer) (x + 19 * y)).hashCode(); }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }
}
