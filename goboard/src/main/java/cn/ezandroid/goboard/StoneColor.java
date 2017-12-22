package cn.ezandroid.goboard;

/**
 * 棋子颜色枚举
 *
 * @author like
 * @date 2017-12-20
 */
public enum StoneColor {
    BLACK,
    WHITE;

    StoneColor() {}

    public StoneColor getOther() {
        return (this == BLACK) ? WHITE : BLACK;
    }
}
