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
import com.danxx.mdplayer.application.Common;
import com.danxx.mdplayer.meizhi.APIService;
import com.danxx.mdplayer.model.MeizhiClassify;
import com.danxx.mdplayer.utils.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import retrofit.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Danxx on 2016/6/13.
 * 图片分类
 */
public class MeizhiClassifyFragment extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BlurView blurView;
    private View rootView;

    private List<MeizhiClassify.TngouEntity> mData = new ArrayList<MeizhiClassify.TngouEntity>();
    private List<String> titles = new ArrayList<>();
    public MeizhiClassifyFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_meizhi, container, false);
        initView();
//        setupBlurView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchDataByRxjava();
    }

    private void initView(){
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
//        blurView = (BlurView) rootView.findViewById(R.id.blurView);
    }

    /**
     * 图片分类数据获取
     */
    private void fetchDataByRxjava(){
        Retrofit retrofit = RetrofitUtil.createRetrofit(Common.meizhi_api);
        APIService service = retrofit.create(APIService.class);
        Observable<MeizhiClassify> observable = service.getMeizhiClassify();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<MeizhiClassify>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(MeizhiClassify meizhiClassify) {
                    Log.d("danxx", "data size-->" + meizhiClassify.getTngou().size());
                    if (meizhiClassify.getTngou().size() > 0) {
                        mData = meizhiClassify.getTngou();
                        initViewPager();
                    }
                }
            });

    }

//    private void setupBlurView() {
//        final float radius = 16f;
//
//        final View decorView = getActivity().getWindow().getDecorView();
//        //Activity's root View. Can also be root View of your layout
//        final View rootView = decorView.findViewById(android.R.id.content);
//        //set background, if your root layout doesn't have one
//        final Drawable windowBackground = decorView.getBackground();
//
//        blurView.setupWith(rootView)
//                .windowBackground(windowBackground)
//                .blurAlgorithm(new RenderScriptBlur(getActivity(), false)) //Preferable algorithm, needs RenderScript support mode enabled
//                .blurRadius(radius);
//    }

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

}
