/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.game.board.go.elements.group;

/**
 * 棋群变化监听器
 * <p>
 * 当有棋子添加或者删除时回调
 *
 * @author Barry Becker
 */
public interface GoGroupChangeListener {

    void onGoGroupChanged();
}