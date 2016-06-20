package com.danxx.mdplayer.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.adapter.BaseRecyclerViewAdapter;
import com.danxx.mdplayer.adapter.BaseRecyclerViewHolder;
import com.danxx.mdplayer.base.BaseFragment;
import com.danxx.mdplayer.model.CacheManager;
import com.danxx.mdplayer.model.FileBean;
import com.danxx.mdplayer.model.Model;
import com.danxx.mdplayer.presenter.VideoFilePresenter;
import com.danxx.mdplayer.view.IMVPView;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 *视频列表页
 */
public class FileListFragment extends BaseFragment implements IMVPView {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MSG_READ_FINISH = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String cacheKey = "MDPlayerCacheData";
    private LinearLayoutManager mLayoutManager;
    private FileListAdapter mAdapter;
    private View rootView;
    private RecyclerView filesListView;
    private FloatingActionMenu FAM;
    private String tempStr;
    private boolean isRefreshing;
    private int mScrollThreshold = 4;
    private VideoFilePresenter videoFilePresenter;

    private SwipeRefreshLayout refreshLayout;

    /**包含有视频文件夹集合**/
    private HandlerThread handlerThread;
    private Handler readTaskHandler;
    /**
     */
    public static FileListFragment newInstance(String param1, String param2) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(handlerThread == null){
            handlerThread = new HandlerThread("handlerThread");
        }
        handlerThread.start();
        if(readTaskHandler == null){
            readTaskHandler = new Handler(handlerThread.getLooper());
        }
//        readTaskHandler.post(new ReadVideoDirectoryTask(getActivity(), mainHandler));
        tempStr = CacheManager.getInstance().getAsString(cacheKey);
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        return rootView;
    }

    @Override
    protected void initViews(View contentView) {
        filesListView = (RecyclerView) rootView.findViewById(R.id.filesListview);
        FAM = (FloatingActionMenu) rootView.findViewById(R.id.FAM);
        FAM.hideMenu(false);
        mAdapter = new FileListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        filesListView.setLayoutManager(mLayoutManager);
        filesListView.setItemAnimator(new DefaultItemAnimator());
        filesListView.setAdapter(mAdapter);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshLayout);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        refreshLayout.setProgressViewOffset(true, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources()
                        .getDisplayMetrics()));
        refreshLayout.setProgressViewEndTarget(true, 200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FAM.showMenu(true);
            }
        }, 300);
    }

    @Override
    protected void initListeners() {
        //item点击监听
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(int position, Object data) {
                Intent intent = new Intent(getActivity(), VideoListActivity.class);
                intent.putExtra("path", ((FileBean) data).path);
                getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });
        // 下拉刷新监听
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                readTaskHandler.post(new ReadVideoDirectoryTask(getActivity(), mainHandler));
            }
        });
        // recyclerView滚动FloatingActionMenu显示隐藏监听
        filesListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (dy > 0) {
                        //onScrollUp
                        FAM.close(false);
                        FAM.hideMenu(true);
                    } else {
                        //ScrollDown
                        if (FAM.isOpened()) {
                            FAM.close(false);
                        } else {
                            FAM.showMenu(true);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initDatas() {
        /**如果有缓存数据那就先显示缓存数据**/
        if(tempStr != null && !TextUtils.isEmpty(tempStr)){
            Gson gson = new Gson();
            // json转为带泛型的list
            List<FileBean> dataList = gson.fromJson(tempStr,
                    new TypeToken<List<FileBean>>() {
                    }.getType());
            if(dataList.size()>0){
//                mAdapter.setData(dataList);
//                mAdapter.notifyDataSetChanged();
            }
        }else{
//            readTaskHandler.post(new ReadVideoDirectoryTask(getActivity(), mainHandler));
        }
        videoFilePresenter = new VideoFilePresenter();
        videoFilePresenter.attachView(this);
        videoFilePresenter.getFileData();
    }

    public void closeFAM(){
        Log.d("danxx" ,"调用关闭");
        FAM.close(false);
    }

    /**
     * 重新扫描,刷新文件
     */
    public void refresh(){
        if(!isRefreshing){
//            readTaskHandler.post(new ReadVideoDirectoryTask(getActivity(), mainHandler));
        }
    }

    @Override
    public void getDataSuccess(List<? extends Model> data) {
        Log.d("danxx" ,"getDataSuccess--->"+data.size());
        mAdapter.setData((List<FileBean>) data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void getDataError(Throwable e) {
        e.printStackTrace();
        showToast("视频文件读取失败，请稍后重试！");
    }

    @Override
    public void showProgress() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        videoFilePresenter.detachView();
    }

    class FileListAdapter extends BaseRecyclerViewAdapter<FileBean>{

        /**
         * 创建item view
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType) {

            View view  = LayoutInflater.from(getActivity()).inflate(R.layout.item_files_layout ,parent ,false);
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
            MyViewHolder viewHolder = (MyViewHolder)holder;
            viewHolder.tvPath.setText(getItemData(position).name);
            viewHolder.tvCount.setText(getItemData(position).count+"个视频文件");
        }

        class MyViewHolder extends BaseRecyclerViewHolder{
            View mView;
            TextView tvPath,tvCount;
            public MyViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                tvPath = (TextView) itemView.findViewById(R.id.tvPath);
                tvCount = (TextView) itemView.findViewById(R.id.tvCount);
            }

            @Override
            protected View getView() {
                return mView;
            }
        }
    }
}
