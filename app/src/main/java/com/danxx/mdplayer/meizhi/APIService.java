package com.danxx.mdplayer.meizhi;


import com.danxx.mdplayer.model.MeizhiClassify;
import com.danxx.mdplayer.model.MeizhiList;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by danxingxi on 2016/06/13.
 */
public interface APIService {

    @GET("tnfs/api/classify")
    Observable<MeizhiClassify> getMeizhiClassify();

    @GET("tnfs/api/list")
    Observable<MeizhiList> getMeizhiList(@Query("id") String id);
}
