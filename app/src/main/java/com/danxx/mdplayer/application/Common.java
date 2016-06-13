package com.danxx.mdplayer.application;

/**
 * Created by Danxx on 2016/6/1.
 */
public class Common {
    /**eventbus的刷新Action消息类型**/
    public static final int EventBusType_Refresh = 1;
    /**eventbus的隐藏Action消息类型**/
    public static final int EventBusType_Hide = 2;
    /**eventbus的显示Action消息类型**/
    public static final int EventBusType_Show = 3;

    /**Meizhi API**/
    public static final String meizhi_api = "http://www.tngou.net/";
    /**图片分类**/
    public static final String img_classify = "http://www.tngou.net/tnfs/api/classify";
    /**图片列表**/
    public static final String img_list = "http://www.tngou.net/tnfs/api/list";
    /**图片详情**/
    public static final String img_show = "http://www.tngou.net/tnfs/api/show";
    /**图片URL拼接前缀**/
    public static final String img_url = "http://tnfs.tngou.net/img";
}
