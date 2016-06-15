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
    /**
     * 获取妹纸分类
     * @return
     */
    @GET("tnfs/api/classify")
    Observable<MeizhiClassify> getMeizhiClassify();

    /**
     * 获取某一个分类的妹纸列表，可以做成分页加载
     * @param id     分类id
     * @param page   页数
     * @param psize  一页的数据量
     * @return
     */
    @GET("tnfs/api/list")
    Observable<MeizhiList> getMeizhiList(@Query("id") String id , @Query("page") String page ,@Query("rows") String psize);
}
