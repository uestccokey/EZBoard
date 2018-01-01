/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEye;

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 *
 * @author Barry Becker
 * @see GoEye
 * <p>
 * We define the Neighbour Classification of  an eye as a number
 * of  digits sorted from low to high, where every intersection in the eye space is associated
 * to a digit that indicates the number of neighbors (adjacent intersections)
 * to that intersection that belong to the eye space.
 * <p>
 * For example, here are all the possible pentomino classifications (independent of symmetry).
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
 */
public enum EyeType {

    /** False eye always have the potential to become no eyes */
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

    /** Usually 2 or more eyes, but may be none or one in some rare cases. */
    TerritorialEye(8) {
        @Override
        public EyeInformation getInformation(String name) { return new TerritorialEyeInformation(); }
    };

    private byte size;

    /**
     * constructor
     */
    EyeType(int eyeSize) {
        this.size = (byte) eyeSize;
    }

    /**
     * @return the number of spaces in they eye (maybe be filled with some enemy stones).
     */
    public byte getSize() {
        return size;
    }

    /**
     * @return true if the shape has the life property
     */
    public abstract EyeInformation getInformation(String name);
}