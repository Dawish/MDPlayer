package com.danxx.mdplayer.model;

/**
 * Created by Danxx on 2016/5/31.
 * 视频类
 */
public class VideoBean extends Model {

    public String name;
    public String path;
    public String size;

    public VideoBean(String name, String size) {
        this.name = name;
        this.size = size;
    }

    public VideoBean(String name, String path, String size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }


}
