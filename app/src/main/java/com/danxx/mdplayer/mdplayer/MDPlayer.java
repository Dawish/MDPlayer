package com.danxx.mdplayer.mdplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.danxx.mdplayer.utils.DeviceUtils;
import com.danxx.mdplayer.utils.NetUtils;
import com.danxx.mdplayer.widget.media.IRenderView;
import com.danxx.mdplayer.widget.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import com.danxx.mdplayer.R;

/**
 *
 * 类描述：视频播放控制类
 *
 */
public class MDPlayer extends RelativeLayout {
	/**
	 * fitParent:scale the video uniformly (maintain the video's aspect ratio)
	 * so that both dimensions (width and height) of the video will be equal to
	 * or **less** than the corresponding dimension of the view. like
	 * ImageView's `CENTER_INSIDE`.等比缩放,画面填满view。
	 */
	public static final String SCALETYPE_FITPARENT = "fitParent";
	/**
	 * fillParent:scale the video uniformly (maintain the video's aspect ratio)
	 * so that both dimensions (width and height) of the video will be equal to
	 * or **larger** than the corresponding dimension of the view .like
	 * ImageView's `CENTER_CROP`.等比缩放,直到画面宽高都等于或小于view的宽高。
	 */
	public static final String SCALETYPE_FILLPARENT = "fillParent";
	/**
	 * wrapContent:center the video in the view,if the video is less than view
	 * perform no scaling,if video is larger than view then scale the video
	 * uniformly so that both dimensions (width and height) of the video will be
	 * equal to or **less** than the corresponding dimension of the view.
	 * 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中。
	 */
	public static final String SCALETYPE_WRAPCONTENT = "wrapContent";
	/**
	 * fitXY:scale in X and Y independently, so that video matches view
	 * exactly.不剪裁,非等比例拉伸画面填满整个View
	 */
	public static final String SCALETYPE_FITXY = "fitXY";
	/**
	 * 16:9:scale x and y with aspect ratio 16:9 until both dimensions (width
	 * and height) of the video will be equal to or **less** than the
	 * corresponding dimension of the view.不剪裁,非等比例拉伸画面到16:9,并完全显示在View中。
	 */
	public static final String SCALETYPE_16_9 = "16:9";
	/**
	 * 4:3:scale x and y with aspect ratio 4:3 until both dimensions (width and
	 * height) of the video will be equal to or **less** than the corresponding
	 * dimension of the view.不剪裁,非等比例拉伸画面到4:3,并完全显示在View中。
	 */
	public static final String SCALETYPE_4_3 = "4:3";
	private static final int MESSAGE_SHOW_PROGRESS = 1;
	private static final int MESSAGE_FADE_OUT = 2;
	private static final int MESSAGE_SEEK_NEW_POSITION = 3;
	private static final int MESSAGE_HIDE_CENTER_BOX = 4;
	private static final int MESSAGE_RESTART_PLAY = 5;
	private Activity activity;
	private Context context;
	private View contentView;
	private IjkVideoView videoView;
	private SeekBar seekBar;
	private AudioManager audioManager;
	private int mMaxVolume;
	private boolean playerSupport;
	private String url;
	private Query $;
	private int STATUS_ERROR = -1;
	private int STATUS_IDLE = 0;
	private int STATUS_LOADING = 1;
	private int STATUS_PLAYING = 2;
	private int STATUS_PAUSE = 3;
	private int STATUS_COMPLETED = 4;
	private long pauseTime;
	private int status = STATUS_IDLE;
	private boolean isLive = false;// 是否为直播
	private boolean isShowCenterControl = false;// 是否显示中心控制器
	private boolean isHideControl = false;//是否隐藏视频控制栏
	private boolean isShowTopControl = true;//是否显示头部显示栏，true：竖屏也显示 false：竖屏不显示，横屏显示
	private boolean isSupportGesture = false;//是否至此手势操作，false ：小屏幕的时候不支持，全屏的支持；true : 小屏幕还是全屏都支持
	private boolean isPrepare = false;// 是否已经初始化播放
	private boolean isNetListener = true;// 是否添加网络监听 (默认是监听)
	// 网络监听回调
	private NetChangeReceiver netChangeReceiver;
	private OnNetChangeListener onNetChangeListener;

	private OrientationEventListener orientationEventListener;
	private int defaultTimeout = 3000;
	private int screenWidthPixels;

	public MDPlayer(Context context) {
		this(context, null);
	}

	public MDPlayer(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MDPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		activity = (Activity) this.context;
		//初始化view和其他相关的
		initView();
	}

	/**
	 * 相应点击事件
	 */
	private final View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.view_jky_player_fullscreen) {
				toggleFullScreen();
			} else if (v.getId() == R.id.app_video_play) {
				doPauseResume();
				show(defaultTimeout);
			} else if (v.getId() == R.id.view_jky_player_center_play) {
				// videoView.seekTo(0);
				// videoView.start();
				doPauseResume();
				show(defaultTimeout);
			} else if (v.getId() == R.id.app_video_finish) {
				if (!fullScreenOnly && !portrait) {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else {
					activity.finish();
				}
			} else if (v.getId() == R.id.view_jky_player_tv_continue) {
				isNetListener = false;// 取消网络的监听
				$.id(R.id.view_jky_player_tip_control).gone();
				play(url, currentPosition);
			}
		}
	};
	private boolean isShowing;
	private boolean portrait;
	private float brightness = -1;
	private int volume = -1;
	private long newPosition = -1;
	private long defaultRetryTime = 5000;
	private OnErrorListener onErrorListener;
	private Runnable oncomplete = new Runnable() {
		@Override
		public void run() {
		}
	};
	private OnInfoListener onInfoListener;
	private OnPreparedListener onPreparedListener;

	/**
	 * try to play when error(only for live video)
	 *
	 * @param defaultRetryTime
	 *            millisecond,0 will stop retry,default is 5000 millisecond
	 */
	public void setDefaultRetryTime(long defaultRetryTime) {
		this.defaultRetryTime = defaultRetryTime;
	}

	private int currentPosition;
	private boolean fullScreenOnly;

	public MDPlayer setTitle(CharSequence title) {
		$.id(R.id.app_video_title).text(title);
		return this;
	}

	private void doPauseResume() {
		if (status == STATUS_COMPLETED) {
			if (isShowCenterControl) {
				$.id(R.id.view_jky_player_center_control).visible();
			}
			videoView.seekTo(0);
			videoView.start();
		} else if (videoView.isPlaying()) {
			statusChange(STATUS_PAUSE);
			videoView.pause();
		} else {
			videoView.start();
		}
		updatePausePlay();
	}

	/**
	 * 更新暂停状态的控件显示
	 */
	private void updatePausePlay() {
		$.id(R.id.view_jky_player_center_control).visibility(
				isShowCenterControl ? View.VISIBLE : View.GONE);
		if (videoView.isPlaying()) {
			$.id(R.id.app_video_play).image(R.drawable.ic_pause);
			$.id(R.id.view_jky_player_center_play).image(
					R.drawable.ic_center_pause);
		} else {
			$.id(R.id.app_video_play).image(R.drawable.ic_play);
			$.id(R.id.view_jky_player_center_play).image(
					R.drawable.ic_center_play);
		}
	}

	/**
	 * @param timeout
	 */
	private void show(int timeout) {
		if(isHideControl){
			showBottomControl(false);
			showCenterControl(false);
			showTopControl(false);
			return;
		}
		if (!isShowing && isPrepare) {
			if(!isShowTopControl && portrait){
				showTopControl(false);
			} else {
				showTopControl(true);
			}
			if (isShowCenterControl) {
				$.id(R.id.view_jky_player_center_control).visible();
			}
			showBottomControl(true);
			if (!fullScreenOnly) {
				$.id(R.id.view_jky_player_fullscreen).visible();
			}
			isShowing = true;
		}
		updatePausePlay();
		handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
		handler.removeMessages(MESSAGE_FADE_OUT);
		if (timeout != 0) {
			handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_FADE_OUT),
					timeout);
		}
	}

	/**
	 * 隐藏显示底部控制栏
	 *
	 * @param show
	 *            true ： 显示 false ： 隐藏
	 */
	private void showBottomControl(boolean show) {
		$.id(R.id.app_video_bottom_box).visibility(
				show ? View.VISIBLE : View.GONE);
		if (isLive) {// 直播需要隐藏和显示一些底部的一些控件
			$.id(R.id.app_video_play).gone();
			$.id(R.id.app_video_currentTime).gone();
			$.id(R.id.app_video_endTime).gone();
			$.id(R.id.app_video_seekBar).gone();
			$.id(R.id.view_jky_player_tv_number).visible();
		}

	}

	/**
	 * 隐藏和显示头部的一些控件
	 *
	 * @param show
	 */
	private void showTopControl(boolean show) {
		$.id(R.id.app_video_top_box)
				.visibility(show ? View.VISIBLE : View.GONE);
		if (isLive) {// 对直播特定控件隐藏显示

		}
	}

	private long duration;
	private boolean instantSeeking;
	private boolean isDragging;
	private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {
			if (!fromUser)
				return;
			$.id(R.id.view_jky_player_tip_control).gone();// 移动时隐藏掉状态image
			int newPosition = (int) ((duration * progress * 1.0) / 1000);
			String time = generateTime(newPosition);
			if (instantSeeking) {
				videoView.seekTo(newPosition);
			}
			$.id(R.id.app_video_currentTime).text(time);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			isDragging = true;
			show(3600000);
			handler.removeMessages(MESSAGE_SHOW_PROGRESS);
			if (instantSeeking) {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
			}
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (!instantSeeking) {
				videoView
						.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
			}
			show(defaultTimeout);
			handler.removeMessages(MESSAGE_SHOW_PROGRESS);
			audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			isDragging = false;
			handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
		}
	};

	@SuppressWarnings("HandlerLeak")
	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_FADE_OUT:
					hide(false);
					break;
				case MESSAGE_HIDE_CENTER_BOX:
					$.id(R.id.app_video_volume_box).gone();
					$.id(R.id.app_video_brightness_box).gone();
					$.id(R.id.app_video_fastForward_box).gone();
					break;
				case MESSAGE_SEEK_NEW_POSITION:
					if (!isLive && newPosition >= 0) {
						videoView.seekTo((int) newPosition);
						newPosition = -1;
					}
					break;
				case MESSAGE_SHOW_PROGRESS:
					setProgress();
					if (!isDragging && isShowing) {
						msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
						sendMessageDelayed(msg, 1000);
						updatePausePlay();
					}
					break;
				case MESSAGE_RESTART_PLAY:
					play(url);
					break;
			}
		}
	};

	/**
	 * 初始化视图
	 */
	public void initView() {
		try {
			IjkMediaPlayer.loadLibrariesOnce(null);
			IjkMediaPlayer.native_profileBegin("libijkplayer.so");
			playerSupport = true;
		} catch (Throwable e) {
			Log.e("GiraffePlayer", "loadLibraries error", e);
		}
		screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
		$ = new Query(activity);
		contentView = View.inflate(context, R.layout.view_super_player, this);
		videoView = (IjkVideoView) contentView.findViewById(R.id.video_view);
		videoView
				.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(IMediaPlayer mp) {
						statusChange(STATUS_COMPLETED);
						oncomplete.run();
					}
				});
		videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(IMediaPlayer mp, int what, int extra) {
				statusChange(STATUS_ERROR);
				if (onErrorListener != null) {
					onErrorListener.onError(what, extra);
				}
				return true;
			}
		});
		videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
			@Override
			public boolean onInfo(IMediaPlayer mp, int what, int extra) {
				switch (what) {
					case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
						statusChange(STATUS_LOADING);
						break;
					case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
						statusChange(STATUS_PLAYING);
						break;
					case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
						// 显示 下载速度
						// Toast.makeText(activity,"download rate:" +
						// extra,Toast.LENGTH_SHORT).show();
						break;
					case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
						statusChange(STATUS_PLAYING);
						break;
				}
				if (onInfoListener != null) {
					onInfoListener.onInfo(what, extra);
				}
				return false;
			}
		});
		videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(IMediaPlayer mp) {
				isPrepare = true;

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						hide(false);
						show(defaultTimeout);
					}
				}, 500);
				if (onPreparedListener != null) {
					onPreparedListener.onPrepared();
				}
			}
		});

		seekBar = (SeekBar) contentView.findViewById(R.id.app_video_seekBar);
		seekBar.setMax(1000);
		seekBar.setOnSeekBarChangeListener(mSeekListener);
		$.id(R.id.app_video_play).clicked(onClickListener);
		$.id(R.id.view_jky_player_fullscreen).clicked(onClickListener);
		$.id(R.id.app_video_finish).clicked(onClickListener);
		$.id(R.id.view_jky_player_center_play).clicked(onClickListener);
		$.id(R.id.view_jky_player_tv_continue).clicked(onClickListener);

		audioManager = (AudioManager) activity
				.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		final GestureDetector gestureDetector = new GestureDetector(activity,
				new PlayerGestureListener());

		View liveBox = contentView.findViewById(R.id.app_video_box);
		liveBox.setClickable(true);
		liveBox.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (gestureDetector.onTouchEvent(motionEvent))
					return true;

				// 处理手势结束
				switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_UP:
						endGesture();
						break;
				}

				return false;
			}
		});

		/**
		 * 监听手机重力感应的切换屏幕的方向
		 */
		orientationEventListener = new OrientationEventListener(activity) {
			@Override
			public void onOrientationChanged(int orientation) {
				if (orientation >= 0 && orientation <= 30 || orientation >= 330
						|| (orientation >= 150 && orientation <= 210)) {
					// 竖屏
					if (portrait) {
						activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
						orientationEventListener.disable();
					}
				} else if ((orientation >= 90 && orientation <= 120)
						|| (orientation >= 240 && orientation <= 300)) {
					if (!portrait) {
						activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
						orientationEventListener.disable();
					}
				}
			}
		};

		if (fullScreenOnly) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		portrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		hideAll();
		if (!playerSupport) {
			showStatus(activity.getResources().getString(R.string.not_support),
					"重试");
		}
	}

	/**
	 * 手势结束
	 */
	private void endGesture() {
		volume = -1;
		brightness = -1f;
		if (newPosition >= 0) {
			handler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
			handler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
		}
		handler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
		handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);

	}

	/**
	 * 视频播放状态的改变
	 * @param newStatus
     */
	private void statusChange(int newStatus) {
		status = newStatus;
		if (!isLive && newStatus == STATUS_COMPLETED) {// 当视频播放完成的时候
			handler.removeMessages(MESSAGE_SHOW_PROGRESS);
			hideAll();
			if (isShowCenterControl) {
				$.id(R.id.view_jky_player_center_control).visible();
			}
		} else if (newStatus == STATUS_ERROR) {
			handler.removeMessages(MESSAGE_SHOW_PROGRESS);
			hideAll();
			if (isLive) {
				showStatus(
						activity.getResources().getString(
								R.string.small_problem), "重试");
				if (defaultRetryTime > 0) {
					handler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY,
							defaultRetryTime);
				}
			} else {
				showStatus(
						activity.getResources().getString(
								R.string.small_problem), "重试");
			}
		} else if (newStatus == STATUS_LOADING) {
			hideAll();
			$.id(R.id.app_video_loading).visible();
		} else if (newStatus == STATUS_PLAYING) {
			hideAll();
		}

	}

	/**
	 * 隐藏全部的控件
	 */
	private void hideAll() {
		$.id(R.id.view_jky_player_center_control).gone();
		$.id(R.id.app_video_loading).gone();
		$.id(R.id.view_jky_player_fullscreen).invisible();
		$.id(R.id.view_jky_player_tip_control).gone();
		showBottomControl(false);
		showTopControl(false);
	}

	/**
	 * 暂停
	 */
	public void onPause() {
		pauseTime = System.currentTimeMillis();
		show(0);// 把系统状态栏显示出来
		if (status == STATUS_PLAYING) {
			videoView.pause();
			if (!isLive) {
				currentPosition = videoView.getCurrentPosition();
			}
		}
	}

	public void onResume() {
		pauseTime = 0;
		if (status == STATUS_PLAYING) {
			if (isLive) {
				videoView.seekTo(0);
			} else {
				if (currentPosition > 0) {
					videoView.seekTo(currentPosition);
				}
			}
			videoView.start();
		}
	}

	/**
	 * 监听全屏跟非全屏
	 * @param newConfig
     */
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
		doOnConfigurationChanged(portrait);
	}

	/**
	 * 当竖横屏切换时处理视频窗口
	 * @param portrait
     */
	private void doOnConfigurationChanged(final boolean portrait) {
		if (videoView != null && !fullScreenOnly) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					setFullScreen(!portrait);
					if (portrait) {
						int screenWidth = DeviceUtils.deviceWidth(activity);
						ViewGroup.LayoutParams layoutParams = getLayoutParams();
						activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						layoutParams.width = screenWidth;
						layoutParams.height = screenWidth * 9 / 16;
						setLayoutParams(layoutParams);
						requestLayout();
					} else {
						int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
						int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
						ViewGroup.LayoutParams layoutParams = getLayoutParams();
						layoutParams.height = heightPixels;
						layoutParams.width = widthPixels;
						setLayoutParams(layoutParams);
					}
					updateFullScreenButton();
				}
			});
			orientationEventListener.enable();
		}
	}

	/**
	 * ActionBar的隐藏和显示
	 * @param fullScreen
     */
	// TODO
	private void tryFullScreen(boolean fullScreen) {
		if (activity instanceof AppCompatActivity) {
			ActionBar supportActionBar = ((AppCompatActivity) activity)
					.getSupportActionBar();
			if (supportActionBar != null) {
				if (fullScreen) {
					supportActionBar.hide();
				} else {
					supportActionBar.show();
				}
			}
		}
		setFullScreen(fullScreen);
	}
	// TODO 这个是防止项目没有引用v7包
//	private void tryFullScreen(boolean fullScreen) {
//		if (activity instanceof Activity) {
//			android.app.ActionBar supportActionBar = ((Activity) activity)
//					.getActionBar();
//			if (supportActionBar != null) {
//				if (fullScreen) {
//					supportActionBar.hide();
//				} else {
//					supportActionBar.show();
//				}
//			}
//		}
//		setFullScreen(fullScreen);
//	}

	/**
	 * 主动使窗口横竖屏切换
	 * @param fullScreen
     */
	private void setFullScreen(boolean fullScreen) {
		if (activity != null) {
			WindowManager.LayoutParams attrs = activity.getWindow()
					.getAttributes();
			if (fullScreen) {
				attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
				activity.getWindow().setAttributes(attrs);
				activity.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			} else {
				attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				activity.getWindow().setAttributes(attrs);
				activity.getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			}
		}

	}

	/**
	 * 在activity中的onDestroy中需要回调
	 */
	public void onDestroy() {
		unregisterNetReceiver();// 取消网络变化的监听
		orientationEventListener.disable();
		handler.removeCallbacksAndMessages(null);
		videoView.stopPlayback();
	}

	/**
	 * 显示错误信息
	 *
	 * @param statusText
	 *            错误提示
	 * @param btnText
	 *            错误按钮提示
	 */
	private void showStatus(String statusText, String btnText) {
		$.id(R.id.view_jky_player_tip_control).visible();
		$.id(R.id.view_jky_player_tip_text).text(statusText);
		$.id(R.id.view_jky_player_tv_continue).text(btnText);
		isPrepare = false;// 设置点击不能出现控制栏
	}

	/**
	 * 开始播放
	 *
	 * @param url
	 *            播放视频的地址
	 */
	public void play(String url) {
		this.url = url;
		play(url, 0);
	}

	/**
	 *
	 * @param url
	 *            开始播放(可播放指定位置)
	 * @param currentPosition
	 *            指定位置的大小(0-1000)
	 * @see （一般用于记录上次播放的位置或者切换视频源）
	 */
	public void play(String url, int currentPosition) {
		this.url = url;
		if (!isNetListener) {// 如果设置不监听网络的变化，则取消监听网络变化的广播
			unregisterNetReceiver();
		} else {
			// 注册网路变化的监听
			registerNetReceiver();
		}
		if(videoView!=null){
			release();
		}
		if (isNetListener
				&& (NetUtils.getNetworkType(activity) == 2 || NetUtils
				.getNetworkType(activity) == 4)) {// 手机网络的情况下
			$.id(R.id.view_jky_player_tip_control).visible();
		} else {
			if (playerSupport) {
				$.id(R.id.app_video_loading).visible();
				videoView.setVideoPath(url);
				if (isLive) {
					videoView.seekTo(0);
				} else {
					seekTo(currentPosition, true);
				}
				videoView.start();
			}
		}
	}

	/**
	 * 播放切换视频源地址
	 *
	 * @param url
	 */
	public void playSwitch(String url) {
		this.url = url;
		if (videoView.isPlaying()) {
			getCurrentPosition();
		}
		play(url, (int) currentPosition);
	}

	/**
	 * 格式化显示的时间
	 * @param time
	 * @return
     */
	private String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
				seconds) : String.format("%02d:%02d", minutes, seconds);
	}

	private int getScreenOrientation() {
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int orientation;
		// if the device's natural orientation is portrait:
		if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
				&& height > width
				|| (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
				&& width > height) {
			switch (rotation) {
				case Surface.ROTATION_0:
					orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					break;
				case Surface.ROTATION_90:
					orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					break;
				case Surface.ROTATION_180:
					orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
					break;
				case Surface.ROTATION_270:
					orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
					break;
				default:
					orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					break;
			}
		}
		else {
			switch (rotation) {
				case Surface.ROTATION_0:
					orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					break;
				case Surface.ROTATION_90:
					orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					break;
				case Surface.ROTATION_180:
					orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
					break;
				case Surface.ROTATION_270:
					orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
					break;
				default:
					orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					break;
			}
		}

		return orientation;
	}

	/**
	 * 滑动改变声音大小
	 *
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (volume == -1) {
			volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (volume < 0)
				volume = 0;
		}
		hide(true);

		int index = (int) (percent * mMaxVolume) + volume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		int i = (int) (index * 1.0 / mMaxVolume * 100);
		String s = i + "%";
		if (i == 0) {
			s = "off";
		}
		// 显示
		$.id(R.id.app_video_volume_icon).image(
				i == 0 ? R.drawable.ic_volume_off_white_36dp
						: R.drawable.ic_volume_up_white_36dp);
		$.id(R.id.app_video_brightness_box).gone();
		$.id(R.id.app_video_volume_box).visible();
		$.id(R.id.app_video_volume_box).visible();
		$.id(R.id.app_video_volume).text(s).visible();
	}

	private void onProgressSlide(float percent) {
		long position = videoView.getCurrentPosition();
		long duration = videoView.getDuration();
		long deltaMax = Math.min(100 * 1000, duration - position);
		long delta = (long) (deltaMax * percent);

		newPosition = delta + position;
		if (newPosition > duration) {
			newPosition = duration;
		} else if (newPosition <= 0) {
			newPosition = 0;
			delta = -position;
		}
		int showDelta = (int) delta / 1000;
		if (showDelta != 0) {
			$.id(R.id.app_video_fastForward_box).visible();
			String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
			$.id(R.id.app_video_fastForward).text(text + "s");
			$.id(R.id.app_video_fastForward_target).text(
					generateTime(newPosition) + "/");
			$.id(R.id.app_video_fastForward_all).text(generateTime(duration));
		}
	}

	/**
	 * 滑动改变亮度
	 *
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (brightness < 0) {
			brightness = activity.getWindow().getAttributes().screenBrightness;
			if (brightness <= 0.00f) {
				brightness = 0.50f;
			} else if (brightness < 0.01f) {
				brightness = 0.01f;
			}
		}
		Log.d(this.getClass().getSimpleName(), "brightness:" + brightness
				+ ",percent:" + percent);
		$.id(R.id.app_video_brightness_box).visible();
		WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
		lpa.screenBrightness = brightness + percent;
		if (lpa.screenBrightness > 1.0f) {
			lpa.screenBrightness = 1.0f;
		} else if (lpa.screenBrightness < 0.01f) {
			lpa.screenBrightness = 0.01f;
		}
		$.id(R.id.app_video_brightness).text(
				((int) (lpa.screenBrightness * 100)) + "%");
		activity.getWindow().setAttributes(lpa);

	}

	private long setProgress() {
		if (isDragging) {
			return 0;
		}

		long position = videoView.getCurrentPosition();
		long duration = videoView.getDuration();
		if (seekBar != null) {
			if (duration > 0) {
				long pos = 1000L * position / duration;
				seekBar.setProgress((int) pos);
			}
			int percent = videoView.getBufferPercentage();
			seekBar.setSecondaryProgress(percent * 10);
		}

		this.duration = duration;
		$.id(R.id.app_video_currentTime).text(generateTime(position));
		$.id(R.id.app_video_endTime).text(generateTime(this.duration));
		return position;
	}

	public void hide(boolean force) {
		if (force || isShowing) {
			handler.removeMessages(MESSAGE_SHOW_PROGRESS);
			showBottomControl(false);
			$.id(R.id.view_jky_player_center_control).gone();
			showTopControl(false);
			$.id(R.id.view_jky_player_fullscreen).invisible();
			isShowing = false;
		}
	}

	/**
	 * 更新全屏按钮
	 */
	private void updateFullScreenButton() {
		if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 全屏幕
			$.id(R.id.view_jky_player_fullscreen).image(
					R.drawable.shrink_fullscreen);
			$.id(R.id.view_jky_player_iv_share).gone();
			$.id(R.id.view_jky_play_iv_setting).visible();
		} else {
			$.id(R.id.view_jky_player_fullscreen).image(R.drawable.enlarge_fullscreen);
			$.id(R.id.view_jky_player_iv_share).visible();
			$.id(R.id.view_jky_play_iv_setting).gone();
		}
	}

	/**
	 * 设置只能全屏
	 *
	 * @param fullScreenOnly
	 *            true ： 只能全屏 false ： 小屏幕显示
	 */
	public void setFullScreenOnly(boolean fullScreenOnly) {
		this.fullScreenOnly = fullScreenOnly;
		tryFullScreen(fullScreenOnly);
		if (fullScreenOnly) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
		updateFullScreenButton();
	}

	/**
	 * using constants in GiraffePlayer,eg: GiraffePlayer.SCALETYPE_FITPARENT
	 *
	 * @param scaleType
	 */
	public void setScaleType(String scaleType) {
		if (SCALETYPE_FITPARENT.equals(scaleType)) {
			videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
		} else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
			videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
		} else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
			videoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
		} else if (SCALETYPE_FITXY.equals(scaleType)) {
			videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
		} else if (SCALETYPE_16_9.equals(scaleType)) {
			videoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
		} else if (SCALETYPE_4_3.equals(scaleType)) {
			videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
		}
	}

	/**
	 * 是否显示左上导航图标(一般有actionbar or appToolbar时需要隐藏)
	 *
	 * @param show
	 */
	public void setShowNavIcon(boolean show) {
		$.id(R.id.app_video_finish).visibility(show ? View.VISIBLE : View.GONE);
	}

	public void start() {
		videoView.start();
	}

	public void pause() {
		videoView.pause();
	}

	/**
	 * 当用户点击返回键时调用
	 * @return
     */
	public boolean onBackPressed() {
		if (!fullScreenOnly
				&& getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			return true;
		}
		return false;
	}

	class Query {
		private final Activity activity;
		private View view;

		public Query(Activity activity) {
			this.activity = activity;
		}

		public Query id(int id) {
			view = contentView.findViewById(id);
			return this;
		}

		public Query image(int resId) {
			if (view instanceof ImageView) {
				((ImageView) view).setImageResource(resId);
			}
			return this;
		}

		public Query visible() {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			return this;
		}

		public Query gone() {
			if (view != null) {
				view.setVisibility(View.GONE);
			}
			return this;
		}

		public Query invisible() {
			if (view != null) {
				view.setVisibility(View.INVISIBLE);
			}
			return this;
		}

		public Query clicked(View.OnClickListener handler) {
			if (view != null) {
				view.setOnClickListener(handler);
			}
			return this;
		}

		public Query text(CharSequence text) {
			if (view != null && view instanceof TextView) {
				((TextView) view).setText(text);
			}
			return this;
		}

		public Query visibility(int visible) {
			if (view != null) {
				view.setVisibility(visible);
			}
			return this;
		}
	}

	public class PlayerGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		private boolean firstTouch;
		private boolean volumeControl;
		private boolean toSeek;

		/**
		 * 双击
		 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (!isPrepare) {// 视频没有初始化点击屏幕不起作用
				return false;
			}
			videoView.toggleAspectRatio();
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			firstTouch = true;
			return super.onDown(e);

		}

		/**
		 * 滑动
		 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY) {
			if(!isSupportGesture && portrait){
				return super.onScroll(e1, e2, distanceX, distanceY);
			}
			float mOldX = e1.getX(), mOldY = e1.getY();
			float deltaY = mOldY - e2.getY();
			float deltaX = mOldX - e2.getX();
			if (firstTouch) {
				toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
				volumeControl = mOldX > screenWidthPixels * 0.5f;
				firstTouch = false;
			}

			if (toSeek) {
				if (!isLive) {
					onProgressSlide(-deltaX / videoView.getWidth());
				}
			} else {
				float percent = deltaY / videoView.getHeight();
				if (volumeControl) {
					onVolumeSlide(percent);
				} else {
					onBrightnessSlide(percent);
				}
			}
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (!isPrepare) {// 视频没有初始化点击屏幕不起作用
				return false;
			}
			if (isShowing) {
				hide(false);
			} else {
				show(defaultTimeout);
			}
			return true;
		}
	}

	/**
	 * is player support this device
	 *
	 * @return
	 */
	public boolean isPlayerSupport() {
		return playerSupport;
	}

	/**
	 * 是否正在播放
	 *
	 * @return
	 */
	public boolean isPlaying() {
		return videoView != null ? videoView.isPlaying() : false;
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		if(videoView.isPlaying()) {
			videoView.stopPlayback();
		}
	}

	/**
	 * 停止播放，（仅仅对列表播放）
	 */
	public void stopPlayVideo() {
		if (this != null) {
			videoView.stopPlayback();
		}
	}

	/**
	 * 释放资源
	 */
	public void release() {
		videoView.release(true);
		videoView.seekTo(0);
	}

	/**
	 * 显示列表中的视图(仅仅对列表播放的一个方法)
	 * @param viewId
     */
	public void showView(int viewId) {
		ViewGroup last = (ViewGroup) this.getParent();//找到videoitemview的父类，然后remove
		if (last != null) {
			last.removeAllViews();
			View itemView = (View) last.getParent();
			if (itemView != null) {
				View viewById = itemView.findViewById(viewId);
				if (viewById != null)
					viewById.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * seekTo position
	 *
	 * @param msec
	 *            millisecond
	 */
	public MDPlayer seekTo(int msec, boolean showControlPanle) {
		videoView.seekTo(msec);
		if (showControlPanle) {
			show(defaultTimeout);
		}
		return this;
	}

	/**
	 * 快退快退（取决于传进来的percent）
	 * @param percent
	 * @return
     */
	public MDPlayer forward(float percent) {
		if (isLive || percent > 1 || percent < -1) {
			return this;
		}
		onProgressSlide(percent);
		showBottomControl(true);
		handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
		endGesture();
		return this;
	}

	/**
	 * 获取当前播放的currentPosition
	 *
	 * @return
	 */
	public int getCurrentPosition() {
		if (!isLive) {
			currentPosition = videoView.getCurrentPosition();
		} else {// 直播
			currentPosition = -1;
		}
		return currentPosition;
	}

	/**
	 * 获取视频的总长度
	 *
	 * @return
	 */
	public int getDuration() {
		return videoView.getDuration();
	}

	public MDPlayer playInFullScreen(boolean fullScreen) {
		if (fullScreen) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			updateFullScreenButton();
		}
		return this;
	}

	/**
	 * 用户主动点击大小屏的切换
	 * 设置播放视频的是否是全屏
	 */
	public void toggleFullScreen() {
		/***
		 * 就算只用户主动切换大小，也是去是activity转向来实现的
		 */
		if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 转小屏
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			if(isShowTopControl){
				showTopControl(false);
			}
		} else {// 转全屏
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			showTopControl(true);
		}
		updateFullScreenButton();
	}

	public interface OnErrorListener {
		void onError(int what, int extra);
	}

	public interface OnInfoListener {
		void onInfo(int what, int extra);
	}

	public interface OnPreparedListener {
		void onPrepared();
	}

	public MDPlayer onError(OnErrorListener onErrorListener) {
		this.onErrorListener = onErrorListener;
		return this;
	}

	public MDPlayer onComplete(Runnable complete) {
		this.oncomplete = complete;
		return this;
	}

	public MDPlayer onInfo(OnInfoListener onInfoListener) {
		this.onInfoListener = onInfoListener;
		return this;
	}

	public MDPlayer onPrepared(OnPreparedListener onPreparedListener) {
		this.onPreparedListener = onPreparedListener;
		return this;
	}

	// 网络监听的回调
	public MDPlayer setOnNetChangeListener(
			OnNetChangeListener onNetChangeListener) {
		this.onNetChangeListener = onNetChangeListener;
		return this;
	}

	/**
	 * set is live (can't seek forward)
	 *
	 * @param isLive
	 * @return
	 */
	public MDPlayer setLive(boolean isLive) {
		this.isLive = isLive;
		return this;
	}

	public MDPlayer toggleAspectRatio() {
		if (videoView != null) {
			videoView.toggleAspectRatio();
		}
		return this;
	}

	/**
	 * 注册网络监听器
	 */
	private void registerNetReceiver() {
		if (netChangeReceiver == null) {
			IntentFilter filter = new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION);
			netChangeReceiver = new NetChangeReceiver();
			activity.registerReceiver(netChangeReceiver, filter);
		}
	}

	/**
	 * 销毁网络监听器
	 */
	private void unregisterNetReceiver() {
		if (netChangeReceiver != null) {
			activity.unregisterReceiver(netChangeReceiver);
			netChangeReceiver = null;
		}
	}

	public interface OnNetChangeListener {
		// wifi
		void onWifi();

		// 手机
		void onMobile();

		// 网络断开
		void onDisConnect();

		// 网路不可用
		void onNoAvailable();
	}

	/********************************* 网络变化监听 ************************************/
	public class NetChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (onNetChangeListener == null) {
				return;
			}
			if (NetUtils.getNetworkType(activity) == 3) {// 网络是WIFI
				onNetChangeListener.onWifi();
			} else if (NetUtils.getNetworkType(activity) == 2
					|| NetUtils.getNetworkType(activity) == 4) {// 网络不是手机网络或者是以太网
				// TODO 更新状态是暂停状态
				statusChange(STATUS_PAUSE);
				videoView.pause();
				updatePausePlay();
				$.id(R.id.app_video_loading).gone();
				onNetChangeListener.onMobile();
				showStatus(
						activity.getResources().getString(
								R.string.player_not_wifi), "继续");
			} else if (NetUtils.getNetworkType(activity) == 1) {// 网络链接断开
				onPause();
				onNetChangeListener.onDisConnect();
			} else {
				onNetChangeListener.onNoAvailable();
			}
		}
	}

	/*************************************** 对外调用的方法 ********************/

	/**
	 * 是否显示中心控制器
	 *
	 * @param isShow
	 *            true ： 显示 false ： 不显示
	 */
	public MDPlayer showCenterControl(boolean isShow) {
		this.isShowCenterControl = isShow;
		return this;
	}

	public MDPlayer setShowTopControl(boolean isShowTopControl){
		this.isShowTopControl = isShowTopControl;
		return this;
	}

	/**
	 * 点击的时候是否显示控制栏
	 * @param isHideControl
	 * @return
	 */
	public MDPlayer setHideControl(boolean isHideControl){
		this.isHideControl = isHideControl;
		return this;
	}

	/**
	 * 设置播放视频是否有网络变化的监听
	 *
	 * @param isNetListener
	 *            true ： 监听 false ： 不监听
	 * @return
	 */
	public MDPlayer setNetChangeListener(boolean isNetListener) {
		this.isNetListener = isNetListener;
		return this;
	}

	/**
	 * 设置小屏幕是否支持手势操作（默认false）
	 * @param isSupportGesture
	 * 				true : 支持（小屏幕支持，大屏幕支持）
	 * 				false ：不支持（小屏幕不支持,大屏幕支持）
	 * @return
     */
	public MDPlayer setSupportGesture(boolean isSupportGesture){
		this.isSupportGesture = isSupportGesture;
		return this;
	}

	/**
	 * 获取到当前播放的状态
	 * @return
	 */
	public int getVideoStatus() {
		return videoView.getCurrentState();
	}

	/**
	 * 获得某个控件
	 *
	 * @param ViewId
	 * @return
	 */
	public View getView(int ViewId) {
		return activity.findViewById(ViewId);
	}

}
