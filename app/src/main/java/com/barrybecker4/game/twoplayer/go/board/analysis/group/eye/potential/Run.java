/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential;

/**
 * Figure out the "eye potential" contribution from a horizontal or vertical run within a string.
 *
 * @author Barry Becker
 */
class Run {

    private int firstPos;
    private int endPos;
    private int max;
    private boolean boundedByFriends;

    /**
     * Constructor.
     * The runLength is the magnitude of the run within a potential eyespace. It may contain dead enemy stones.
     * It is the difference between the endPos and the firstPos.
     *
     * @param firstPos              either the starting row or column position.
     *                              The position before is either a friend stone or the edge.
     * @param endPos                one past the other other extreme edge of the run.
     *                              This position is either a friend stone or the edge (actually one past the last line).
     * @param max                   the last valid location before the edge.
     * @param boundedByFriendStones if true, then we have our own stones at either end and not an edge.
     */
    Run(int firstPos, int endPos, int max, boolean boundedByFriendStones) {

        this.firstPos = firstPos;
        this.endPos = endPos;
        this.max = max;
        this.boundedByFriends = boundedByFriendStones;
    }

    /**
     * @return potential score for the runlength.
     */
    float getPotential() {
        float potential;

        int runLength = endPos - firstPos;
        assert (runLength > 0);

        if ((firstPos == 1 || endPos == max + 1 || boundedByFriends)) {
            potential = getRunPotentialInternal(runLength);
        } else {
            potential = getRunPotentialToBoundary(runLength);
        }
        return potential;
    }

    /**
     * This case is where the run is next to an edge or bounded by friend stones.
     * Weight the potential more heavily.
     *
     * @return potential score for the runlength.
     */
    private float getRunPotentialInternal(int runLength) {
        float potential;
        switch (runLength) {
            case 1:
                potential = 0.25f;
                break;
            case 2:
                potential = 0.35f;
                break;
            case 3:
                potential = 0.4f;
                break;
            case 4:
                potential = 0.3f;
                break;
            case 5:
                potential = 0.2f;
                break;
            case 6:
                potential = 0.15f;
                break;
            case 7:
                potential = 0.1f;
                break;
            case 8:
                potential = 0.05f;
                break;
            default:
                potential = 0.0f;
        }
        return potential;
    }

    /**
     * A run to boundary. Less weight attributed.
     *
     * @return potential score for the runlength.
     */
    private float getRunPotentialToBoundary(int runLength) {
        float potential;
        switch (runLength) {
            case 1:
                potential = 0.05f;
                break;
            case 2:
                potential = 0.15f;
                break;
            case 3:
                potential = 0.2f;
                break;
            case 4:
                potential = 0.25f;
                break;
            case 5:
                potential = 0.2f;
                break;
            case 6:
                potential = 0.15f;
                break;
            case 7:
                potential = 0.1f;
                break;
            case 8:
                potential = 0.05f;
                break;
            default:
                potential = 0.0f;
        }
        return potential;
    }
}