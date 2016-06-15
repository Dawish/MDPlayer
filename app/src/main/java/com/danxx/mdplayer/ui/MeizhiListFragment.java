package com.danxx.mdplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.adapter.BaseRecyclerViewAdapter;
import com.danxx.mdplayer.adapter.BaseRecyclerViewHolder;
import com.danxx.mdplayer.application.Common;
import com.danxx.mdplayer.meizhi.APIService;
import com.danxx.mdplayer.model.MeizhiList;
import com.danxx.mdplayer.utils.RetrofitUtil;
import com.danxx.mdplayer.widget.SpaceItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Danxx on 2016/6/14.
 * 图片列表
 */
public class MeizhiListFragment extends Fragment {
    private static final String ARG_PARAM = "id";
    /**图片分类id**/
    private int id;
    private RecyclerView listRecyclerView;

    private View rootView;
    private List<MeizhiList.TngouEntity> mData = new ArrayList<MeizhiList.TngouEntity>();
    private MyAdapter mAdapter;

    public MeizhiListFragment() {
        // Required empty public constructor
    }

    /**
     */
    public static MeizhiListFragment newInstance(int id) {
        MeizhiListFragment fragment = new MeizhiListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_meizhi_list, container, false);
        initView();
        return rootView;
    }

    private void initView(){
        listRecyclerView = (RecyclerView) rootView.findViewById(R.id.listRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        listRecyclerView.setLayoutManager(linearLayoutManager);
        listRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MyAdapter();
        //设置item间距
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_10dp);
        listRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        listRecyclerView.setAdapter(mAdapter);
        fetchDataByRxjava();
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                Intent intent = new Intent();
                intent.putExtra("url" ,((MeizhiList.TngouEntity)data).getImg());
                intent.setClass(getActivity() ,MeizhiDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Rxjava获取图片信息列表
     */
    private void fetchDataByRxjava(){
        Log.d("danxx","fetchDataByRxjava---->");
        Retrofit retrofit = RetrofitUtil.createRetrofit(Common.meizhi_api);
        APIService service = retrofit.create(APIService.class);

        Observable<MeizhiList> observable = service.getMeizhiList(String.valueOf(id));
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<MeizhiList>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.d("danxx", "list data fetch error");
            }

            @Override
            public void onNext(MeizhiList meizhiList) {
                if (meizhiList != null && meizhiList.getTngou().size() > 0) {
                    mData = meizhiList.getTngou();
                    Log.d("danxx", "list data size-->" + mData.size());
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    class MyAdapter extends BaseRecyclerViewAdapter<MeizhiList.TngouEntity>{

        /**
         * 创建item view
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_image , parent ,false);
            ViewHolder viewHolder = new ViewHolder(view);
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
            ImageView imageView = ((ViewHolder)holder).image;
            Picasso.with(getActivity()).load("http://tnfs.tngou.net/image"+getItemData(position).getImg()).into(imageView);
        }
        class ViewHolder extends BaseRecyclerViewHolder{
            ImageView image;
            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.img);
            }

            @Override
            protected View getView() {
                return null;
            }
        }
    }

}
