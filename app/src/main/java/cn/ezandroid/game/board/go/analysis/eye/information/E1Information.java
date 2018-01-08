/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.analysis.eye.information;

/**
 * 单一空间的眼位
 *
 * @author Barry Becker
 */
public class E1Information extends AbstractEyeSubtypeInformation {

    public E1Information() {
        initialize(false, 1);
    }

    @Override
    public String getTypeName() {
        return "E1";
    }
}