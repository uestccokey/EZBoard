package cn.ezandroid.lib.board.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 主题模型
 *
 * @author like
 * @date 2018-09-22
 */
public class GoTheme {

    public static final int INVALID_VALUE = 0;

    public String mENName;
    public String mCNName;
    public int mVersion;
    public String mAuthor;
    public String mHomepage;
    public String mDescription;
    public String mExtra;

    public BoardTheme mBoardTheme;
    public StoneTheme mBlackStoneTheme;
    public StoneTheme mWhiteStoneTheme;
    public MarkTheme mMarkTheme;
    public SoundEffect mSoundEffect;

    /**
     * 音效
     */
    public static class SoundEffect {
        public String mMove; // 落子
        public String mTakeLess; // 提子数<=2
        public String mTakeMore; // 提子数>2
    }

    /**
     * 棋盘
     */
    public static class BoardTheme {
        private Drawable mBackgroundDrawable = null;

        public String mBackground; // #开头表示颜色值，否则为资源图片名称
        public String mLineColor;
        public float mLineWidth;
        public String mBorderColor;
        public float mBorderWidth;
        public boolean mStoneShadowOn;

        protected DrawableCache mDrawableCache;

        public BoardTheme(DrawableCache cache) {
            mDrawableCache = cache;
        }

        public Drawable getBackground() {
            if (mBackgroundDrawable == null && !TextUtils.isEmpty(mBackground)) {
                mBackgroundDrawable = mDrawableCache.load(mBackground);
            }
            return mBackgroundDrawable;
        }

        public int getLineColor() {
            if (!TextUtils.isEmpty(mLineColor)) {
                return Color.parseColor(mLineColor);
            }
            return INVALID_VALUE;
        }

        public int getBorderColor() {
            if (!TextUtils.isEmpty(mBorderColor)) {
                return Color.parseColor(mBorderColor);
            }
            return INVALID_VALUE;
        }
    }

    /**
     * 棋子
     */
    public static class StoneTheme {
        private List<Drawable> mTextureDrawableList = new ArrayList<>();

        public String[] mTextures; // #开头表示颜色值，否则为资源图片名称，支持多个纹理
        public String mBorderColor;
        public float mBorderWidth;

        protected DrawableCache mDrawableCache;

        public StoneTheme(DrawableCache cache) {
            mDrawableCache = cache;
        }

        public List<Drawable> getTextures() {
            if (mTextureDrawableList.isEmpty() && mTextures != null) {
                for (String texture : mTextures) {
                    if (!TextUtils.isEmpty(texture)) {
                        mTextureDrawableList.add(mDrawableCache.load(texture));
                    }
                }
            }
            return mTextureDrawableList;
        }

        public Drawable getRandomTexture() {
            List<Drawable> drawables = getTextures();
            if (!drawables.isEmpty()) {
                Random random = new Random();
                return drawables.get(random.nextInt(drawables.size()));
            } else {
                return null;
            }
        }

        public int getBorderColor() {
            if (!TextUtils.isEmpty(mBorderColor)) {
                return Color.parseColor(mBorderColor);
            }
            return INVALID_VALUE;
        }
    }

    /**
     * 标记
     */
    public static class MarkTheme {
        public String mHighLightColor;

        public int getHighLightColor() {
            if (!TextUtils.isEmpty(mHighLightColor)) {
                return Color.parseColor(mHighLightColor);
            }
            return INVALID_VALUE;
        }
    }

    /**
     * Drawable缓存
     */
    public static class DrawableCache {

        private LruCache<String, Drawable> mLruCache;
        private Context mContext;

        public DrawableCache(Context context, int size) {
            mLruCache = new LruCache<String, Drawable>(size) {
                @Override
                protected int sizeOf(String key, Drawable value) {
                    if (value instanceof ColorDrawable) {
                        return 4; // Int占四字节
                    } else if (value instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) value).getBitmap();
                        return bitmap.getRowBytes() * bitmap.getHeight();
                    } else {
                        return 0; // 不应该出现
                    }
                }
            };
            mContext = context;
        }

        public Drawable load(String key) {
            Drawable value = mLruCache.get(key);
            if (value == null) {
                if (key.startsWith("#")) {
                    value = new ColorDrawable(Color.parseColor(key));
                } else {
                    value = new BitmapDrawable(mContext.getResources(), loadBitmap(mContext, key));
                }
                mLruCache.put(key, value);
            }
            return value;
        }

        public void put(String key, Drawable value) {
            mLruCache.put(key, value);
        }

        /**
         * 加载图片
         *
         * @param context
         * @param path
         * @return
         */
        private Bitmap loadBitmap(Context context, String path) {
            InputStream in = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inDither = false;
                options.inInputShareable = true;
                options.inPurgeable = true;
                if (ResourcePath.ASSETS.belongsTo(path)) {
                    in = context.getAssets().open(ResourcePath.ASSETS.crop(path));
                    return BitmapFactory.decodeStream(in, null, options);
                } else if (ResourcePath.FILE.belongsTo(path)) {
                    return BitmapFactory.decodeFile(ResourcePath.FILE.crop(path), options);
                } else if (ResourcePath.DRAWABLE.belongsTo(path)) {
                    return BitmapFactory.decodeResource(context.getResources(),
                            Integer.parseInt(ResourcePath.DRAWABLE.crop(path)), options);
                } else {
                    return BitmapFactory.decodeFile(path, options);
                }
            } catch (IOException | OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    /**
     * 路径前缀枚举
     */
    public enum ResourcePath {

        FILE("file://"), // file:// + /sdcard/demo.png
        ASSETS("file:///android_asset/"), // file:///android_asset/ + demo.png
        DRAWABLE("drawable://"), // drawable:// + 3243342
        RAW("raw://"), // raw:// + 3243342
        UNKNOWN("");

        private String mScheme;

        ResourcePath(String scheme) {
            this.mScheme = scheme;
        }

        public boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.US).startsWith(this.mScheme);
        }

        public String wrap(String path) {
            return this.mScheme + path;
        }

        public String crop(String uri) {
            if (!this.belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected path [%2$s]", new Object[]{uri, this.mScheme}));
            } else {
                return uri.substring(this.mScheme.length());
            }
        }
    }
}
