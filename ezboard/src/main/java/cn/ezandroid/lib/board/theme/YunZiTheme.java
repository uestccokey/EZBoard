package cn.ezandroid.lib.board.theme;

import cn.ezandroid.lib.board.R;

/**
 * 云子主题
 *
 * @author like
 * @date 2018-09-24
 */
public class YunZiTheme extends GoTheme {

    public YunZiTheme(DrawableCache cache) {
        mENName = "YunZi";
        mCNName = "云子";
        mAuthor = "uestccokey";
        mHomepage = "https://github.com/uestccokey";
        mVersion = 1;

        mBoardTheme = new BoardTheme(cache);
        mBoardTheme.mBackground = ResourcePath.ASSETS.wrap("yunzi/oak.jpg");
        mBoardTheme.mBorderColor = "#FF000000";
        mBoardTheme.mBorderWidth = 3.0f;
        mBoardTheme.mLineColor = "#FF000000";
        mBoardTheme.mLineWidth = 1.5f;
        mBoardTheme.mStoneShadowOn = true;

        mBlackStoneTheme = new StoneTheme(cache);
        mBlackStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("yunzi/black.png")
        };

        mWhiteStoneTheme = new StoneTheme(cache);
        mWhiteStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("yunzi/white.png")
        };

        mMarkTheme = new MarkTheme();
        mMarkTheme.mHighLightColor = "#FFFF0000";

        mSoundEffect = new SoundEffect();
        mSoundEffect.mMove = ResourcePath.RAW.wrap(String.valueOf(R.raw.move_wood));
        mSoundEffect.mTakeLess = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_less));
        mSoundEffect.mTakeMore = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_more));
    }
}
