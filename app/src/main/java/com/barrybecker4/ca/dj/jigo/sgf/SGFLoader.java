/*
 * Copyright (C) 2001 by Dave Jarvis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

package com.barrybecker4.ca.dj.jigo.sgf;

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
import com.barrybecker4.ca.dj.jigo.sgf.tokens.InfoToken;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

/**
 * A way to load SGF files.  This class handles SGF File Formats 3 and 4.
 * The API is extremely simple, so subclassing to handle different file
 * formats shouldn't be difficult.
 */
public class SGFLoader {
    private final static String INVALID_SGF_FILE =
            "Invalid SGF File.";

    private static List<SGFToken> myGameInfoTokens = new ArrayList<SGFToken>(10);

    /**
     * constructor.
     * Barry: I would really like this class to be abstract and this method protected, but jigo classes use it.
     */
    public SGFLoader() { }

    /**
     * Returns a new SGFGame, provided a valid (and open) InputStream.
     * <p>
     * Given an input stream, this method will parse its contents and return
     * a full SGFGame, complete with board markings, comments, and variations.
     * On some minor errors (like syntax problems with the file), the parsing
     * will stop and return whatever was parsed to that point.
     * <p>
     * <p>
     * Since everything is static, only one thread at a time may load a game.
     *
     * @param is - The InputStream from which contains SGF 3 or SGF 4 data.
     * @return A complete SGFGame, or null on unrecoverable errors.
     */
    public synchronized SGFGame load(InputStream is)
            throws IOException, SGFException {
        // Create and initialize a new StreamTokenizer, to make parsing simple.
        //
        StreamTokenizer st = new StreamTokenizer(
                new BufferedReader(new InputStreamReader(is)));

        resetTokenizer(st);

        SGFGame sgfGame = readGame(st);

        is.close();

        return sgfGame;
    }

    /**
     * Reads the entire game into a set of SGFTokens organized into a tree,
     * which represent variations in the game.  Does not handle multiple
     * games in a single file, yet (why this was made part of the SGF
     * specification is beyond me).
     *
     * @param st - The StreamTokenizer used to read text-based tokens from
     *           the input stream.
     * @return A fully parsed SGFGame, or null on unrecoverable errors.
     */
    private SGFGame readGame(StreamTokenizer st)
            throws IOException, SGFException {
        // Reset the game information tokens so they have a place to stay.
        //
        myGameInfoTokens.clear();

        // Read the game tree, and return a new SGFGame coupling
        // them like lovers.
        //
        SGFTree rootTree = readTree(st);

        SGFGame sgfGame = new SGFGame(rootTree);

        // Get the the game info tokens read from the SGF file and add them
        // to the game.  This way we guarantee that all tokens related to
        // the game are in the SGFGame class.
        //
        for (SGFToken token : myGameInfoTokens) {
            sgfGame.addInfoToken((InfoToken) (token));
        }

        return sgfGame;
    }

    /**
     * Reads the contents of an SGFTree, provided a StreamTokenizer to help
     * with parsing the textual tokens into SGFTokens.
     * <p>
     * A tree is a semi-colon followed by any number of leaves, followed by
     * either a new tree (indicated by an opening parenthesis) or the end
     * the current tree (indicated by a closing parenthesis).
     *
     * @param st - The StreamTokenizer from which to read an SGF File.
     * @return An SGFTree representing the contents of the given stream.
     */
    private SGFTree readTree(StreamTokenizer st)
            throws IOException, SGFException {
        SGFTree tree = new SGFTree();
        int token;

        // Continue reading in from the file while the next token is one of:
        //   leaf, sub-tree, or end of current tree.
        //
        // The StreamTokenizer will ignore whitespace and blank lines for us.
        //
        while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
            switch (token) {
                // We found a new leaf, so read it.
                //
                case (int) ';':
                    tree.addLeaf(readLeaf(st));
                    break;

                // Read an entire sub-tree (variation).
                //
                case (int) '(':
                    tree.addTree(readTree(st));
                    break;

                // This variation has no more leaves (or sub-variations).
                //
                case (int) ')':
                    return tree;

                // A major parsing error has occured.  Return whatever was parsed to
                // this point.
                //
                default:
                    throw new SGFException(INVALID_SGF_FILE);
            }
        }

        return tree;
    }

    /**
     * Reads the contents of an SGF node, provided a StreamTokenizer to help
     * with parsing the textual tokens into SGFTokens.
     * <p>
     * A leaf (node) starts at a semicolon contains tokens up until another
     * semicolon or closing parenthesis occurs.
     *
     * @param st - The StreamTokenizer from which to read an SGF node.
     * @return An SGFLeaf representing the contents of the node.
     */
    private SGFLeaf readLeaf(StreamTokenizer st)
            throws IOException, SGFException {
        SGFLeaf leaf = null;
        SGFToken sgfToken;

        int token;

        // Keep reading in tokens until the end of the file, the start of a new
        // leaf, start of a new tree, or end of the current tree/game.  These
        // are a semi-colon, open parenthesis, and close parenthesis respectively.
        //
        while (((token = st.nextToken()) != StreamTokenizer.TT_EOF) &&
                (token != (int) ';') &&
                (token != (int) '(') &&
                (token != (int) ')')) {
            if (token == StreamTokenizer.TT_WORD)
                if ((sgfToken = readToken(st)) != null) {
                    // Since we found a token that belongs to the "information"
                    // class, it gets a special place in habitat.
                    //
                    if (sgfToken instanceof InfoToken)
                        myGameInfoTokens.add(sgfToken);
                    else {
                        if (leaf == null)
                            leaf = new SGFLeaf(sgfToken);
                        else
                            leaf.addToken(sgfToken);
                    }
                }
        }

        // We found something that couldn't be an SGFToken, so let the calling
        // method handle whatever we read.  (End of game/variation/leaf, etc.)
        //
        st.pushBack();
        return leaf;
    }

    /**
     * Reads an SGF token, provided a StreamTokenizer to help with parsing the
     * text into SGFTokens.
     * <p>
     * In order to support importing of game types other than go, I have made this
     * method abstract. See implementations in derived classes. (Barry 2007)
     * <p>
     * Barry: I would really like this method to be Abstract, but jigo classes need to use this
     * <p>
     *
     * @param st - The StreamTokenizer from which to read an SGF token.
     * @return An SGFToken representing a piece of information about the game.
     */
    protected SGFToken readToken(StreamTokenizer st) throws IOException, SGFException {
        SGFToken token;
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

            // Tokens which apply to a leaf are next most common (board mark-up,
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
        else if (tokenName.equals("BL"))
            token = new BlackTimeToken();
        else if (tokenName.equals("WL"))
            token = new WhiteTimeToken();
        else if (tokenName.equals("OB"))
            token = new BlackStonesLeftToken();
        else if (tokenName.equals("OW"))
            token = new WhiteStonesLeftToken();
        else if (tokenName.equals("PM"))
            token = new PrintModeToken();
        else if (tokenName.equals("SY"))
            token = new SystemToken();
        else if (tokenName.equals("PC"))
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

            // If all else fails, just read it as a generic Text token (as opposed to
            // letting the SGF parsing fail for what might be something as
            // insignificant as an SGF token name typo).
            //
        else
            token = new TextToken();

        // Now that we know what type of token we have, ask it to parse itself.
        // Most of the parsing is done by the TextToken class.  All tokens are
        // subclasses of SGFToken.
        //
        token.parse(st);

        return token;
    }

    /**
     * Changes the settings of the given StreamTokenizer to make parsing the
     * SGF easier.
     *
     * @param st - The StreamTokenizer to alter.
     */
    private static void resetTokenizer(StreamTokenizer st) {
        // Configure the StreamTokenizer to enhance parsing.
        //
        st.lowerCaseMode(false);
        st.eolIsSignificant(false);

        // There's a bug in StreamTokenizer: parsing of numbers is turned on
        // auto-magically (how nice, eh?).  So the only way to get rid of this is
        // to make all numeric-related characters ordinary, then make them word
        // characters.  Only by looking at the source to StreamTokenizer can it be
        // discerned that "ordinaryChars( ... )" removes all numeric properties
        // of the characters in question.
        //
        // To be a bit more efficient about this, we're going to make ALL relevant
        // characters ordinary, and then make them ALL word characters, and then
        // make only the special tokens ordinary (again).  Otherwise, we'd have
        // to specifically select ranges out of the ASCII table and make several
        // calls to "wordChars" in order to make word characters out of everything
        // except the characters we have to "jump" over.  This way we just make
        // everything equal except for exactly the characters we want to tokenize.
        //
        st.ordinaryChars((int) '!', (int) '~');
        st.wordChars((int) '!', (int) '~');

        st.ordinaryChar((int) ';');
        st.ordinaryChar((int) '\\');
        st.ordinaryChars((int) '[', (int) ']');
        st.ordinaryChars((int) '(', (int) ')');
    }
}

