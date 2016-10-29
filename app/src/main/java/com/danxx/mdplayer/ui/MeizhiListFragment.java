package com.danxx.mdplayer.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.danxx.mdplayer.base.BaseFragment;
import com.danxx.mdplayer.model.MeizhiList;
import com.danxx.mdplayer.model.Model;
import com.danxx.mdplayer.presenter.MeizhiPresenter;
import com.danxx.mdplayer.view.IMVPView;
import com.danxx.mdplayer.widget.SpaceItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danxx on 2016/6/14.
 * 图片列表
 */
public class MeizhiListFragment extends BaseFragment implements IMVPView {
    private static final String ARG_PARAM = "id";
    /**图片分类id**/
    private int id;
    private RecyclerView listRecyclerView;
    private View rootView;
    private List<MeizhiList.TngouEntity> mData = new ArrayList<MeizhiList.TngouEntity>();
    private MyAdapter mAdapter;
    private MeizhiPresenter meizhiPresenter;

    public MeizhiListFragment() {
        // Required empty public constructor
    }

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
    protected View getContentView(LayoutInflater inflater,ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_meizhi_list, container, false);
        return rootView;
    }

    @Override
    protected void initViews(View contentView) {
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

    }

    @Override
    protected void initListeners() {
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                Intent intent = new Intent();
                intent.putExtra("url", ((MeizhiList.TngouEntity) data).getImg());
                intent.putExtra("name" ,((MeizhiList.TngouEntity) data).getTitle());
                intent.setClass(getActivity(), MeizhiDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initDatas() {
        meizhiPresenter = new MeizhiPresenter();
        meizhiPresenter.attachView(this);
        meizhiPresenter.getMeizhiListData(id);
    }


    @Override
    public void getDataSuccess(List<? extends Model> data) {
        Log.d("danxx","mezhilist getDataSuccess-->"+data.size());
        mData.clear();
        mData = (List<MeizhiList.TngouEntity>) data;
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void getDataError(Throwable e) {
        Log.e("danxx","mezhilist getDataError-->");
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        meizhiPresenter.detachView();
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
            SimpleDraweeView imageView = ((ViewHolder)holder).image;
//            Picasso.with(getActivity()).load("http://tnfs.tngou.net/image"+getItemData(position).getImg()).into(imageView);
            imageView.setImageURI("http://tnfs.tngou.net/image"+getItemData(position).getImg());
        }
        class ViewHolder extends BaseRecyclerViewHolder{
            SimpleDraweeView image;
            public ViewHolder(View itemView) {
                super(itemView);
                image = (SimpleDraweeView) itemView.findViewById(R.id.img);
            }

            @Override
            protected View getView() {
                return null;
            }
        }
    }

}
