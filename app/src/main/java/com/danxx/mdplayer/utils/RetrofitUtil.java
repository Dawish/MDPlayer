package com.danxx.mdplayer.utils;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Danxx on 2016/6/13.
 */
public class RetrofitUtil {
    /**
     * 创建Retrofit
     * @param baseUrl
     * @return
     */
    public static Retrofit createRetrofit(String baseUrl){
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

}
