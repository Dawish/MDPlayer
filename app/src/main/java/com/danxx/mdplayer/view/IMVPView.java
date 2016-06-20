package com.danxx.mdplayer.view;

import com.danxx.mdplayer.model.Model;
import com.danxx.mdplayer.mvp.MvpView;

import java.util.List;

/**
 * Created by Danxx on 2016/6/17.
 * 接口
 */
public interface IMVPView extends MvpView {

    /**
     * 获取数据成功后回调
     * @param data
     */
    void getDataSuccess(List<? extends Model> data);

    /**
     * 获取数据失败
     * @param e
     */
    void getDataError(Throwable e);

    void showProgress();

    void hideProgress();
}
