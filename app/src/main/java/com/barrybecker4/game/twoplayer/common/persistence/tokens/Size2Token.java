/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.persistence.tokens;

import java.io.IOException;
import java.io.StreamTokenizer;

import cn.ezandroid.sgf.SGFException;
import cn.ezandroid.sgf.tokens.InfoToken;
import cn.ezandroid.sgf.tokens.SGFToken;

/**
 * The size of the game board.
 * Unlike the size of a go board, 2 dimensions must be specified.
 */
public class Size2Token extends SGFToken implements InfoToken {

    private int numRows;
    private int numColumns;

    public Size2Token() {}

    /**
     * Parse the dimensions of the board
     */
    @Override
    protected boolean parseContent(StreamTokenizer st) throws IOException, SGFException {
        try {
            int token = st.nextToken();
            numRows = Integer.parseInt(st.sval);
            if (st.nextToken() != (int) ']') return false;
            if (st.nextToken() != (int) '[') return false;
            st.nextToken();
            numColumns = Integer.parseInt(st.sval);
            if (st.nextToken() != (int) ']') return false;
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }
}

