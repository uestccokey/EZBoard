/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

/**
 * 眼型枚举
 * <p>
 * http://www.lamsade.dauphine.fr/~cazenave/papers/eyeLabelling.pdf
 * <p>
 * 眼位中每个交叉点的邻接眼位点数量进行由低到高排序
 * <p>
 * E11222: XXXXX      XXXX      XXX      XX     XX    X       X
 * >                     X        XX      X      X    X      XX
 * >                                     XX      XX   XXX   XX
 * <p>
 * E11123: XX        X       X
 * >      XX        XX       X
 * >       X         X      XXX
 * >                 X
 * <p>
 * E11114: X
 * >      XXX
 * >       X
 * <p>
 * E12223: XX
 * >       XXX
 *
 * @author Barry Becker
 */
public enum EyeType {

    /** 假眼总是有变成无眼的可能 */
    FalseEye(0) {
        @Override
        public EyeInformation getInformation(String name) { return new FalseEyeInformation(); }
    },

    E1(1) {
        @Override
        public EyeInformation getInformation(String name) { return new E1Information(); }
    },
    E2(2) {
        @Override
        public EyeInformation getInformation(String name) { return new E2Information(); }
    },
    E3(3) {
        @Override
        public EyeInformation getInformation(String name) { return new E3Information(); }
    },
    E4(4) {
        @Override
        public EyeInformation getInformation(String name) { return new E4Information(name); }
    },
    E5(5) {
        @Override
        public EyeInformation getInformation(String name) { return new E5Information(name); }
    },
    E6(6) {
        @Override
        public EyeInformation getInformation(String name) { return new E6Information(name); }
    },
    E7(7) {
        @Override
        public EyeInformation getInformation(String name) { return new E7Information(name); }
    },

    /** 通常有两个或更多的眼，但是有一些罕见情况下只有一个眼或者没有眼 */
    TerritorialEye(8) {
        @Override
        public EyeInformation getInformation(String name) { return new TerritorialEyeInformation(); }
    };

    private byte mSize;

    EyeType(int eyeSize) {
        this.mSize = (byte) eyeSize;
    }

    /**
     * 获取眼位空间大小（眼位中敌方棋子也包含在内）
     *
     * @return
     */
    public byte getSize() {
        return mSize;
    }

    /**
     * 获取指定类型名称的眼位信息
     *
     * @param name
     * @return
     */
    public abstract EyeInformation getInformation(String name);
}