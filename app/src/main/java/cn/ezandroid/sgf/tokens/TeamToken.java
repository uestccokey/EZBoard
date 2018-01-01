package cn.ezandroid.sgf.tokens;

/**
 * 对局者队伍父类
 *
 * @author like
 * @date 2017-12-30
 */
public class TeamToken extends TextToken {

    public TeamToken() {}

    public String getTeam() {
        return getText();
    }
}
