package com.danxx.mdplayer.meizhi;


import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Zhk on 2015/12/20.
 */
public interface APIService {
    @GET("weather")
    Call<Weather> loadeather(@Query("cityname") String cityname, @Query("key") String apiKey);
    /**
     * retrofit 支持 rxjava 整合
     * 这种方法适用于新接口
     */
    @GET("weather")
    Observable<Weather> getWeatherData(@Query("cityname") String cityname, @Query("key") String apiKey);
}
