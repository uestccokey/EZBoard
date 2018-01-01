/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.persistence;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.IBoard;
import com.barrybecker4.game.common.board.IRectangularBoard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import cn.ezandroid.sgf.SGFException;
import cn.ezandroid.sgf.SGFGame;
import cn.ezandroid.sgf.SGFLeaf;
import cn.ezandroid.sgf.SGFLoader;
import cn.ezandroid.sgf.SGFTree;
import cn.ezandroid.sgf.tokens.InfoToken;
import cn.ezandroid.sgf.tokens.PlacementToken;
import cn.ezandroid.sgf.tokens.SGFToken;
import cn.ezandroid.sgf.tokens.SizeToken;

/**
 * Import the state of a game from a file.
 *
 * @author Barry Becker
 */
public abstract class GameImporter<M extends Move, B extends IBoard<M>> {

    protected GameController<M, B> controller_;

    protected GameImporter(GameController<M, B> controller) {
        controller_ = controller;
    }

    /**
     * Restore the state of a game from an input stream.
     *
     * @param iStream the stream to restore from.
     */
    public abstract void restoreFromStream(InputStream iStream) throws IOException, SGFException;

    /**
     * This will restore a game from an SGF structure to the controller
     */
    protected void restoreGame(SGFGame game) {
        parseSGFGameInfo(game);

        MoveList<M> moveSequence = new MoveList<>();
        extractMoveList(game.getTree(), moveSequence);
        GameContext.log(1, "move sequence= " + moveSequence);
        controller_.reset();

        for (M m : moveSequence) {
            GameContext.log(1, "now making:" + m);
            controller_.makeMove(m);
        }
    }

    protected abstract SGFLoader createLoader();

    /**
     * @param game to parse
     */
    protected void parseSGFGameInfo(SGFGame game) {
        Iterator<InfoToken> e = game.getInfoTokens();
        int size = 13; // default unless specified
        while (e.hasNext()) {
            InfoToken token = e.next();
            if (token instanceof SizeToken) {
                SizeToken sizeToken = (SizeToken) token;
                GameContext.log(2, "info token size =" + sizeToken.getSize());
                size = sizeToken.getSize();
            }
        }
        ((IRectangularBoard) controller_.getBoard()).setSize(size, size);
    }

    /**
     * create a Move from an SGF token.
     *
     * @return move that was created from the token.
     */
    protected abstract M createMoveFromToken(SGFToken token);

    /**
     * Given an SGFTree and a place to store the moves of a game, this
     * method weeds out all the moves from the given SGFTree into a single
     * Vector of moves.  Variations are discarded.
     *
     * @param tree     - The SGFTree containing an SGF variation tree.
     * @param moveList - The place to store the moves for the game's main
     *                 variation.
     */
    private void extractMoveList(SGFTree tree, MoveList<M> moveList) {
        Iterator<SGFTree> trees = tree.getTrees();
        Iterator<SGFLeaf> leaves = tree.getLeaves();
        Iterator<SGFToken> tokens;
        while (leaves.hasNext()) {
            SGFToken token;
            tokens = leaves.next().getTokens();

            boolean found = false;

            // While a move token hasn't been found, and there are more tokens to
            // examine ... try and find a move token in this tree's leaves to add
            // to the collection of moves (moveList).
            while (tokens.hasNext() && !found) {
                token = tokens.next();
                found = processToken(token, moveList);
            }
        }
        // If there are variations, use the first variation, which is
        // the entire game, without extraneous variations.
        if (trees.hasNext()) {
            extractMoveList(trees.next(), moveList);
        }
    }

    /**
     * @param token    to process
     * @param moveList to add the processed token to
     * @return true if the token is an instance of PlacementToken.
     */
    protected boolean processToken(SGFToken token, MoveList<M> moveList) {
        boolean found = false;
        if (token instanceof PlacementToken) {
            M move = createMoveFromToken(token);
            GameContext.log(2, "creating move=" + move);
            moveList.add(move);
            found = true;
        } else {
            GameContext.log(0, "ignoring token " + token.getClass().getName());
        }
        return found;
    }
}
