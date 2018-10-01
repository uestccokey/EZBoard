package cn.ezandroid.lib.board.theme;

import cn.ezandroid.lib.board.R;

/**
 * 卡通主题
 *
 * @author like
 * @date 2018-09-24
 */
public class CartoonTheme extends GoTheme {

    public CartoonTheme(DrawableCache cache) {
        mENName = "Cartoon";
        mCNName = "卡通";
        mAuthor = "geovens";
        mHomepage = "https://github.com/geovens";
        mVersion = 1;

        mBoardTheme = new BoardTheme(cache);
        mBoardTheme.mBackground = ResourcePath.ASSETS.wrap("cartoon/board.png");
        mBoardTheme.mBorderColor = "#FF000000";
        mBoardTheme.mBorderWidth = 3.0f;
        mBoardTheme.mLineColor = "#FF000000";
        mBoardTheme.mLineWidth = 1.5f;
        mBoardTheme.mStoneShadowOn = true;

        mBlackStoneTheme = new StoneTheme(cache);
        mBlackStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("cartoon/black1.png"),
                ResourcePath.ASSETS.wrap("cartoon/black2.png"),
                ResourcePath.ASSETS.wrap("cartoon/black3.png"),
                ResourcePath.ASSETS.wrap("cartoon/black4.png"),
                ResourcePath.ASSETS.wrap("cartoon/black5.png")
        };

        mWhiteStoneTheme = new StoneTheme(cache);
        mWhiteStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("cartoon/white1.png"),
                ResourcePath.ASSETS.wrap("cartoon/white2.png"),
                ResourcePath.ASSETS.wrap("cartoon/white3.png"),
                ResourcePath.ASSETS.wrap("cartoon/white4.png"),
                ResourcePath.ASSETS.wrap("cartoon/white5.png")
        };

        mMarkTheme = new MarkTheme();
        mMarkTheme.mHighLightColor = "#FFFF0000";

        mSoundEffect = new SoundEffect();
        mSoundEffect.mMove = ResourcePath.RAW.wrap(String.valueOf(R.raw.move_pebble));
        mSoundEffect.mTakeLess = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_less));
        mSoundEffect.mTakeMore = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_more));
    }
}
