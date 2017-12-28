/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.persistence;

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;
import com.barrybecker4.ca.dj.jigo.sgf.SGFLoader;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.CommentToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.DateToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.FileFormatToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameCommentToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameIDToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameNameToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameTypeToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.NodeNameToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.ResultToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SGFToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.TextToken;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player1MoveToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player1NameToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player2MoveToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player2NameToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Size2Token;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.TwoPlayerMoveToken;

import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * Loads a two player game from a text file.
 *
 * @author Barry Becker
 */
public class TwoPlayerSGFLoader extends SGFLoader {

    /**
     * Creates a new instance of SGFTwoPlayerLoader
     */
    public TwoPlayerSGFLoader() {}

    /**
     * Reads an SGF token, provided a StreamTokenizer to help with parsing the
     * text into SGFTokens.
     * <p>
     *
     * @param st - The StreamTokenizer from which to read an SGF token.
     * @return An SGFToken representing a piece of information about the game.
     */
    @Override
    protected SGFToken readToken(StreamTokenizer st) throws IOException, SGFException {
        SGFToken token = null;
        String tokenName = st.sval.toUpperCase();

        // moves are the most common token in an SGF file.
        switch (tokenName) {
            case "P1":
                token = createPlayer1MoveToken();
                break;
            case "P2":
                token = createPlayer2MoveToken();
                break;

            // Comments, notes, and figures are next most common.
            case "C":
            case "COMMENT":
                token = new CommentToken();
                break;
            case "N":
            case "NAME":
                token = new NodeNameToken();
                break;

            // Lastly, tokens that belong to the first leaf of the first variation
            // appear once.  These are intentionally placed in this position as a
            // standard convention for JiGo's SGF API.
            case "FF":
                token = new FileFormatToken();
                break;
            case "GM":
            case "GAME":
                token = new GameTypeToken();
                break;
            case "SZ2":
            case "SIZE":
                token = new Size2Token();
                break;
            case "PLAYER1":
                token = new Player1NameToken();
                break;
            case "PLAYER2":
                token = new Player2NameToken();
                break;
            case "DT":
            case "DATE":
                token = new DateToken();
                break;
            case "RE":
            case "RESULT":
                token = new ResultToken();
                break;
            case "GC":
                token = new GameCommentToken();
                break;
            case "GN":
            case "GAMENAME":
                token = new GameNameToken();
                break;
            case "ID":
                token = new GameIDToken();
                break;
            case "CA":
                //  token = new CharsetToken();   // where did this token class go?
                token = new TextToken();
                break;

            // If all else fails, fail
            default:
                throw new SGFException("unexpected token name:" + tokenName);
        }

        parseToken(token, st);
        return token;
    }

    /**
     * Now that we know what type of token we have, ask it to parse itself.
     * Most of the parsing is done by the TextToken class.  All tokens are
     * subclasses of SGFToken.
     *
     * @param token game token of some type.
     * @param st    stream tokenizer
     */
    private void parseToken(SGFToken token, StreamTokenizer st) throws IOException, SGFException {
        token.parse(st);
        GameContext.log(2, "parsed token = " + token.toString());
    }

    protected TwoPlayerMoveToken createPlayer1MoveToken() {
        return new Player1MoveToken();
    }

    protected TwoPlayerMoveToken createPlayer2MoveToken() {
        return new Player2MoveToken();
    }
}
