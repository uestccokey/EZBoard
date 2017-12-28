/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.persistence;

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;
import com.barrybecker4.ca.dj.jigo.sgf.SGFLoader;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.AddBlackToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.AddWhiteToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.ArrowToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BadMoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BlackMoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BlackNameToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BlackRankToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BlackSpeciesToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BlackStonesLeftToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BlackTimeToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.CircleToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.CommentToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.DateToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.DimToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.DoubtfulMoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.EvenPositionToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.EventToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.FigureToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.FileFormatToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameCommentToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameIDToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameNameToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GameTypeToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GoodBlackMoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.GoodWhiteMoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.InterestingMoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.KoToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.KomiToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.LabelToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.LineToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.MoveNumberToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.NodeNameToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.PlaceToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.PrintModeToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.ResultToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.RoundToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.RuleSetToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SGFToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SelectedListToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SizeToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SourceToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SquareToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SystemToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.TesujiToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.TextToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.TimeLimitToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.UnclearPositionToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.UserToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.ViewToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.WhiteMoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.WhiteNameToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.WhiteRankToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.WhiteSpeciesToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.WhiteStonesLeftToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.WhiteTimeToken;

import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * Created on May 27, 2007, 5:35 AM
 *
 * @author Barry Becker
 */
class GoSGFLoader extends SGFLoader {

    /**
     * Creates a new instance of SGFGoLoader
     */
    public GoSGFLoader() {
    }

    /**
     * Reads an SGF token, provided a StreamTokenizer to help with parsing the
     * text into SGFTokens.
     * <p>
     *
     * @param st - The StreamTokenizer from which to read an SGF token.
     * @return An SGFToken representing a piece of information about the game.
     */
    @Override
    protected SGFToken readToken(StreamTokenizer st)
            throws IOException, SGFException {
        SGFToken token = null;
        String tokenName = st.sval.toUpperCase();

        // Black and White moves are the most common token in an SGF file.
        //
        if (tokenName.equals("B") || tokenName.equals("BLACK"))
            token = new BlackMoveToken();
        else if (tokenName.equals("W") || tokenName.equals("WHITE"))
            token = new WhiteMoveToken();

            // Comments, notes, and figures are next most common.
            //
        else if (tokenName.equals("C") || tokenName.equals("COMMENT"))
            token = new CommentToken();
        else if (tokenName.equals("N") || tokenName.equals("NAME"))
            token = new NodeNameToken();
        else if (tokenName.equals("FG"))
            token = new FigureToken();

            // LTokens which apply to a leaf are next most common (board mark-up,
            // estimated score, etc.).
            //
        else if (tokenName.equals("AR"))
            token = new ArrowToken();
        else if (tokenName.equals("CR"))
            token = new CircleToken();
        else if (tokenName.equals("SQ"))
            token = new SquareToken();
        else if (tokenName.equals("LB"))
            token = new LabelToken();
        else if (tokenName.equals("MN"))
            token = new MoveNumberToken();
        else if (tokenName.equals("LN"))
            token = new LineToken();
        else if (tokenName.equals("DD"))
            token = new DimToken();
        else if (tokenName.equals("SL"))
            token = new SelectedListToken();
        else if (tokenName.equals("VW"))
            token = new ViewToken();

            // Adding black moves and white moves is typically done at the beginning
            // of a game (initial board position).
            //
        else if (tokenName.equals("AB") || tokenName.equals("ADDBLACK"))
            token = new AddBlackToken();
        else if (tokenName.equals("AW") || tokenName.equals("ADDWHITE"))
            token = new AddWhiteToken();

            // Many features of SGF are rarely used (but must be present and accounted
            // for in order to make a fully-compliant API).  These follow ...
            //
        else if (tokenName.equals("TE"))
            token = new TesujiToken();
        else if (tokenName.equals("BM"))
            token = new BadMoveToken();
        else if (tokenName.equals("GB"))
            token = new GoodBlackMoveToken();
        else if (tokenName.equals("GW"))
            token = new GoodWhiteMoveToken();
        else if (tokenName.equals("DM"))
            token = new EvenPositionToken();
        else if (tokenName.equals("UC"))
            token = new UnclearPositionToken();
        else if (tokenName.equals("IT"))
            token = new InterestingMoveToken();
        else if (tokenName.equals("DO"))
            token = new DoubtfulMoveToken();

            // Ko move, illegal capture, used mostly at the end of the game.
            //
        else if (tokenName.equals("KO"))
            token = new KoToken();

            // Lastly, tokens that belong to the first leaf of the first variation
            // appear once.  These are intentionally placed in this position as a
            // standard convention for JiGo's SGF API.
            //
        else if (tokenName.equals("FF"))
            token = new FileFormatToken();
        else if (tokenName.equals("GM") || tokenName.equals("GAME"))
            token = new GameTypeToken();
        else if (tokenName.equals("SZ") || tokenName.equals("SIZE"))
            token = new SizeToken();
        else if (tokenName.equals("KM") || tokenName.equals("KOMI"))
            token = new KomiToken();
        else if (tokenName.equals("PW") || tokenName.equals("PLAYERWHITE"))
            token = new WhiteNameToken();
        else if (tokenName.equals("PB") || tokenName.equals("PLAYERBLACK"))
            token = new BlackNameToken();
        else if (tokenName.equals("WR") || tokenName.equals("WHITERANK"))
            token = new WhiteRankToken();
        else if (tokenName.equals("BR") || tokenName.equals("WHITERANK"))
            token = new BlackRankToken();
        else if (tokenName.equals("DT") || tokenName.equals("DATE"))
            token = new DateToken();
        else if (tokenName.equals("RE") || tokenName.equals("RESULT"))
            token = new ResultToken();
        else if (tokenName.equals("TM") || tokenName.equals("TIME"))
            token = new TimeLimitToken();
        else if (tokenName.equals("BL"))  // BlacktimeLeft, Balck's time left after white has made a move
            token = new BlackTimeToken();
        else if (tokenName.equals("WL"))  // WhitetimeLeft, White's time left after black has made a move
            token = new WhiteTimeToken();
        else if (tokenName.equals("OB"))
            token = new BlackStonesLeftToken();
        else if (tokenName.equals("OW"))
            token = new WhiteStonesLeftToken();
        else if (tokenName.equals("PM"))
            token = new PrintModeToken();
        else if (tokenName.equals("SY"))
            token = new SystemToken();
        else if (tokenName.equals("PC"))  // place where game took place
            token = new PlaceToken();
        else if (tokenName.equals("EV") || tokenName.equals("EVENT"))
            token = new EventToken();
        else if (tokenName.equals("RO") || tokenName.equals("ROUND"))
            token = new RoundToken();
        else if (tokenName.equals("SO") || tokenName.equals("SOURCE"))
            token = new SourceToken();
        else if (tokenName.equals("US"))
            token = new UserToken();
        else if (tokenName.equals("GC"))
            token = new GameCommentToken();
        else if (tokenName.equals("RU"))
            token = new RuleSetToken();
        else if (tokenName.equals("BS"))
            token = new BlackSpeciesToken();
        else if (tokenName.equals("WS"))
            token = new WhiteSpeciesToken();
        else if (tokenName.equals("GN") || tokenName.equals("GAMENAME"))
            token = new GameNameToken();
        else if (tokenName.equals("ID"))
            token = new GameIDToken();
        else if (tokenName.equals("CA")) {
            //  token = new CharsetToken();   // where did this token class go?
            token = new TextToken();
        }
        // else if (tokenName.equals("HA") )
        //    token = new HandicapToken();

        // these token names appear in the IGS test files, but I don't know what they are for, or they just aren't that useful.
        // see http://www.red-bean.com/sgf/proplist_t.html
        // ST  Style           root	      number (range: 0-3)
        // OT  Overtime        game-info        simpletext
        // HA  Handicap        game-info        number
        // PL  Player to play  setup            color
        // AP  Application     root	      composed simpletext ':' number
        // CP  copyright
        // LT  Lose on Time, present in the root node if losing on time happens
        // PP ???
        // RG ???
        else if (tokenName.equals("ST") || tokenName.equals("OT") || tokenName.equals("PL")
                || tokenName.equals("CA") || tokenName.equals("HA")
                || tokenName.equals("AP") || tokenName.equals("CP") || tokenName.equals("LT")
                || tokenName.equals("PP") || tokenName.equals("RG")) {
            token = new TextToken();
        } else { // If all else fails, fail.
            throw new SGFException("unexpected token name:" + tokenName);
        }

        // Now that we know what type of token we have, ask it to parse itself.
        // Most of the parsing is done by the TextToken class.  All tokens are
        // subclasses of SGFToken.
        //
        token.parse(st);

        return token;
    }

}
