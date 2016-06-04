package com.danxx.mdplayer.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.adapter.BaseRecyclerViewAdapter;
import com.danxx.mdplayer.adapter.BaseRecyclerViewHolder;
import com.danxx.mdplayer.model.VideoBean;
import com.danxx.mdplayer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {
    private String path;
    private static final int MSG_READ_FINISH = 1;
    private VideoListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView videoListView;
    private File rootFile;
    private TextView tvFilePath;

    /**包含有视频文件夹集合**/
    private List<VideoBean> videoBeans = new ArrayList<VideoBean>();
    private HandlerThread handlerThread;
    private Handler readTaskHandler;
    private Handler mainHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == MSG_READ_FINISH){
                if(videoBeans.size()>0){
                    mAdapter.setData(videoBeans);
                    mAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(VideoListActivity.this, "sorry,没有读取到视频文件!", Toast.LENGTH_LONG).show();
                }
            }
            return false;
        }
    });

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set Explode enter transition animation for current activity
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode().setDuration(800));
        getWindow().setExitTransition(null);

        setContentView(R.layout.activity_video_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        mAdapter = new VideoListAdapter();
        tvFilePath = (TextView) findViewById(R.id.tvFilePath);
        videoListView = (RecyclerView) findViewById(R.id.videoListView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        videoListView.setLayoutManager(mLayoutManager);
        videoListView.setAdapter(mAdapter);
        if(path != null && !TextUtils.isEmpty(path)){
            tvFilePath.setText(path);
            initData();
        }
    }

    private void initData(){
        rootFile = new File(path);

        handlerThread = new HandlerThread("ReadVideoFileTask");
        handlerThread.start();
        readTaskHandler = new Handler(handlerThread.getLooper());
        ReadVideoFileTask readVideoFileTask = new ReadVideoFileTask(mainHandler);
        readTaskHandler.post(readVideoFileTask);

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                VideoActivity.intentTo(VideoListActivity.this , ((VideoBean)data).path ,((VideoBean)data).name);
            }
        });
    }

    class ReadVideoFileTask implements Runnable{

        Handler mainHandler;
        public ReadVideoFileTask(Handler handler){
            this.mainHandler = handler;
        }
        @Override
        public void run() {
            eachAllMedias(rootFile);
            Log.d("danxx", "文件读取完成");
            Message msg = mainHandler.obtainMessage(MSG_READ_FINISH);
            msg.sendToTarget();
        }

        /** 遍历所有文件夹，查找出视频文件 */
        private void eachAllMedias(File f) {
            if (f != null && f.exists() && f.isDirectory()) {
                File[] files = f.listFiles();
                if (files != null) { //文件夹里面存在文件或者文件夹
                    for (File file : f.listFiles()) {
                        if (file.isDirectory()) {
                            eachAllMedias(file);
                        } else if (file.exists() && file.canRead() && FileUtils.isVideo(file)) {
                            String name = file.getName();
                            String size = FileUtils.showFileSize(file.length());
                            String path = file.getPath();
                            videoBeans.add(new VideoBean(name , path , size));
                        }
                    }
                }
            }else{
                Log.d("danxx" ,"目录直接为空");
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class VideoListAdapter extends BaseRecyclerViewAdapter<VideoBean>{

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
            ((MyViewHolder)holder).tvName.setText(getItemData(position).name);
            ((MyViewHolder)holder).tvSize.setText(getItemData(position).size);
        }

        class MyViewHolder extends BaseRecyclerViewHolder{
            View mView;
            ImageView ivPic;
            TextView tvName,tvSize,tvlength;
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
