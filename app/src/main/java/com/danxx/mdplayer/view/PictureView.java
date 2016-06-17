package com.danxx.mdplayer.view;

import com.danxx.mdplayer.mvp.MvpView;

/**
 * Created by Danxx on 2016/6/17.
 * 妹纸详情接口
 */
public interface PictureView extends MvpView {

    /**
     * 图片保存到本地成功后的回调
     * @param path  保存在本地的path
     */
    void onSaveSuccess(String path);

    /**
     * 图片保存到本地失败后回调
     * @param e
     */
    void onSaveError(Throwable e);
}
