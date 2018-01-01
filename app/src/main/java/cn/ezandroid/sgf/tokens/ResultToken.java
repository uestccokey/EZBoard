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

package cn.ezandroid.sgf.tokens;

import java.io.IOException;
import java.io.StreamTokenizer;

import cn.ezandroid.sgf.SGFException;

/**
 * 对局结果 (jigo, winner: score, resignation, time).
 */
public class ResultToken extends TextToken implements InfoToken {

    public final static int WHITE_WINS = 1, // 白胜
            BLACK_WINS = 2, // 黑胜
            JIGO = 3, // 和棋
            SUSPENDED = 4; // 暂停

    public final static int BY_SCORE = 1,
            BY_RESIGNATION = 2,
            BY_TIME = 3,
            BY_FORFEIT = 4,
            BY_UNKNOWN = 5,
            BY_JIGO = 6;

    // Indext into the resulting text string where the final score characters
    // are located.  This only applies if there is a definitive winner
    // (BLACK_WINS or WHITE_WINS), BY_SCORE.
    //
    private final static int SCORE_INDEX = 2;

    // By default, the winner is unknown, and the reason for winning is unknown.
    //
    private int mWinner = SUSPENDED, mReason = BY_UNKNOWN;

    // How much did the winner win by?  (Only counts when there is a winner.)
    //
    private float mScore = 0;

    public ResultToken() { }

    /**
     * The following conventions are used to denote the game result:
     * <p>
     * <PRE>
     * "0" (zero) or "Draw" for a draw (jigo),
     * "B+score" for black win
     * "W+score" for white win
     * e.g. "B+0.5", "W+64", "B+12.5"
     * <p>
     * "B+R"  | "B+Resign",  "W+R" | "W+Resign" for win by resignation
     * "B+T"  | "B+Time",    "W+T" | "W+Time" for win by time
     * "B+F"  | "B+Forfeit", "W+F" | "W+Forfeit" for win by forfeit
     * "Void" | "?" for no result/suspended play/unknown result
     * </PRE>
     */
    protected boolean parseContent(StreamTokenizer st)
            throws IOException, SGFException {
        if (!super.parseContent(st))
            return false;

        String text = getText();

        if (text.startsWith("B"))
            setWinner(BLACK_WINS);
        else if (text.startsWith("W"))
            setWinner(WHITE_WINS);
        else if (text.equals("0") ||
                text.equalsIgnoreCase("DRAW") ||
                text.equalsIgnoreCase("JIGO"))
            setWinner(JIGO);
        else {
            setWinner(SUSPENDED);
            return true;
        }

        try {
            // The third character should be R, T, or F.  If it isn't, then the
            // third character and on is a number, representing how much the
            // winner won by.
            //
            switch (text.charAt(2)) {
                case 'R':
                    setReason(BY_RESIGNATION);
                    break;
                case 'T':
                    setReason(BY_TIME);
                    break;
                case 'F':
                    setReason(BY_FORFEIT);
                    break;
                case 'J':
                case '0':
                    setReason(BY_JIGO);
                    break;

                default:
                    setReason(BY_SCORE);
                    setScore(Float.valueOf(text.substring(SCORE_INDEX, text.length())));
                    break;
            }
        } catch (Exception e) {
        }

        return true;
    }

    /**
     * Returns one of the "winner" constants (WHITE_WINS, BLACK_WINS,
     * JIGO, or SUSPENDED).
     *
     * @return Who won, tie game, or suspended (check against constants).
     */
    public int getWinner() {
        return mWinner;
    }

    private void setWinner(int winner) {
        mWinner = winner;
    }

    /**
     * Returns one of the "reason" constants (BY_SCORE, BY_TIME, BY_FORFEIT,
     * BY_RESIGNATION or BY_UNKNOWN).
     *
     * @return The reason for winning.
     */
    public int getReason() {
        return mReason;
    }

    private void setReason(int reason) {
        mReason = reason;
    }

    /**
     * Provided getReason returns BY_SCORE, this method will return how much
     * the winner won by.
     *
     * @return How many points the winner obtained.
     */
    public float getScore() {
        return mScore;
    }

    private void setScore(float score) {
        mScore = score;
    }

    /**
     * Returns a human-readable string of the game's result.
     *
     * @return English text stating the game's result.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        switch (getWinner()) {
            case WHITE_WINS:
                sb.append("White by ");
                break;
            case BLACK_WINS:
                sb.append("Black by ");
                break;
            case JIGO:
                sb.append("Jigo.");
                return sb.toString();
            case SUSPENDED:
                sb.append("Suspended.");
                return sb.toString();
        }

        switch (getReason()) {
            case BY_SCORE:
                sb.append(getScore());
                break;
            case BY_RESIGNATION:
                sb.append("resignation");
                break;
            case BY_TIME:
                sb.append("time");
                break;
            case BY_FORFEIT:
                sb.append("forfeit");
                break;
            case BY_UNKNOWN:
                sb.append("unknown reason");
                break;
            case BY_JIGO:
                sb.append("jigo");
                break;
        }

        sb.append('.');
        return sb.toString();
    }
}