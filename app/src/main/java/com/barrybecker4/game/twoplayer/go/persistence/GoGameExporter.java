/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.persistence;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.go.options.GoOptions;

import java.io.IOException;
import java.io.Writer;

/**
 * Exports the state of a Go game to a file.
 *
 * @author Barry Becker
 */
public class GoGameExporter extends TwoPlayerGameExporter<GoMove, GoBoard> {

    float komi = 0.5f;

    public GoGameExporter(TwoPlayerController<GoMove, GoBoard> controller) {
        super(controller);
        komi = ((GoOptions) controller.getOptions()).getKomi();
    }

    /**
     * save the current state of the go game to a file in SGF (4) format
     *
     * @param fileName name of the file to save the state to
     * @param ae       the exception that occurred causing us to want to save state
     */
    @Override
    public void saveToFile(String fileName, AssertionError ae) {
        GameContext.log(1, "saving state to :" + fileName);
        GoBoard b = board_;

        try {
            Writer out = createWriter(fileName);
            //PrintWriter foo;
            // SGF header info
            out.write("(;\n");
            out.write("FF[4]\n");
            out.write("GM[1]\n");
            out.write("CA[UTF-8]\n");
            out.write("ST[2]\n");
            out.write("RU[japanese]\n");
            out.write("SZ[" + b.getNumRows() + "]\n");
            out.write("PB[" + players.getPlayer1().getName() + "]\n");
            out.write("PW[" + players.getPlayer2().getName() + "]\n");
            out.write("KM[" + komi + "]\n");
            out.write("PC[US]\n");
            out.write("HA[" + b.getHandicap() + "]\n");
            out.write("GN[test1]\n");
            // out.write("PC[US]"); ?? add the handicap stones if present

            writeMoves(b.getMoveList(), out);
            writeExceptionIfAny(ae, out);

            out.write(')');
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    @Override
    protected String getSgfForMove(GoMove move) {

        // passes are not represented in SGF - so just skip it if the piece is null.
        if (move.getPiece() == null)
            return "[]";
        StringBuilder buf = new StringBuilder("");
        char player = 'W';
        if (move.getPiece().isOwnedByPlayer1()) {
            player = 'B';
        }
        buf.append(';');
        buf.append(player);
        buf.append('[');
        buf.append((char) ('a' + move.getToCol() - 1));
        buf.append((char) ('a' + move.getToRow() - 1));
        buf.append(']');
        buf.append('\n');
        return buf.toString();
    }
}
