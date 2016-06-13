package com.danxx.mdplayer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danxx.mdplayer.R;
import com.danxx.mdplayer.application.Common;
import com.danxx.mdplayer.meizhi.APIService;
import com.danxx.mdplayer.model.MeizhiClassify;
import com.danxx.mdplayer.utils.RetrofitUtil;

import retrofit.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Danxx on 2016/6/13.
 */
public class MeizhiFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public MeizhiFragment() {
        // Required empty public constructor
    }

    /**
     */
    public static MeizhiFragment newInstance(String param1, String param2) {
        MeizhiFragment fragment = new MeizhiFragment();
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
        return inflater.inflate(R.layout.fragment_meizhi, container, false);
    }

    private void fetchData(){
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
                        Log.d("danxx" ,"data size-->"+meizhiClassify.getTngou().size());
                    }
                });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchData();
    }
}
