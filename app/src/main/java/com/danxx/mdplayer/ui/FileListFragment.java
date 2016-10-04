package com.danxx.mdplayer.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
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
import com.danxx.mdplayer.model.FileBean;
import com.danxx.mdplayer.model.Model;
import com.danxx.mdplayer.presenter.VideoFilePresenter;
import com.danxx.mdplayer.view.IMVPView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

/**
 *视频列表页
 */
public class FileListFragment extends BaseFragment implements IMVPView {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayoutManager mLayoutManager;
    private FileListAdapter mAdapter;
    private View rootView;
    private RecyclerView filesListView;
    private FloatingActionMenu FAM;
    private boolean isRefreshing;
    private int mScrollThreshold = 4;
    private VideoFilePresenter videoFilePresenter;
    private FloatingActionButton menuHistory,menuNetwork;
    private SwipeRefreshLayout refreshLayout;
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
        menuHistory = (FloatingActionButton) contentView.findViewById(R.id.menuHistory);
        menuNetwork = (FloatingActionButton) contentView.findViewById(R.id.menuNetwork);

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
        refreshLayout.setProgressViewEndTarget(false, 200);
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
                refresh();
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
        menuNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFAM();
                Intent intent = new Intent(getContext(), MDPlayerDetailsActivity.class);
                startActivity(intent);
                /*final AppCompatEditText editText = new AppCompatEditText(getActivity());
                editText.setHint("请输入视频播放地址");
                new AlertDialog.Builder(getActivity()).setView(editText)
                    .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setMessage("打开网络视频").setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showToast(editText.getText().toString());
                        dialogInterface.dismiss();
                    }
                }).show();*/
            }
        });
    }

    @Override
    protected void initDatas() {
        videoFilePresenter = new VideoFilePresenter();
        videoFilePresenter.attachView(this);
        videoFilePresenter.getFileData(false);
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
            videoFilePresenter.getFileData(true);
        }
    }

    @Override
    public void getDataSuccess(List<? extends Model> data) {
        Log.d("danxx" ,"getDataSuccess--->"+data.size());
        mAdapter.setData((List<FileBean>) data);
        mAdapter.notifyDataSetChanged();
        showToast("读取到了"+data.size()+"个目录");
    }

    @Override
    public void getDataError(Throwable e) {
        e.printStackTrace();
        showToast("视频文件读取失败，请稍后重试！");
    }

    @Override
    public void showProgress() {
        isRefreshing  = true;
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        isRefreshing = false;
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
