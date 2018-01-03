/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.geometry;

/**
 * 由左上和右下两个点定义的框
 *
 * @author Barry Becker
 */
public class Box {

    private IntLocation mTopLeftCorner;
    private IntLocation mBottomRightCorner;

    public Box(Location pt0, Location pt1) {
        this(Math.min(pt0.getRow(), pt1.getRow()), Math.min(pt0.getCol(), pt1.getCol()),
                Math.max(pt0.getRow(), pt1.getRow()), Math.max(pt0.getCol(), pt1.getCol()));
    }

    public Box(Location pt0) {
        this(pt0, pt0);
    }

    public Box(int rowMin, int colMin, int rowMax, int colMax) {
        if (rowMin > rowMax) {
            int temp = rowMin;
            rowMin = rowMax;
            rowMax = temp;
        }
        if (colMin > colMax) {
            int temp = colMin;
            colMin = colMax;
            colMax = temp;
        }

        mTopLeftCorner = new IntLocation(rowMin, colMin);
        mBottomRightCorner = new IntLocation(rowMax, colMax);
    }

    public Box(Box oldBox, Location point) {
        this(oldBox.getTopLeftCorner(), oldBox.getBottomRightCorner());
        expandBy(point);
    }

    /**
     * 获取框的宽度
     *
     * @return
     */
    public int getWidth() {
        return Math.abs(mBottomRightCorner.getCol() - mTopLeftCorner.getCol());
    }

    /**
     * 获取框的高度
     *
     * @return
     */
    public int getHeight() {
        return Math.abs(mBottomRightCorner.getRow() - mTopLeftCorner.getRow());
    }

    public IntLocation getTopLeftCorner() {
        return mTopLeftCorner;
    }

    public IntLocation getBottomRightCorner() {
        return mBottomRightCorner;
    }

    public int getMinRow() {
        return mTopLeftCorner.getRow();
    }

    public int getMinCol() {
        return mTopLeftCorner.getCol();
    }

    public int getMaxRow() {
        return mBottomRightCorner.getRow();
    }

    public int getMaxCol() {
        return mBottomRightCorner.getCol();
    }

    public int getArea() {
        return getWidth() * getHeight();
    }

    /**
     * 检测该框是否包含指定点
     *
     * @param pt
     * @return
     */
    public boolean contains(Location pt) {
        int row = pt.getRow();
        int col = pt.getCol();
        return (row >= getMinRow() && row <= getMaxRow() && col >= getMinCol() && col <= getMaxCol());
    }

    /**
     * 检查指定点是否在该框的边线上
     *
     * @param location
     * @return
     */
    public boolean isOnEdge(Location location) {
        return (location.getRow() == mBottomRightCorner.getRow()
                || location.getRow() == mTopLeftCorner.getRow()
                || location.getCol() == mBottomRightCorner.getCol()
                || location.getCol() == mTopLeftCorner.getCol());
    }

    /**
     * 检查指定点是否在该框的4个角上
     *
     * @param location
     * @return
     */
    public boolean isOnCorner(Location location) {
        return location.equals(mBottomRightCorner)
                || location.equals(mTopLeftCorner)
                || ((location.getRow() == mBottomRightCorner.getRow()
                && location.getCol() == mTopLeftCorner.getCol())
                || (location.getRow() == mTopLeftCorner.getRow()
                && location.getCol() == mBottomRightCorner.getCol()));
    }

    /**
     * 注意，由于4个角的点是不可变的，所以如果要改变它，我们会重新创建一个新的对象
     *
     * @param loc
     */
    public void expandBy(Location loc) {
        expandBy(new IntLocation(loc));
    }

    /**
     * 注意，由于4个角的点是不可变的，所以如果要改变它，我们会重新创建一个新的对象
     *
     * @param loc
     */
    public void expandBy(IntLocation loc) {
        if (loc.getRow() < mTopLeftCorner.getRow()) {
            mTopLeftCorner = new IntLocation(loc.getRow(), mTopLeftCorner.getCol());
        } else if (loc.getRow() > mBottomRightCorner.getRow()) {
            mBottomRightCorner = new IntLocation(loc.getRow(), mBottomRightCorner.getCol());
        }

        if (loc.getCol() < mTopLeftCorner.getCol()) {
            mTopLeftCorner = new IntLocation(mTopLeftCorner.getRow(), loc.getCol());
        } else if (loc.getCol() > mBottomRightCorner.getCol()) {
            mBottomRightCorner = new IntLocation(mBottomRightCorner.getRow(), loc.getCol());
        }
    }

    /**
     * 扩展该框的所有边
     *
     * @param amount 每个边扩展了多少
     * @param maxRow 扩展到的最大行数
     * @param maxCol 扩展到的最大列数
     */
    public void expandGloballyBy(int amount, int maxRow, int maxCol) {
        mTopLeftCorner =
                new IntLocation(Math.max(mTopLeftCorner.getRow() - amount, 1),
                        Math.max(mTopLeftCorner.getCol() - amount, 1));

        mBottomRightCorner =
                new IntLocation(Math.min(mBottomRightCorner.getRow() + amount, maxRow),
                        Math.min(mBottomRightCorner.getCol() + amount, maxCol));
    }

    /**
     * 当该框的某边距离边界在threshold以内时，扩展该边到边界
     * <p>
     * 比如7x7的棋盘，Box为(3,3)和(4,6)两点确定的框，threshold为1时，执行该方法后，Box变为(3,3)和(4,7)的框
     *
     * @param threshold 阈值距离
     * @param maxRow    扩展到的最大行数
     * @param maxCol    扩展到的最大列数
     */
    public void expandBordersToEdge(int threshold, int maxRow, int maxCol) {
        if (mTopLeftCorner.getRow() <= threshold + 1) {
            mTopLeftCorner = new IntLocation(1, mTopLeftCorner.getCol());
        }
        if (mTopLeftCorner.getCol() <= threshold + 1) {
            mTopLeftCorner = new IntLocation(mTopLeftCorner.getRow(), 1);
        }

        if (maxRow - mBottomRightCorner.getRow() <= threshold) {
            mBottomRightCorner = new IntLocation(maxRow, mBottomRightCorner.getCol());
        }
        if (maxCol - mBottomRightCorner.getCol() <= threshold) {
            mBottomRightCorner = new IntLocation(mBottomRightCorner.getRow(), maxCol);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("Box:"); //NON-NLS
        buf.append(mTopLeftCorner);
        buf.append(" - ");
        buf.append(mBottomRightCorner);
        return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Box box = (Box) o;

        return !(mBottomRightCorner != null ? !mBottomRightCorner.equals(box.mBottomRightCorner) : box.mBottomRightCorner != null)
                && !(mTopLeftCorner != null ? !mTopLeftCorner.equals(box.mTopLeftCorner) : box.mTopLeftCorner != null);
    }

    @Override
    public int hashCode() {
        int result = mTopLeftCorner != null ? mTopLeftCorner.hashCode() : 0;
        result = 31 * result + (mBottomRightCorner != null ? mBottomRightCorner.hashCode() : 0);
        return result;
    }
}
