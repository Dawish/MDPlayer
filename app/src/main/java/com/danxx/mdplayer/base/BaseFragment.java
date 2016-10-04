package com.danxx.mdplayer.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Dawish on 2016/6/17.
 * Base Fragment
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    protected View contentView;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        mActivity = getActivity();
        contentView = getContentView(inflater,container);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initDatas();
        initListeners();
    }

    /**
     * set content view
     * @param inflater
     * @return
     */
    protected abstract View getContentView(LayoutInflater inflater ,ViewGroup container);

    /**
     * init view
     * @param contentView
     * @return
     */
    protected abstract void initViews(View contentView);

    /***
     * set listeners
     */
    protected abstract void initListeners();

    /**
     * init data
     */
    protected abstract void initDatas();
    /**
     * show toast
     */
    protected  void showToast(String msg){
        if(msg != null){
            Toast.makeText(mActivity , msg,Toast.LENGTH_SHORT).show();
        }
    }
}
