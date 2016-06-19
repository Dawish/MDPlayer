package com.danxx.mdplayer.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.adapter.FragmentAdapter;
import com.danxx.mdplayer.base.BaseFragment;
import com.danxx.mdplayer.model.MeizhiClassify;
import com.danxx.mdplayer.model.Model;
import com.danxx.mdplayer.presenter.MeizhiPresenter;
import com.danxx.mdplayer.view.IMVPView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danxx on 2016/6/13.
 * 图片分类
 */
public class MeizhiClassifyFragment extends BaseFragment implements IMVPView {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View rootView;
    private List<MeizhiClassify.TngouEntity> mData = new ArrayList<MeizhiClassify.TngouEntity>();
    private List<String> titles = new ArrayList<>();
    private MeizhiPresenter meizhiPresenter;

    public MeizhiClassifyFragment() {
    }

    /**
     */
    public static MeizhiClassifyFragment newInstance(String param1, String param2) {
        MeizhiClassifyFragment fragment = new MeizhiClassifyFragment();
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
        rootView = inflater.inflate(R.layout.fragment_meizhi, container, false);
        return rootView;
    }

    @Override
    protected void initViews(View contentView) {
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initDatas() {
        meizhiPresenter = new MeizhiPresenter();
        meizhiPresenter.attachView(this);
        meizhiPresenter.getMeizhiClassifyData();
    }

    private void initViewPager(){
        int size = mData.size();
        for(int i=0;i<size;i++){
            Log.d("danxx", "name--->" + mData.get(i).name);
//            tabLayout.addTab(tabLayout.newTab().setText(mData.get(i).name));
            titles.add(mData.get(i).name);
        }

        List<Fragment> fragments = new ArrayList<>();
        for(int i=0;i<titles.size();i++){
            fragments.add(MeizhiListFragment.newInstance(mData.get(i).id));
        }

        FragmentAdapter mFragmentAdapteradapter =
                new FragmentAdapter(getActivity().getSupportFragmentManager(), fragments, titles);
        //给ViewPager设置适配器
        viewPager.setAdapter(mFragmentAdapteradapter);
        //将TabLayout和ViewPager关联起来。
        tabLayout.setupWithViewPager(viewPager);
        //给TabLayout设置适配器
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapteradapter);

    }

    @Override
    public void getDataSuccess(List<? extends Model> data) {
        Log.d("danxx","getDataSuccess-->"+data.size());
        mData = (List<MeizhiClassify.TngouEntity>) data;
        initViewPager();
    }

    @Override
    public void getDataError(Throwable e) {
        e.printStackTrace();
        Log.d("danxx","getDataError-->");
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        meizhiPresenter.detachView();
    }
}
