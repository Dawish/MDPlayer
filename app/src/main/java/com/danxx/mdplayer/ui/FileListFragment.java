package com.danxx.mdplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.adapter.BaseRecyclerViewAdapter;
import com.danxx.mdplayer.adapter.BaseRecyclerViewHolder;
import com.danxx.mdplayer.model.FileBean;
import com.danxx.mdplayer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *视频列表页
 */
public class FileListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MSG_READ_FINISH = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayoutManager mLayoutManager;
    private FileListAdapter mAdapter;
    private View rootView;
    private RecyclerView filesListView;
    private SwipeRefreshLayout refreshLayout;

    private OnFragmentInteractionListener mListener;
    /**包含有视频文件夹集合**/
    private List<FileBean> fileBeans = new ArrayList<FileBean>();
    private HandlerThread handlerThread;
    private Handler readTaskHandler;
    private Handler mainHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == MSG_READ_FINISH){
                if(fileBeans.size()>0){
                    Toast.makeText(getActivity(), "视频文件读取到了"+ fileBeans.size(), Toast.LENGTH_LONG).show();
                    mAdapter.setData(fileBeans);
                    mAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(), "sorry,没有读取到视频文件!", Toast.LENGTH_LONG).show();
                }
                filesListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },3000);
            }
            return false;
        }
    });
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
        readTaskHandler.post(new ReadVideoDirectoryTask(getActivity(), mainHandler));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        initView();
        return rootView;
    }

    private void initView(){
        filesListView = (RecyclerView) rootView.findViewById(R.id.filesListview);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        refreshLayout.setEnabled(false);
        refreshLayout.setRefreshing(true);

        mAdapter = new FileListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        filesListView.setLayoutManager(mLayoutManager);
        filesListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                Intent intent = new Intent(getActivity(),VideoListActivity.class);
                intent.putExtra("path" ,((FileBean)data).path);
                getActivity().startActivity(intent);
            }
        });
    }

    class ReadVideoDirectoryTask implements Runnable{

        Context mContext;
        Handler mainHandler;
        File f;
        public ReadVideoDirectoryTask(Context context ,Handler handler){
            this.mContext = context;
            this.mainHandler = handler;
            f = Environment.getExternalStorageDirectory();
        }
        @Override
        public void run() {
            eachAllMedias(f);
            Log.d("danxx" ,"文件读取完成");
            Message msg = mainHandler.obtainMessage(MSG_READ_FINISH);
            msg.sendToTarget();
        }

        /** 遍历所有文件夹，查找出视频文件 */
        private void eachAllMedias(File f) {
            if (f != null && f.exists() && f.isDirectory()) {
                File[] files = f.listFiles();
                /**文件夹里视频数量**/
                int videoCount = 0;
                if (files != null) { //文件夹里面存在文件或者文件夹
                    for (File file : f.listFiles()) {
                        if (file.isDirectory()) {
                            eachAllMedias(file);
                        } else if (file.exists() && file.canRead() && FileUtils.isVideo(file)) {
                            videoCount++;
                            Log.d("danxx" ,"videoCount-->"+videoCount);
                        }
                    }
                    /**当前文件夹下包含有视频**/
                    if(videoCount > 0){
                        fileBeans.add(new FileBean(f.getPath() ,videoCount));
                    }
                }
            }else{
                Log.d("danxx" ,"目录直接为空");
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        int i=0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            viewHolder.tvPath.setText(getItemData(position).path);
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
