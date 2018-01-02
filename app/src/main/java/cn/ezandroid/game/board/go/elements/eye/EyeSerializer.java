// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package cn.ezandroid.game.board.go.elements.eye;

/**
 * Move serialization of eye to separate class for better reuse.
 *
 * @author Barry Becker
 */
public class EyeSerializer {

    /** The eye to serialize. */
    private final IGoEye eye;

    /**
     * Constructor.
     */
    public EyeSerializer(IGoEye eye) {
        this.eye = eye;
    }

    public String serialize() {
        StringBuilder bldr = new StringBuilder("GoEye: ");
        bldr.append(" ownedByPlayer1=").append(eye.isOwnedByPlayer1());
        bldr.append(" status=").append(eye.getStatus());
        bldr.append(" info=").append("[").append(eye.getInformation()).append("]");
        bldr.append(" num corner pts=").append(eye.getNumCornerPoints());
        bldr.append(" num edge pts=").append(eye.getNumEdgePoints());
        bldr.append(" UnconditionallyAlive=").append(eye.isUnconditionallyAlive());
        bldr.append(" num members=").append(eye.size());
        return bldr.toString();
    }
}