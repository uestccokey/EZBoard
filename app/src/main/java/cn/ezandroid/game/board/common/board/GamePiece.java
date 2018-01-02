/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common.board;

import java.io.Serializable;

/**
 * the BoardPosition describes the physical marker at a location on the board.
 * It may be empty if there is no piece there.
 * Immutable except for transparency and annotation which should not matter.
 *
 * @author Barry Becker
 * @see Board
 */
public class GamePiece implements Serializable {

    private static final long serialVersionUID = 1;

    public static final char P1_SYMB = 'X';
    public static final char P2_SYMB = 'O';

    /** Subclasses should add more types if needed. */
    public static final char REGULAR_PIECE = 'x';

    /** true if this is player1's piece. */
    protected boolean ownedByPlayer1;

    /** the type of piece to draw */
    protected char pieceType;

    /**
     * For some pieces we may wish to represent them
     * more transparently (255 = total transparent; 0= totally opaque)
     */
    private short transparency;

    /**
     * a string associated with the piece to give additional information.
     * For example you can use this to show a number (0-99)
     */
    private String annotation;

    /**
     * default constructor
     */
    protected GamePiece() {
        ownedByPlayer1 = false;
        pieceType = REGULAR_PIECE;
        transparency = 0;
    }

    /**
     * constructor   (assumes a regular piece)
     *
     * @param player1 if owned by player1
     */
    public GamePiece(boolean player1) {
        ownedByPlayer1 = player1;
        pieceType = REGULAR_PIECE;
        transparency = 0;
    }

    /**
     * constructor
     *
     * @param player1 if owned by player1
     * @param type    there may be different types of pieces (for example in chess there are many; checkers has 2)
     */
    protected GamePiece(boolean player1, char type) {
        ownedByPlayer1 = player1;
        pieceType = type;
        transparency = 0;
    }

    /**
     * Copy constructor
     */
    protected GamePiece(GamePiece piece) {
        this(piece.isOwnedByPlayer1());
        this.pieceType = piece.getType();
        transparency = piece.transparency;
        annotation = piece.annotation;
    }

    /**
     * @return create a deep copy
     */
    public GamePiece copy() {
        return new GamePiece(this);
    }

    public final char getType() {
        return pieceType;
    }

    public final boolean isOwnedByPlayer1() {
        return ownedByPlayer1;
    }

    public final void setTransparency(short trans) {
        transparency = trans;
    }

    public final short getTransparency() {
        return transparency;
    }

    /**
     * @param annotation number or word to show next to the game piece.
     */
    public final void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * @return number or word to show next to the game piece.
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * @return Possibly give more detail that you would get with just toString().
     */
    public String getDescription() {
        return toString();
    }

    public char getSymbol() {
        return ownedByPlayer1 ? 'X' : 'O';
    }

    /**
     * @return a string representation of the board position
     */
    @Override
    public String toString() {
        return Character.toString(getSymbol());
    }
}

