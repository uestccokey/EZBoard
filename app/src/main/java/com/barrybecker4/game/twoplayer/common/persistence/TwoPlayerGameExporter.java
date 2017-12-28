/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.persistence;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.IGameController;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.persistence.GameExporter;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Exports the state of a two player game to a file.
 *
 * @author Barry Becker
 */
public class TwoPlayerGameExporter<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>> extends GameExporter<M, B> {

    protected PlayerList players;

    /** make a copy of the board and players in case they change */
    public TwoPlayerGameExporter(IGameController<M, B> controller) {
        super((B) controller.getBoard().copy());

        players = new PlayerList();
        players.addAll(controller.getPlayers());
    }

    /**
     * save the current state of the game to a file in SGF (4) format.
     * SGF stands for Smart Game Format. Its text based but should be xml.
     *
     * @param fileName name of the file to save the state to
     * @param ae       the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile(String fileName, AssertionError ae) {
        GameContext.log(1, "saving state to :" + fileName);

        try {
            Writer out = createWriter(fileName);
            // SGF header info
            out.write("(;\n");
            out.write("FF[4]\n");
            out.write("GM[1]\n");
            //out.write( "CA[UTF-8]\n" );
            out.write("SZ2[" + board_.getNumRows() + "][" + board_.getNumCols() + "]\n");
            out.write("Player1[" + players.getPlayer1().getName() + "]\n");
            out.write("Player2[" + players.getPlayer2().getName() + "]\n");
            out.write("GN[test1]\n");

            writeMoves(board_.getMoveList(), out);
            writeExceptionIfAny(ae, out);

            out.write(')');
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    protected void writeMoves(MoveList<M> moves, Writer out) throws IOException {
        Iterator<M> it = moves.iterator();
        GameContext.log(1, "movelist size= " + moves.size());
        while (it.hasNext()) {
            M move = it.next();
            out.write(getSgfForMove(move));
        }
    }

    protected void writeExceptionIfAny(AssertionError ae, Writer out) throws IOException {
        // include error info and stack trace in the comments to help debug
        if (ae != null) {
            out.write("C[");
            if (ae.getMessage() != null) {
                out.write(ae.getMessage());
                //out would need to be a PrintWriter for this to work
                //rte.printStackTrace(out);
            }
            out.write("]\n");
        }
    }

    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    @Override
    protected String getSgfForMove(M move) {
        // passes are not represented in SGF - so just skip it if the piece is null.

        StringBuilder buf = new StringBuilder("");
        String player = "P2";
        if (move.isPlayer1()) {
            player = "P1";
        }
        buf.append(';');
        buf.append(player);
        serializePosition(move.getToLocation(), buf);
        buf.append('\n');
        return buf.toString();
    }
}
