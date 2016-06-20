package com.danxx.mdplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.adapter.BaseRecyclerViewAdapter;
import com.danxx.mdplayer.adapter.BaseRecyclerViewHolder;
import com.danxx.mdplayer.base.BaseActivity;
import com.danxx.mdplayer.model.Model;
import com.danxx.mdplayer.model.VideoBean;
import com.danxx.mdplayer.presenter.VideoFilePresenter;
import com.danxx.mdplayer.utils.FileUtils;
import com.danxx.mdplayer.utils.RxUtil;
import com.danxx.mdplayer.view.IMVPView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class VideoListActivity extends BaseActivity implements IMVPView {
    private String path;
    private static final int MSG_READ_FINISH = 1;
    private VideoListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView videoListView;
    private File rootFile;
    private TextView tvFilePath;
    private VideoFilePresenter videoFilePresenter;

    /**
     * 包含有视频文件夹集合
     **/
    private List<VideoBean> videoBeans = new ArrayList<VideoBean>();


    /**
     * Fill in layout id
     *
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_list;
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
        path = intent.getStringExtra("path");
        tvFilePath = (TextView) findViewById(R.id.tvFilePath);
        videoListView = (RecyclerView) findViewById(R.id.videoListView);
        mAdapter = new VideoListAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        videoListView.setLayoutManager(mLayoutManager);
        videoListView.setAdapter(mAdapter);
    }

    /**
     * Initialize the Activity data
     */
    @Override
    protected void initData() {
        videoFilePresenter = new VideoFilePresenter();
        videoFilePresenter.attachView(this);
        if (path != null && !TextUtils.isEmpty(path)) {
            tvFilePath.setText(path);
            rootFile = new File(path);
//            ReadVideoFileByRxjava();
            videoFilePresenter.getVideoData(rootFile);
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
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                VideoActivity.intentTo(VideoListActivity.this, ((VideoBean) data).path, ((VideoBean) data).name);
            }
        });
    }

    /**
     * 参考:http://blog.csdn.net/job_hesc/article/details/46242117
     */
    private void ReadVideoFileByRxjava() {
        Observable.just(rootFile)
            .flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    return RxUtil.listFiles(file);
                }
            })
            .subscribe(
                new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        Log.d("danxx", "onCompleted");
                        if (videoBeans.size() > 0) {
//                            mAdapter.setData(videoBeans);
//                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(VideoListActivity.this, "sorry,没有读取到视频文件!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(File file) {
                        String name = file.getName();
                        String size = FileUtils.showFileSize(file.length());
                        String path = file.getPath();
                        videoBeans.add(new VideoBean(name, path, size));
                        Log.d("danxx", "name--->" + name);
                    }
                }
            );
    }

    /**
     * 获取数据成功后回调
     *
     * @param data
     */
    @Override
    public void getDataSuccess(List<? extends Model> data) {
        mAdapter.setData((List<VideoBean>) data);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取数据失败
     *
     * @param e
     */
    @Override
    public void getDataError(Throwable e) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoFilePresenter.detachView();
    }

    class VideoListAdapter extends BaseRecyclerViewAdapter<VideoBean> {

        /**
         * 创建item view
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(VideoListActivity.this).inflate(R.layout.item_videos_layout, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        /**
         * 绑定数据
         *
         * @param holder
         * @param position
         */
        @Override
        protected void bindData(BaseRecyclerViewHolder holder, int position) {
            ((MyViewHolder) holder).tvName.setText(getItemData(position).name);
            ((MyViewHolder) holder).tvSize.setText(getItemData(position).size);
        }

        class MyViewHolder extends BaseRecyclerViewHolder {
            View mView;
            ImageView ivPic;
            TextView tvName, tvSize, tvlength;

            public MyViewHolder(View itemView) {
                super(itemView);
                ivPic = (ImageView) itemView.findViewById(R.id.ivPic);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvSize = (TextView) itemView.findViewById(R.id.tvSize);
                tvlength = (TextView) itemView.findViewById(R.id.tvlength);
                mView = itemView;
            }

            @Override
            protected View getView() {
                return mView;
            }
        }
    }
}
