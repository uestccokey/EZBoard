package cn.ezandroid.lib.board.theme;

import cn.ezandroid.lib.board.R;

/**
 * 暗色主题
 *
 * @author like
 * @date 2018-09-24
 */
public class DarkTheme extends GoTheme {

    public DarkTheme(DrawableCache cache) {
        mENName = "Dark";
        mCNName = "夜晚";
        mAuthor = "uestccokey";
        mHomepage = "https://github.com/uestccokey";
        mVersion = 1;

        mBoardTheme = new BoardTheme(cache);
        mBoardTheme.mBackground = "#FF888888";
        mBoardTheme.mBorderColor = "#FFFFFFFF";
        mBoardTheme.mBorderWidth = 3.0f;
        mBoardTheme.mLineColor = "#FFFFFFFF";
        mBoardTheme.mLineWidth = 1.5f;
        mBoardTheme.mStoneShadowOn = false;

        mBlackStoneTheme = new StoneTheme(cache);
        mBlackStoneTheme.mTextures = new String[]{"#FF000000"};
        mBlackStoneTheme.mBorderColor = "#FFFFFFFF";
        mBlackStoneTheme.mBorderWidth = 2.5f;

        mWhiteStoneTheme = new StoneTheme(cache);
        mWhiteStoneTheme.mTextures = new String[]{"#FFFFFFFF"};
        mWhiteStoneTheme.mBorderColor = "#FF000000";
        mWhiteStoneTheme.mBorderWidth = 2.5f;

        mMarkTheme = new MarkTheme();
        mMarkTheme.mHighLightColor = "#FFFF0000";

        mSoundEffect = new SoundEffect();
        mSoundEffect.mMove = ResourcePath.RAW.wrap(String.valueOf(R.raw.move_pebble));
        mSoundEffect.mTakeLess = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_less));
        mSoundEffect.mTakeMore = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_more));
    }
}
