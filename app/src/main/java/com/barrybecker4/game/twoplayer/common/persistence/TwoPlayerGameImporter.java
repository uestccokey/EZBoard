/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.persistence;

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;
import com.barrybecker4.ca.dj.jigo.sgf.SGFGame;
import com.barrybecker4.ca.dj.jigo.sgf.SGFLoader;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.InfoToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.PlacementToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SGFToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.TextToken;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.board.IRectangularBoard;
import com.barrybecker4.game.common.persistence.GameImporter;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player1MoveToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player1NameToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player2NameToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Size2Token;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.TwoPlayerMoveToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * Imports the state of a two player game from a file.
 *
 * @author Barry Becker
 */
public class TwoPlayerGameImporter<M extends TwoPlayerMove, B
        extends TwoPlayerBoard<M>>
        extends GameImporter<M, B> {

    public TwoPlayerGameImporter(TwoPlayerController<M, B> controller) {
        super(controller);
    }

    /**
     * Take what is in the specified file and show it in the viewer.
     *
     * @param iStream some input stream
     */
    public void restoreFromStream(InputStream iStream)
            throws IOException, SGFException {

        // If this ever fails from the IDE (and not gradle) its probably because
        // the IDE is not copying the sgf file to the classes directory.
        // In Intellij this can be fixed by going to the compiler settings and
        // adding ?*.sgf; to the resource pattern list.
        if (iStream == null) throw new IllegalArgumentException("iStream is null");
        SGFLoader gameLoader = createLoader();
        SGFGame game = gameLoader.load(iStream);
        restoreGame(game);
    }

    @Override
    protected SGFLoader createLoader() {
        return new TwoPlayerSGFLoader();
    }

    /**
     * Initialize the board based on the SGF game.
     */
    @Override
    protected void parseSGFGameInfo(SGFGame game) {
        TwoPlayerController gc = (TwoPlayerController) controller_;

        Enumeration e = game.getInfoTokens();
        int numRows = 15; // default unless specified
        int numCols = 12; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof Size2Token) {
                Size2Token sizeToken = (Size2Token) token;
                GameContext.log(2, "info token columns =" +
                        sizeToken.getNumColumns() + " rows=" + sizeToken.getNumRows());
                numRows = sizeToken.getNumRows();
                numCols = sizeToken.getNumColumns();
            } else if (token instanceof Player2NameToken) {
                Player2NameToken nameToken = (Player2NameToken) token;
                gc.getPlayers().getPlayer2().setName(nameToken.getName());
            } else if (token instanceof Player1NameToken) {
                Player1NameToken nameToken = (Player1NameToken) token;
                gc.getPlayers().getPlayer1().setName(nameToken.getName());
            }
        }
        ((IRectangularBoard) gc.getBoard()).setSize(numRows, numCols);
    }

    /**
     *
     */
    @Override
    protected boolean processToken(SGFToken token, MoveList<M> moveList) {
        boolean found = false;
        if (token instanceof PlacementToken) {
            moveList.add(createMoveFromToken(token));
            found = true;
        } else if (token instanceof TextToken) {
            TextToken textToken = (TextToken) token;
            GameContext.log(1, "text=" + textToken.getText());
        } else {
            GameContext.log(1, "\nignoring token " + token.getClass().getName());
        }
        return found;
    }

    /**
     * Create a move from the two player move Token
     */
    @Override
    protected M createMoveFromToken(SGFToken token) {
        TwoPlayerMoveToken mvToken = (TwoPlayerMoveToken) token;
        boolean player1 = token instanceof Player1MoveToken;

        return (M) TwoPlayerMove.createMove(mvToken.getToY(), mvToken.getToX(), 0, new GamePiece(player1));
    }
}
