package cn.ezandroid.lib.board.theme;

import cn.ezandroid.lib.board.R;

/**
 * 黑白主题
 *
 * @author like
 * @date 2018-09-23
 */
public class MonochromeTheme extends GoTheme {

    public MonochromeTheme(DrawableCache cache) {
        mENName = "Monochrome";
        mCNName = "极简";
        mAuthor = "uestccokey";
        mHomepage = "https://github.com/uestccokey";
        mVersion = 1;

        mBoardTheme = new BoardTheme(cache);
        mBoardTheme.mBackground = "#FFFFFFFF";
        mBoardTheme.mBorderColor = "#FF000000";
        mBoardTheme.mBorderWidth = 3.0f;
        mBoardTheme.mLineColor = "#FF000000";
        mBoardTheme.mLineWidth = 1.5f;
        mBoardTheme.mStoneShadowOn = true;

        mBlackStoneTheme = new StoneTheme(cache);
        mBlackStoneTheme.mTextures = new String[]{"#FF000000"};

        mWhiteStoneTheme = new StoneTheme(cache);
        mWhiteStoneTheme.mTextures = new String[]{"#FFFFFFFF"};
        mWhiteStoneTheme.mBorderColor = "#FF000000";
        mWhiteStoneTheme.mBorderWidth = 2.0f;

        mMarkTheme = new MarkTheme();
        mMarkTheme.mHighLightColor = "#FFFF0000";

        mSoundEffect = new SoundEffect();
        mSoundEffect.mMove = ResourcePath.RAW.wrap(String.valueOf(R.raw.move_pebble));
        mSoundEffect.mTakeLess = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_less));
        mSoundEffect.mTakeMore = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_more));
    }
}
