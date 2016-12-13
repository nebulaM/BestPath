package com.github.android.bestpath.mediaPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;

import com.github.android.bestpath.R;

/**
 * http://stackoverflow.com/questions/30143255/how-to-use-one-instance-of-mediaplayer-for-several-fragments
 * http://stackoverflow.com/questions/28380525/android-one-mediaplayer-instance-singleton
 */

public class MediaPlayerSingleton extends MediaPlayer {
    private static MediaPlayer mp;

    private MediaPlayerSingleton() {}

    public static MediaPlayer getInstance() {
        synchronized (MediaPlayerSingleton.class){// if you'll be using it in moe then one thread
        if (mp== null)
            mp = new MediaPlayerSingleton();
        }

        return mp;
    }

}
