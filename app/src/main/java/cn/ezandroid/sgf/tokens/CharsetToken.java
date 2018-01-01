package cn.ezandroid.sgf.tokens;

/**
 * 字符集
 *
 * @author like
 * @date 2017-12-29
 */
public class CharsetToken extends TextToken implements InfoToken {

    public CharsetToken() {}

    public String getCharset() {
        return getText();
    }
}
