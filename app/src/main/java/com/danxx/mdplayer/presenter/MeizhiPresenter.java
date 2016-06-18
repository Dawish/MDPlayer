package com.danxx.mdplayer.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.danxx.mdplayer.application.Common;
import com.danxx.mdplayer.meizhi.APIService;
import com.danxx.mdplayer.model.MeizhiClassify;
import com.danxx.mdplayer.module.WasuCacheModule;
import com.danxx.mdplayer.mvp.BasePresenter;
import com.danxx.mdplayer.utils.RetrofitUtil;
import com.danxx.mdplayer.view.MeizhiView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Danxx on 2016/6/17.
 */
public class MeizhiPresenter extends BasePresenter<MeizhiView> {
    private static final String cacheKey = "MeizhiClassifyCacheData";
    private boolean inited_01 = false;
    private Gson gson = new Gson();
    private List<MeizhiClassify.TngouEntity> mData = new ArrayList<MeizhiClassify.TngouEntity>();
    /**
     * 获取妹纸分类信息
     */
    public void getMeizhiClassifyData(){
        Log.d("danxx", "getMeizhiClassifyData-->");

        /**先从缓存获取数据**/
        String cacheStr = WasuCacheModule.getInstance().getAsString(cacheKey);
        if(cacheStr!=null && !TextUtils.isEmpty(cacheStr)){  //要是缓存中数据就使用缓存中的数据显示
            mData = gson.fromJson(cacheStr , new TypeToken<List<MeizhiClassify.TngouEntity>>() {}.getType());
            if(mData != null && mData.size()>0){
                if(MeizhiPresenter.this.getMvpView() != null){
                    MeizhiPresenter.this.getMvpView().getDataSuccess(mData);
                    inited_01 = true;
                }
            }
        }else{
            inited_01 = false;
        }
        Retrofit retrofit = RetrofitUtil.createRetrofit(Common.meizhi_api);
        APIService service = retrofit.create(APIService.class);
        Observable<MeizhiClassify> observable = service.getMeizhiClassify();
        this.mCompositeSubscription.add( observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<MeizhiClassify>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    if(MeizhiPresenter.this.getMvpView() != null){
                        MeizhiPresenter.this.getMvpView().getDataError(e);
                    }
                }

                @Override
                public void onNext(MeizhiClassify meizhiClassify) {
                    Log.d("danxx", "onNext data size-->" + meizhiClassify.getTngou().size());
                    if (meizhiClassify.getTngou().size() > 0) {
                        if(!inited_01){  //缓存中没有数据就显示类容保存数据
                            if(MeizhiPresenter.this.getMvpView() != null){
                                MeizhiPresenter.this.getMvpView().getDataSuccess(meizhiClassify.getTngou());
                            }
                            String cacheStr = gson.toJson(meizhiClassify.getTngou());
                            if(!TextUtils.isEmpty(cacheStr)){
                                WasuCacheModule.getInstance().remove(cacheKey);
                                WasuCacheModule.getInstance().put(cacheKey ,cacheStr);
                            }
                        }else{  //缓存中有数据就更新缓存中的数据
                            String cacheStr = gson.toJson(meizhiClassify.getTngou());
                            if(!TextUtils.isEmpty(cacheStr)){
                                WasuCacheModule.getInstance().remove(cacheKey);
                                WasuCacheModule.getInstance().put(cacheKey ,cacheStr);
                            }
                        }
                    }
                }
            }));


    }

    /**
     *  获取妹纸图片列表数据
     *  @param id 妹纸分类id
     */
    public void getMeizhiListData(int id){

    }

}
