package cn.ezandroid.lib.board.theme;

import cn.ezandroid.lib.board.R;

/**
 * GeQiShiTheme
 *
 * @author like
 * @date 2018-09-24
 */
public class GeQiShiTheme extends GoTheme {

    public GeQiShiTheme(GoTheme.DrawableCache cache) {
        mENName = "GeQiShi";
        mCNName = "蛤碁石";
        mAuthor = "uestccokey";
        mHomepage = "https://github.com/uestccokey";
        mVersion = 1;

        mBoardTheme = new GoTheme.BoardTheme(cache);
        mBoardTheme.mBackground = ResourcePath.ASSETS.wrap("haqishi/kaya.jpg");
        mBoardTheme.mBorderColor = "#FF000000";
        mBoardTheme.mBorderWidth = 3.0f;
        mBoardTheme.mLineColor = "#FF000000";
        mBoardTheme.mLineWidth = 1.5f;
        mBoardTheme.mStoneShadowOn = true;

        mBlackStoneTheme = new GoTheme.StoneTheme(cache);
        mBlackStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("haqishi/black.png")
        };

        mWhiteStoneTheme = new GoTheme.StoneTheme(cache);
        mWhiteStoneTheme.mTextures = new String[]{
                ResourcePath.ASSETS.wrap("haqishi/white.png")
        };

        mMarkTheme = new GoTheme.MarkTheme();
        mMarkTheme.mHighLightColor = "#FFFF0000";

        mSoundEffect = new SoundEffect();
        mSoundEffect.mMove = ResourcePath.RAW.wrap(String.valueOf(R.raw.move_wood));
        mSoundEffect.mTakeLess = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_less));
        mSoundEffect.mTakeMore = ResourcePath.RAW.wrap(String.valueOf(R.raw.take_more));
    }
}
