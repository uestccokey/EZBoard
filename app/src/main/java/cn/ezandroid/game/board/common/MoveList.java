/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.common;

import java.util.ArrayList;

/**
 * A list of game moves.
 * What kind of performance difference is there if this is a LinkedList instead of ArrayList?
 *
 * @author Barry Becker
 */
public class MoveList<M extends Move> extends ArrayList<M> {

    private final Object lock = new Object();

    /**
     * Construct set of players
     */
    public MoveList() {}

    /**
     * Copy constructor. Does not make a deep copy.
     *
     * @param list list of moves to initialize with
     */
    public MoveList(MoveList<M> list) {
        super(list);
    }

    /**
     * Copies the constituent moves as well.
     *
     * @return a deep copy of the movelist.
     */
    public MoveList<M> copy() {
        MoveList<M> copiedList = new MoveList<>();
        synchronized (lock) {
            for (M m : this) {
                copiedList.add((M) m.copy());
            }
        }
        return copiedList;
    }

    @Override
    public boolean add(M m) {
        synchronized (lock) {
            return super.add(m);
        }
    }

    @Override
    public void add(int index, M move) {
        synchronized (lock) {
            super.add(index, move);
        }
    }

    /**
     * @return the player that goes first.
     */
    public M getFirstMove() {
        return get(0);
    }

    public synchronized M getLastMove() {
        if (isEmpty()) {
            return null;
        }
        return get(size() - 1);
    }

    public M removeLast() {
        synchronized (lock) {
            return remove(this.size() - 1);
        }
    }

    /**
     * @return number of active players.
     */
    public int getNumMoves() {
        return size();
    }

    @Override
    public MoveList<M> subList(int first, int last) {
        MoveList<M> subList = new MoveList<>();
        synchronized (lock) {
            subList.addAll(super.subList(first, last));
        }
        return subList;
    }

    /**
     * @return a random move from the list.
     */
    public M getRandomMove() {
        return getRandomMove(size());
    }

    /**
     * Randomly get one of the top n moves and ignore the rest.
     * The moves are assumed ordered.
     *
     * @param ofFirstN the first n to choose randomly from.
     * @return a random move from the list.
     */
    public M getRandomMove(int ofFirstN) {
        int r = GameContext.random().nextInt(Math.min(ofFirstN, size()));
        return get(r);
    }

    /**
     * Randomly get one of the top n moves and ignore the rest.
     * The moves are assumed ordered.
     *
     * @param percentLessThanBestThresh randomly get one of the moves who's score is
     *                                  not more than this percent less that the first..
     * @return a random move from the list.
     */
    public M getRandomMoveForThresh(int percentLessThanBestThresh) {
        // first find the index of the last move that is still above the thresh
        double thresh = getFirstMove().getValue() * (1.0 - (float) percentLessThanBestThresh / 100.0);
        int ct = 1;
        M currentMove = getFirstMove();
        int numMoves = size();
        while (currentMove.getValue() > thresh && ct < numMoves) {
            currentMove = get(ct++);
        }
        int r = GameContext.random().nextInt(ct);
        return get(r);
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        int ct = 1;
        for (M m : this) {
            bldr.append(ct++).append(") ").append(m.toString()).append("\n");
        }
        return bldr.toString();
    }
}