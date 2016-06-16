package com.danxx.mdplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.danxx.mdplayer.R;
import com.danxx.mdplayer.base.BaseActivity;
import com.squareup.picasso.Picasso;

/**
 * 妹纸图片详情
 * danxingxi
 */
public class MeizhiDetailActivity extends BaseActivity {
    private PhotoView photoView;
    private String url;

    /**
     * Fill in layout id
     *
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_meizhi_detail;
    }

    /**
     * Initialize the view in the layout
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void initViews(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        photoView = (PhotoView) findViewById(R.id.img);
        // 启用图片缩放功能
        photoView.enable();
    }

    /**
     * Initialize the Activity data
     */
    @Override
    protected void initData() {

        // 禁用图片缩放功能 (默认为禁用，会跟普通的ImageView一样，缩放功能需手动调用enable()启用)
//        photoView.disenable();
        // 获取图片信息
        Info info = photoView.getInfo();
        // 从普通的ImageView中获取Info
        //Info info = PhotoView.getImageViewInfo(ImageView);
        // 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
        photoView.animaFrom(info);
        // 从现在的图片变化到所给定的图片信息，用于图片放大后点击缩小到原来的位置，具体使用可以参照demo的使用
        photoView.animaTo(info, new Runnable() {
            @Override
            public void run() {
                //动画完成监听
            }
        });
        // 获取/设置 动画持续时间
        photoView.setAnimaDuring(200);
        int d = photoView.getAnimaDuring();
        // 获取/设置 最大缩放倍数
        photoView.setMaxScale( 4.0f );
        float maxScale = photoView.getMaxScale();
        // 设置动画的插入器
//        photoView.setInterpolator(Interpolator interpolator);
        if(!TextUtils.isEmpty(url)){
            Picasso.with(MeizhiDetailActivity.this).load("http://tnfs.tngou.net/image"+url).into(photoView);
        }
    }

    /**
     * Initialize the toolbar in the layout
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    /**
     * Initialize the View of the listener
     */
    @Override
    protected void initListeners() {

    }

}
