package com.danxx.mdplayer.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.mdplayer.MDPlayer;

/**
 * 类描述：视频详情页
 *
 * @author Super南仔
 * @time 2016-9-19
 */
public class MDPlayerDetailsActivity extends AppCompatActivity implements View.OnClickListener, MDPlayer.OnNetChangeListener {

    private MDPlayer player;
    private boolean isLive;
    private EditText videoUrl;
    private Button playBtn;
    /**
     * 当视频窗口全屏的时候videoview的容器
     */
    private FrameLayout fullScreen;
    /**
     * 测试地址
     */
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_super_video_layout);
        initData();
        initView();
        initPlayer();
    }

    /**
     * 初始化相关的信息
     */
    private void initData() {
        isLive = getIntent().getBooleanExtra("isLive", false);
        url = getIntent().getStringExtra("url");
//        url = "http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=normal".trim();
        url = "http://ht.cdn.turner.com/nba/big/channels/nba_tv/2016/04/02/20160402-bop-warriors-celtics.nba_nba_1280x720.mp4".trim();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        fullScreen = (FrameLayout) findViewById(R.id.full_screen);
        videoUrl = (EditText) findViewById(R.id.videUrl);
        videoUrl.setText(url);
        playBtn = (Button) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        findViewById(R.id.tv_replay).setOnClickListener(this);
        findViewById(R.id.tv_play_location).setOnClickListener(this);
        findViewById(R.id.tv_play_switch).setOnClickListener(this);
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        player = (MDPlayer) findViewById(R.id.view_super_player);
        if (isLive) {
            player.setLive(true);//设置该地址是直播的地址
        }
        player.setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(this)//实现网络变化的回调
                .onPrepared(new MDPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */
            }
        }).onInfo(new MDPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */

            }
        }).onError(new MDPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */

            }
        }).setTitle("")//设置视频的titleName
                .play(url);//开始播放视频
        player.setScaleType(MDPlayer.SCALETYPE_FITXY);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_replay) {
            if (player != null) {
                player.play(url);
            }
        } else if (view.getId() == R.id.tv_play_location) {
            if (isLive) {
                Toast.makeText(this, "直播不支持指定播放", Toast.LENGTH_SHORT).show();
                return;
            }
            if (player != null) {
                /**
                 * 这个节点是根据视频的大小来获取的。不同的视频源节点也会不一致（一般用在退出视频播放后保存对应视频的节点从而来达到记录播放）
                 */
                player.play(url, 89528);
            }
        } else if (view.getId() == R.id.tv_play_switch) {
            /**
             * 切换视频播放源（一般是标清，高清的切换ps：由于我没有找到高清，标清的视频源，所以也是换相同的地址）
             */
            if (isLive) {
                player.playSwitch(url);
            } else {
                player.playSwitch("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high");
            }
        }else if(view.getId() == R.id.playBtn){
            if(TextUtils.isEmpty(videoUrl.getText().toString())){
                Toast.makeText(MDPlayerDetailsActivity.this, "URL地址不正确，请重试！" ,Toast.LENGTH_SHORT).show();
                return;
            }
            player.pause();
            player.play(videoUrl.getText().toString());
        }
    }

    /**
     * 网络链接监听类
     */
    @Override
    public void onWifi() {
        Toast.makeText(this, "当前网络环境是WIFI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMobile() {
        Toast.makeText(this, "当前网络环境是手机网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisConnect() {
        Toast.makeText(this, "网络链接断开", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoAvailable() {
        Toast.makeText(this, "无网络链接", Toast.LENGTH_SHORT).show();
    }


    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (player != null) {
//            player.onConfigurationChanged(newConfig);
//        }
        if (player != null) {
            /**
             * 在activity中监听到横竖屏变化时调用播放器的监听方法来实现播放器大小切换
             */
            player.onConfigurationChanged(newConfig);
            // 切换为小屏
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                fullScreen.setVisibility(View.GONE);
                fullScreen.removeAllViews();
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.video_screen);
                frameLayout.removeAllViews();
                ViewGroup last = (ViewGroup) player.getParent();//找到videoitemview的父类，然后remove
                if (last != null) {
//                    last.removeAllViews();
                    last.removeView(player);
                }
                frameLayout.addView(player);
                int mShowFlags =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                fullScreen.setSystemUiVisibility(mShowFlags);
            } else {
                //切换为全屏
                ViewGroup viewGroup = (ViewGroup) player.getParent();
                if (viewGroup == null)
                    return;
                viewGroup.removeAllViews();
                fullScreen.addView(player);
                fullScreen.setVisibility(View.VISIBLE);
                int mHideFlags =
                        View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                fullScreen.setSystemUiVisibility(mHideFlags);
            }
        } else {
            fullScreen.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

}
