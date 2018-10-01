package cn.ezandroid.lib.board.theme;

import cn.ezandroid.lib.board.R;

/**
 * 逼真主题
 *
 * @author like
 * @date 2018-09-24
 */
public class PhotoRealisticTheme extends GoTheme {

    public PhotoRealisticTheme(DrawableCache cache) {
        mENName = "PhotoRealistic";
        mCNName = "逼真";
        mAuthor = "yishn";
        mHomepage = "https://github.com/yishn";
        mVersion = 1;

        mBoardTheme = new BoardTheme(cache);
        mBoardTheme.mBackground = ResourcePath.ASSETS.wrap("sabaki/board.png");
        mBoardTheme.mBorderColor = "#FF000000";
        mBoardTheme.mBorderWidth = 3.0f;
        mBoardTheme.mLineColor = "#FF000000";
        mBoardTheme.mLineWidth = 1.5f;
        mBoardTheme.mStoneShadowOn = true;

        mBlackStoneTheme = new StoneTheme(cache);
        mBlackStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("photorealistic/black.png")
        };

        mWhiteStoneTheme = new StoneTheme(cache);
        mWhiteStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("photorealistic/white.png")
        };

        mMarkTheme = new MarkTheme();
        mMarkTheme.mHighLightColor = "#FFFF0000";

        mSoundEffect = new SoundEffect();
        mSoundEffect.mMove = ResourcePath.RAW.wrap(String.valueOf(R.raw.move_wood));
        mSoundEffect.mTakeLess = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_less));
        mSoundEffect.mTakeMore = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_more));
    }
}