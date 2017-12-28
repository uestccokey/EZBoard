/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.persistence;

import com.barrybecker4.ca.dj.jigo.sgf.Point;
import com.barrybecker4.ca.dj.jigo.sgf.SGFGame;
import com.barrybecker4.ca.dj.jigo.sgf.SGFLoader;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.AddBlackToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.AddWhiteToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.BlackNameToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.InfoToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.KomiToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.MoveToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.PlacementListToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.RuleSetToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SGFToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SizeToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.TextToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.WhiteNameToken;
import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.IRectangularBoard;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameImporter;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.options.GoOptions;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Imports the stat of a Go game from a file.
 *
 * @author Barry Becker
 */
public class GoGameImporter extends TwoPlayerGameImporter {

    public GoGameImporter(TwoPlayerController controller) {
        super(controller);
    }

    @Override
    protected SGFLoader createLoader() {
        return new GoSGFLoader();
    }

    /**
     * Initialize the board based on the SGF game.
     */
    @Override
    protected void parseSGFGameInfo(SGFGame game) {

        TwoPlayerController gc = (TwoPlayerController) controller_;
        PlayerList players = gc.getPlayers();

        Enumeration e = game.getInfoTokens();
        int size = 13; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof SizeToken) {
                SizeToken sizeToken = (SizeToken) token;
                size = sizeToken.getSize();
            } else if (token instanceof KomiToken) {
                KomiToken komiToken = (KomiToken) token;
                ((GoOptions) gc.getOptions()).setKomi(komiToken.getKomi());
            }
            // so we don't guess wrong on where the handicap positions are
            // we will rely on their being an AB (add black) command to specifically tell where the handicap stones are
            /*else if (token instanceof HandicapToken) {
                HandicapToken handicapToken = (HandicapToken) token;
                GameContext.log(2,"***handicap ="+handicapToken.getHandicap());
                this.setHandicap(handicapToken.getHandicap());
            }*/
            else if (token instanceof WhiteNameToken) {
                WhiteNameToken nameToken = (WhiteNameToken) token;
                players.getPlayer2().setName(nameToken.getName());
            } else if (token instanceof BlackNameToken) {
                BlackNameToken nameToken = (BlackNameToken) token;
                players.getPlayer1().setName(nameToken.getName());
            } else if (token instanceof RuleSetToken) {
                //RuleSetToken ruleToken = (RuleSetToken) token;
                //this.setRuleSet(ruleToken.getKomi());
            } else {
                GameContext.log(1, "Ignoring  token =" + token.getClass().getName() + " while parsing.");
            }
        }
        ((IRectangularBoard) gc.getBoard()).setSize(size, size);
    }


    @Override
    protected boolean processToken(SGFToken token, MoveList moveList) {

        boolean found = false;
        if (token instanceof MoveToken) {
            moveList.add(createMoveFromToken(token));
            found = true;
        } else if (token instanceof AddBlackToken) {
            addMoves((PlacementListToken) token, moveList);
            found = true;
        } else if (token instanceof AddWhiteToken) {
            addMoves((PlacementListToken) token, moveList);
            found = true;
        }
        /*
        else if (token instanceof CharsetToken ) {
            CharsetToken charsetToken = (CharsetToken) token;
        }
        else if (token instanceof OverTimeToken ) {
            OverTimeToken charsetToken = (OverTimeToken) token;
            System.out.println("charset="+charsetToken.getCharset());
        }
         */
        else if (token instanceof TextToken) {
            TextToken textToken = (TextToken) token;
        } else {
            GameContext.log(0, "Ignoring token " + token.getClass().getName() + " while processing.");
        }
        return found;
    }

    /**
     * add a sequence of moves all at once.
     * Such as placing handicaps when reading from an sgf file.
     *
     * @param token game token
     */
    private static void addMoves(PlacementListToken token, MoveList moveList) {

        Iterator<Point> points = token.getPoints();
        // System.out.println("num points ="+token.getPoints2().size());
        boolean player1 = token instanceof AddBlackToken;

        while (points.hasNext()) {
            Point point = points.next();
            //System.out.println("adding move at row=" + point.y+" col="+ point.x);
            moveList.add(new GoMove(new ByteLocation(point.y, point.x), 0, new GoStone(player1)));
        }
    }

    @Override
    protected GoMove createMoveFromToken(SGFToken token) {
        MoveToken mvToken = (MoveToken) token;
        if (mvToken.isPass()) {
            return GoMove.createPassMove(0, !mvToken.isWhite());
        }
        return new GoMove(
                new ByteLocation(mvToken.getY(), mvToken.getX()),
                0, new GoStone(!mvToken.isWhite()));
    }
}
