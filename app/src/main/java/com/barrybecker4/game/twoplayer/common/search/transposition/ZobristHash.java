/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;

import java.util.Random;

/**
 * A Zobrist Hash is a technique for creating a key for a game board configuration.
 * see http://en.wikipedia.org/wiki/Zobrist_hashing
 * The key is not guaranteed to be unique between positions, but collisions
 * should be exceedingly rare.
 * Keeps track of the random numbers to use for the state at each position.
 * No need to create more than one of these per game type.
 *
 * @author Barry Becker
 */
public final class ZobristHash {

    private long[][][] randomNumberTable;

    /** Get random 64bit integers with a seed so things are predictable. */
    private Random RANDOM;

    /** Some random key representing a passing move */
    private static final long PASS_MOVE_KEY = 3249913293473197278L;

    private TwoPlayerBoard board;

    private HashKey currentKey;

    /** only include the history when debugging. It costs a lot of memory. */
    private boolean includeHistory;

    /**
     * Create the static table of random numbers to use for the Hash from a sample board.
     *
     * @param board game board
     */
    public ZobristHash(TwoPlayerBoard board) {
        this(board, 0, false);  // always false unless debugging.
    }

    /**
     * Create the static table of random numbers to use for the Hash from a sample board.
     *
     * @param board          game board
     * @param randomSeed     having a fixed random seeds allows for the same sequence of random number each time.
     * @param includeHistory if true a lot of debug information is included in the key. Don't use in production.
     */
    public ZobristHash(TwoPlayerBoard board, int randomSeed, boolean includeHistory) {

        this.board = board;
        this.includeHistory = includeHistory;
        injectRandom(new Random(randomSeed));
    }

    /**
     * @return the current Zobrist hash key for the board state.
     */
    public HashKey getKey() {
        return currentKey;
    }

    public void applyMove(Location move, int stateIndex) {

        applyPositionToKey(move, stateIndex);
    }

    public void applyPassingMove() {
        currentKey.applyMove(null, PASS_MOVE_KEY);
    }

    public void applyMoveNumber(int number) {
        currentKey.applyMove(null, number);
    }

    /** for unit testing only so we get repeatable tests. */
    private void injectRandom(Random r) {
        RANDOM = r;
        initialize();
    }

    /**
     * The number of states for a position is the number of pieces (or combinations of pieces if more than one
     * piece type is allowed) at a given position times the number of players (always 2?).
     * So for example, in chess, the numStates would be 7 * 2 = 14. For go, its 2.
     */
    private void initialize() {
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        int numStatesPerPosition = board.getNumPositionStates();
        randomNumberTable = new long[nrows][ncols][numStatesPerPosition];

        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                for (int state = 0; state < numStatesPerPosition; state++) {
                    randomNumberTable[i][j][state] = RANDOM.nextLong();
                }
            }
        }
        currentKey = getInitialKey(board);
    }

    /**
     * @param board board state to initialize hash from.
     * @return the Zobrist Hash Key created from XORing together all the position states.
     */
    private HashKey getInitialKey(TwoPlayerBoard board) {
        currentKey = createHashKey();
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();

        for (int i = 1; i <= nrows; i++) {
            for (int j = 1; j <= ncols; j++) {
                BoardPosition pos = board.getPosition(i, j);
                if (pos.isOccupied()) {
                    applyPositionToKey(new ByteLocation(i, j), board.getStateIndex(pos));
                }
            }
        }
        return currentKey;
    }

    private HashKey createHashKey() {
        return includeHistory ? new HistoricalHashKey() : new HashKey();
    }

    /**
     * note ^ is XOR (exclusive OR) in java.
     */
    private void applyPositionToKey(Location location, int stateIndex) {

        Long specialNum = randomNumberTable[location.getRow() - 1][location.getCol() - 1][stateIndex];
        currentKey.applyMove(location, specialNum);
    }

}
