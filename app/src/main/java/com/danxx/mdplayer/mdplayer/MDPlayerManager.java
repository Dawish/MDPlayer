package com.danxx.mdplayer.mdplayer;

import android.content.Context;

/**
 *
 * 类描述：获取唯一的视频控制器
 *
 */
public class MDPlayerManager {
    public static MDPlayerManager videoPlayViewManage;
    private MDPlayer videoPlayView;

    private MDPlayerManager() {

    }

    public static MDPlayerManager getMDManager() {
        if (videoPlayViewManage == null) {
            videoPlayViewManage = new MDPlayerManager();
        }
        return videoPlayViewManage;
    }

    public MDPlayer initialize(Context context) {
        if (videoPlayView == null) {
            videoPlayView = new MDPlayer(context);
        }
        return videoPlayView;
    }
}
