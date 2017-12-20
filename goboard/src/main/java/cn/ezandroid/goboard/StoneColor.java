package cn.ezandroid.goboard;

/**
 * 棋子颜色枚举
 */
public enum StoneColor {
    BLACK,
    WHITE;

    StoneColor() {}

    StoneColor getOther() {
        return (this == BLACK) ? WHITE : BLACK;
    }
}
