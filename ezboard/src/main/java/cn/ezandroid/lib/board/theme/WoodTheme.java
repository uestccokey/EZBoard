package cn.ezandroid.lib.board.theme;

import cn.ezandroid.lib.board.R;

/**
 * 木纹主题
 *
 * @author like
 * @date 2018-09-24
 */
public class WoodTheme extends GoTheme {

    public WoodTheme(DrawableCache cache) {
        mENName = "Wood";
        mCNName = "木纹";
        mAuthor = "geovens";
        mHomepage = "https://github.com/geovens";
        mVersion = 1;

        mBoardTheme = new BoardTheme(cache);
        mBoardTheme.mBackground = ResourcePath.ASSETS.wrap("wood/board.png");
        mBoardTheme.mBorderColor = "#FF000000";
        mBoardTheme.mBorderWidth = 3.0f;
        mBoardTheme.mLineColor = "#FF000000";
        mBoardTheme.mLineWidth = 1.5f;
        mBoardTheme.mStoneShadowOn = true;

        mBlackStoneTheme = new StoneTheme(cache);
        mBlackStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("wood/black1.png"),
                ResourcePath.ASSETS.wrap("wood/black2.png"),
                ResourcePath.ASSETS.wrap("wood/black3.png"),
                ResourcePath.ASSETS.wrap("wood/black4.png"),
                ResourcePath.ASSETS.wrap("wood/black5.png")
        };

        mWhiteStoneTheme = new StoneTheme(cache);
        mWhiteStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("wood/white1.png"),
                ResourcePath.ASSETS.wrap("wood/white2.png"),
                ResourcePath.ASSETS.wrap("wood/white3.png"),
                ResourcePath.ASSETS.wrap("wood/white4.png"),
                ResourcePath.ASSETS.wrap("wood/white5.png")
        };

        mMarkTheme = new MarkTheme();
        mMarkTheme.mHighLightColor = "#FFFF0000";

        mSoundEffect = new SoundEffect();
        mSoundEffect.mMove = ResourcePath.RAW.wrap(String.valueOf(R.raw.move_wood));
        mSoundEffect.mTakeLess = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_less));
        mSoundEffect.mTakeMore = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_more));
    }
}
