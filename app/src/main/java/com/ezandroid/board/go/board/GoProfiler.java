/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.ezandroid.board.go.board;

import com.ezandroid.board.common.AbstractGameProfiler;

/**
 * Keep track of how much time is spent in each time critical part of the
 * computer move processing.   Singleton.
 *
 * @author Barry Becker
 */
@SuppressWarnings({"ClassWithTooManyMethods"})
public final class GoProfiler extends AbstractGameProfiler {

    /** singleton instance */
    private static GoProfiler instance;

    private static final String UPDATE_STRINGS_AFTER_REMOVE = "updating strings after remove";
    private static final String UPDATE_GROUPS_AFTER_REMOVE = "updating groups after remove";
    private static final String UPDATE_STRINGS_AFTER_MOVE = "updating strings after move";
    private static final String UPDATE_GROUPS_AFTER_MOVE = "updating groups after move";
    private static final String RECREATE_GROUPS_AFTER_MOVE = "recreating groups after move";
    private static final String RECREATE_GROUPS_AFTER_REMOVE = "recreating groups after remove";
    private static final String UPDATE_TERRITORY = "updating territory";
    private static final String ABSOLUTE_TERRITORY = "absolute territory";
    private static final String RELATIVE_TERRITORY = "relative territory";
    private static final String UPDATE_EMPTY = "updating empty regions";
    private static final String GET_GROUP_NBRS = "getting group nbrs";
    private static final String FIND_GROUPS = "finding groups";
    private static final String FIND_STRINGS = "finding strings";
    private static final String FIND_CAPTURES = "finding capturess";
    private static final String UPDATE_EYES = "update eyes";
    private static final String GET_ENEMY_GROUPS_NBRS = "get enemy group nbrs";
    private static final String COPY_BOARD = "copy go board";
    private static final String BENSONS_CHECK = "Bensons check";

    public static GoProfiler getInstance() {
        if (instance == null) {
            instance = new GoProfiler();
        }
        return instance;
    }

    private GoProfiler() {
        add(GENERATE_MOVES);
        add(UNDO_MOVE);
        add(UPDATE_GROUPS_AFTER_REMOVE, UNDO_MOVE);
        add(UPDATE_STRINGS_AFTER_REMOVE, UPDATE_GROUPS_AFTER_REMOVE);
        add(RECREATE_GROUPS_AFTER_REMOVE, UPDATE_GROUPS_AFTER_REMOVE);
        add(MAKE_MOVE);
        add(FIND_CAPTURES, MAKE_MOVE);
        add(UPDATE_STRINGS_AFTER_MOVE, MAKE_MOVE);
        add(UPDATE_GROUPS_AFTER_MOVE, MAKE_MOVE);
        add(RECREATE_GROUPS_AFTER_MOVE, UPDATE_GROUPS_AFTER_MOVE);
        add(CALC_WORTH);     // some of this goes in generate moves, some in
        add(UPDATE_TERRITORY, CALC_WORTH);
        add(ABSOLUTE_TERRITORY, UPDATE_TERRITORY);
        add(UPDATE_EYES, ABSOLUTE_TERRITORY);
        add(BENSONS_CHECK, ABSOLUTE_TERRITORY);
        add(RELATIVE_TERRITORY, UPDATE_TERRITORY);
        add(GET_ENEMY_GROUPS_NBRS, RELATIVE_TERRITORY);
        add(UPDATE_EMPTY, UPDATE_TERRITORY);
        add(GET_GROUP_NBRS);
        add(FIND_GROUPS);
        add(FIND_STRINGS);
        add(COPY_BOARD);
    }

    public void startUpdateStringsAfterRemove() {
        this.start(UPDATE_STRINGS_AFTER_REMOVE);
    }

    public void stopUpdateStringsAfterRemove() {
        this.stop(UPDATE_STRINGS_AFTER_REMOVE);
    }

    public void startUpdateGroupsAfterRemove() {
        this.start(UPDATE_GROUPS_AFTER_REMOVE);
    }

    public void stopUpdateGroupsAfterRemove() {
        this.stop(UPDATE_GROUPS_AFTER_REMOVE);
    }

    public void startRecreateGroupsAfterMove() {
        this.start(RECREATE_GROUPS_AFTER_MOVE);
    }

    public void stopRecreateGroupsAfterMove() {
        this.stop(RECREATE_GROUPS_AFTER_MOVE);
    }

    public void startRecreateGroupsAfterRemove() {
        this.start(RECREATE_GROUPS_AFTER_REMOVE);
    }

    public void stopRecreateGroupsAfterRemove() {
        this.stop(RECREATE_GROUPS_AFTER_REMOVE);
    }

    public void startUpdateStringsAfterMove() {
        this.start(UPDATE_STRINGS_AFTER_MOVE);
    }

    public void stopUpdateStringsAfterMove() {
        this.stop(UPDATE_STRINGS_AFTER_MOVE);
    }

    public void startUpdateGroupsAfterMove() {
        this.start(UPDATE_GROUPS_AFTER_MOVE);
    }

    public void stopUpdateGroupsAfterMove() {
        this.stop(UPDATE_GROUPS_AFTER_MOVE);
    }

    public void startCopyBoard() {
        this.start(COPY_BOARD);
    }

    public void stopCopyBoard() {
        this.stop(COPY_BOARD);
    }

    public void startUpdateTerritory() {
        this.start(UPDATE_TERRITORY);
    }

    public void stopUpdateTerritory() {
        this.stop(UPDATE_TERRITORY);
    }

    public void startAbsoluteTerritory() {
        this.start(ABSOLUTE_TERRITORY);
    }

    public void stopAbsoluteTerritory() {
        this.stop(ABSOLUTE_TERRITORY);
    }

    public void startRelativeTerritory() {
        this.start(RELATIVE_TERRITORY);
    }

    public void stopRelativeTerritory() {
        this.stop(RELATIVE_TERRITORY);
    }

    public void startUpdateEmpty() {
        this.start(UPDATE_EMPTY);
    }

    public void stopUpdateEmpty() {
        this.stop(UPDATE_EMPTY);
    }

    public void startGetGroupNeightbors() {
        this.start(GET_GROUP_NBRS);
    }

    public void stopGetGroupNeighbors() {
        this.stop(GET_GROUP_NBRS);
    }

    public void startFindStrings() {
        this.start(FIND_STRINGS);
    }

    public void stopFindStrings() {
        this.stop(FIND_STRINGS);
    }

    public void startFindCaptures() {
        this.start(FIND_CAPTURES);
    }

    public void stopFindCaptures() {
        this.stop(FIND_CAPTURES);
    }

    public void startUpdateEyes() {
        this.start(UPDATE_EYES);
    }

    public void stopUpdateEyes() {
        this.stop(UPDATE_EYES);
    }

    public void startGetEnemyGroupNbrs() {
        this.start(GET_ENEMY_GROUPS_NBRS);
    }

    public void stopGetEnemyGroupNbrs() {
        this.stop(GET_ENEMY_GROUPS_NBRS);
    }

    public void startBensonsCheck() {
        this.start(BENSONS_CHECK);
    }

    public void stopBensonsCheck() {
        this.stop(BENSONS_CHECK);
    }
}
