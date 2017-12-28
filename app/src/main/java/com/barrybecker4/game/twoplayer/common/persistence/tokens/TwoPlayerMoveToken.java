/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.persistence.tokens;

import com.barrybecker4.ca.dj.jigo.sgf.Point;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.PlacementToken;

import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * A generic two player move.
 * The superclass for Player1MoveToken and Player2MoveToken.
 */
public abstract class TwoPlayerMoveToken extends PlacementToken {
    protected Point toPoint = new Point();


    /**
     * A token the describes where a players pawn started and where it ended after moving.
     */
    protected TwoPlayerMoveToken() { }

    /**
     * Parse in the position of the piece.
     */
    @Override
    protected boolean parseContent(StreamTokenizer st) throws IOException {
        boolean parsed = parsePoint(st, toPoint);
        return st.nextToken() == StreamTokenizer.TT_WORD && parsed;

    }

    /**
     * Parses a point, sets the X and Y values of the PlacementToken
     * accordingly.  This can be called repeatedly for LTokens which take
     * any number of points (see: PlacementListToken).
     * <p>
     * The first opening '[' must have already been read; thus leaving two
     * letters and a closing ']'.  This method reads everything up to and
     * including ']'.  If a ']' follows immediately after the '[', then this
     * move is considered a pass in FF[4].
     * <p>
     * The letters are from 'a' through 'Z', inclusive, to represent row
     * (or column) 1 through 52, respectfully.
     * <p>
     *
     * @return true - The point was perfectly parsed.
     * false - The point wasn't perfectly parsed.
     */
    protected boolean parsePoint(StreamTokenizer st, Point pt) throws IOException {
        st.nextToken();

        pt.x = (coordFromChar(st.sval.charAt(0)));
        pt.y = (coordFromChar(st.sval.charAt(1)));

        return (st.nextToken() == (int) ']');
    }


    /**
     * @param ch a token whose value ranges between 'a' through 'z', or 'A'  through 'Z',
     * @return the appropriate row/column value.  If
     * the token isn't between 'a' and 'z', or 'A' and 'Z', this returns 0;
     */
    protected static int coordFromChar(int ch) {
        if ((ch >= 'a') && (ch <= 'z'))
            return ch - 'a' + 1;

        if ((ch >= 'A') && (ch <= 'Z'))
            return ch - 'A' + 1;

        return 0;
    }

    /**
     * @return The X coordinate of the placement.
     */
    public int getToX() { return toPoint.x; }

    /**
     * @return The Y coordinate of the placement.
     */
    public int getToY() { return toPoint.y; }

}
