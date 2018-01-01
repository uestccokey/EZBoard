package cn.ezandroid.sgf.tokens;

/**
 * 生成该SGF所使用的应用程序及版本
 * <p>
 * 如 CGoban:1.6.2
 *
 * @author like
 * @date 2017-12-30
 */
public class ApplicationToken extends TextToken implements InfoToken {

    public ApplicationToken() {}

    public String getApplication() {
        return getText();
    }
}
