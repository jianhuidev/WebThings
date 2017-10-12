package com.kys26.webthings.video;

import static android.R.attr.path;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/6/3.
 */

public class VideoPlayer {
    static{
        System.loadLibrary("VideoPlayer");
    }
    public static native int play(Object surface,String path);
    public static native void stop();
    public static native void close();
}
