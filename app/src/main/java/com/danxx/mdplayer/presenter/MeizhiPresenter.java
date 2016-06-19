package com.danxx.mdplayer.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.danxx.mdplayer.application.Common;
import com.danxx.mdplayer.meizhi.APIService;
import com.danxx.mdplayer.model.CacheManager;
import com.danxx.mdplayer.model.MeizhiClassify;
import com.danxx.mdplayer.model.MeizhiList;
import com.danxx.mdplayer.mvp.BasePresenter;
import com.danxx.mdplayer.utils.RetrofitUtil;
import com.danxx.mdplayer.view.IMVPView;
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
public class MeizhiPresenter extends BasePresenter<IMVPView> {
    private static final String cacheKey = "MeizhiClassifyCacheData";
    private boolean inited_01 = false;
    private boolean inited_02 = false;
    private Gson gson = new Gson();
    private List<MeizhiClassify.TngouEntity> MeizhiClassifyData = new ArrayList<MeizhiClassify.TngouEntity>();
    private List<MeizhiList.TngouEntity> MeizhiListData = new ArrayList<MeizhiList.TngouEntity>();

    /**
     * 获取妹纸分类信息
     */
    public void getMeizhiClassifyData(){
        Log.d("danxx", "getMeizhiClassifyData-->");

        /**先从缓存获取数据**/
        String cacheStr = CacheManager.getInstance().getAsString(cacheKey);
        if(cacheStr!=null && !TextUtils.isEmpty(cacheStr)){  //要是缓存中数据就使用缓存中的数据显示
            MeizhiClassifyData = gson.fromJson(cacheStr , new TypeToken<List<MeizhiClassify.TngouEntity>>() {}.getType());
            if(MeizhiClassifyData != null && MeizhiClassifyData.size()>0){
                if(MeizhiPresenter.this.getMvpView() != null){
                    MeizhiPresenter.this.getMvpView().getDataSuccess(MeizhiClassifyData);
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
                                CacheManager.getInstance().remove(cacheKey);
                                CacheManager.getInstance().put(cacheKey ,cacheStr);
                            }
                        }else{  //缓存中有数据就更新缓存中的数据
                            String cacheStr = gson.toJson(meizhiClassify.getTngou());
                            if(!TextUtils.isEmpty(cacheStr)){
                                CacheManager.getInstance().remove(cacheKey);
                                CacheManager.getInstance().put(cacheKey ,cacheStr);
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
    public void getMeizhiListData(final int id){
        Log.d("danxx","getMeizhiListData id--->"+id);
        final int mId = id;
        /**先从缓存获取数据**/
        String cacheStr = CacheManager.getInstance().getAsString(String.valueOf(mId));

        if(cacheStr!=null && !TextUtils.isEmpty(cacheStr)){  //要是缓存中数据就使用缓存中的数据显示
            MeizhiListData = gson.fromJson(cacheStr , new TypeToken<List<MeizhiList.TngouEntity>>() {}.getType());
            if(MeizhiListData != null && MeizhiListData.size()>0){
                if(MeizhiPresenter.this.getMvpView() != null){
                    MeizhiPresenter.this.getMvpView().getDataSuccess(MeizhiListData);
                    inited_02 = true;
                }
            }
        }else{
            inited_02 = false;
        }

        Retrofit retrofit = RetrofitUtil.createRetrofit(Common.meizhi_api);
        APIService service = retrofit.create(APIService.class);

        Observable<MeizhiList> observable = service.getMeizhiList(String.valueOf(id) ,"1" ,"40");
        this.mCompositeSubscription.add( observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<MeizhiList>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    if(MeizhiPresenter.this.getMvpView() != null){
                        MeizhiPresenter.this.getMvpView().getDataError(e);
                    }
                    Log.d("danxx", "list data fetch error");
                }

                @Override
                public void onNext(MeizhiList meizhiList) {
                    if (meizhiList != null && meizhiList.getTngou().size() > 0) {
                        if(!inited_02){  //缓存中没有数据就显示类容保存数据
                            if(MeizhiPresenter.this.getMvpView() != null){
                                MeizhiPresenter.this.getMvpView().getDataSuccess(meizhiList.getTngou());
                            }
                            String cacheStr = gson.toJson(meizhiList.getTngou());
                            if(!TextUtils.isEmpty(cacheStr)){
                                CacheManager.getInstance().remove(String.valueOf(mId));
                                CacheManager.getInstance().put(String.valueOf(mId) ,cacheStr);
                            }
                        }else{  //缓存中有数据就更新缓存中的数据
                            String cacheStr = gson.toJson(meizhiList.getTngou());
                            if(!TextUtils.isEmpty(cacheStr)){
                                CacheManager.getInstance().remove(String.valueOf(mId));
                                CacheManager.getInstance().put(String.valueOf(mId) ,cacheStr);
                            }
                        }
                    }
                }
            }));

    }

}
