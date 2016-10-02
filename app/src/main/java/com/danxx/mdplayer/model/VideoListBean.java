package com.danxx.mdplayer.model;

/**
 *  recyclerView item视频数据
 */
public class VideoListBean {

    private String titleName;
    private String videoUrl;
    private int id;

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
