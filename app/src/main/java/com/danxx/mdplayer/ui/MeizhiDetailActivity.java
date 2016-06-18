package com.danxx.mdplayer.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.danxx.mdplayer.R;
import com.danxx.mdplayer.base.BaseActivity;
import com.danxx.mdplayer.presenter.PicturePresenter;
import com.danxx.mdplayer.utils.ImageTools;
import com.danxx.mdplayer.view.PictureView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

/**
 * 妹纸图片详情
 * danxingxi
 */
public class MeizhiDetailActivity extends BaseActivity implements PictureView{
    private PhotoView photoView;
    private String url;
    private PicturePresenter picturePresenter;
    private Bitmap bitmap;
    private String name;
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
        name = intent.getStringExtra("name");
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
        setTitle(name);
        if(!TextUtils.isEmpty(url)){
            Picasso.with(MeizhiDetailActivity.this).load("http://tnfs.tngou.net/image" + url).into(photoView);
        }
        this.picturePresenter = new PicturePresenter();
        this.picturePresenter.attachView(this);
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
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MeizhiDetailActivity.this).setMessage("确定保存图片到本地?")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(photoView.getDrawable() != null){
                            Log.d("danxx" ,"开始保存！！");
                            bitmap = ImageTools.drawableToBitmap(photoView.getDrawable());
                            if(bitmap!=null){
                                picturePresenter.savePicture(bitmap,MeizhiDetailActivity.this,getApplication());
                            }else{
                                showToast("保存失败！");
                            }
                            dialog.dismiss();
                        }else{
                            showToast("图片还在加载中，请稍后重试！");
                            dialog.dismiss();
                        }
                    }
                }).show();
                return false;
            }
        });
    }

    /**
     * 图片保存到本地成功后的回调
     *
     * @param path 保存在本地的path
     */
    @Override
    public void onSaveSuccess(String path) {
        showToast("图片保存在:"+path);
    }

    /**
     * 图片保存到本地失败后回调
     *
     * @param e
     */
    @Override
    public void onSaveError(Throwable e) {
        e.printStackTrace();
        showToast("图片保存失败！");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        picturePresenter.detachView();
    }


    /*********
     * Umeng *
     *********/

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /*********
     * Umeng *
     *********/
}
