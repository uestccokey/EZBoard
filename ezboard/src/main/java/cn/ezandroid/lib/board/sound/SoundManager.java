package cn.ezandroid.lib.board.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.ezandroid.lib.board.theme.GoTheme;

/**
 * 音效管理器
 *
 * @author like
 * @date 2018-06-13
 */
public class SoundManager {

    private static SoundManager sInstance = new SoundManager();

    private SoundPool mSoundPool;
    private Map<String, Integer> mSoundMap;

    private SoundManager() {
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSoundMap = new HashMap<>();
    }

    public static SoundManager getInstance() {
        return sInstance;
    }

    private void playSoundInternal(Context context, String resourcePath) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // 获取当前音量
            float streamVolumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            // 获取系统最大音量
            float streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            // 计算得到播放音量
            float volume = streamVolumeCurrent / streamVolumeMax;
            // 调用SoundPool的play方法来播放声音文件
            mSoundPool.play(mSoundMap.get(resourcePath), volume, volume, 1, 0, 1.0f);
        }
    }

    /**
     * 播放音效
     *
     * @param context
     * @param resourcePath
     */
    public void playSound(Context context, String resourcePath) {
        if (TextUtils.isEmpty(resourcePath)) {
            return;
        }
        Integer id = mSoundMap.get(resourcePath);
        if (id == null || id == 0) {
            if (GoTheme.ResourcePath.ASSETS.belongsTo(resourcePath)) {
                try {
                    id = mSoundPool.load(context.getAssets().openFd(resourcePath), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    id = 0;
                }
            } else if (GoTheme.ResourcePath.FILE.belongsTo(resourcePath)) {
                id = mSoundPool.load(GoTheme.ResourcePath.FILE.crop(resourcePath), 1);
            } else if (GoTheme.ResourcePath.RAW.belongsTo(resourcePath)) {
                id = mSoundPool.load(context, Integer.parseInt(GoTheme.ResourcePath.RAW.crop(resourcePath)), 1);
            } else {
                id = mSoundPool.load(resourcePath, 1);
            }
            mSoundMap.put(resourcePath, id);

            mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> playSoundInternal(context, resourcePath));
        } else {
            playSoundInternal(context, resourcePath);
        }
    }
}
