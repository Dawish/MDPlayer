package com.danxx.mdplayer.model;

import java.util.List;

/**
 * Created by Danxx on 2016/6/13.
 * 妹纸分类bean
 */
public class MeizhiClassify extends Model{

    public boolean status;

    public List<TngouEntity> tngou;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<TngouEntity> getTngou() {
        return tngou;
    }

    public void setTngou(List<TngouEntity> tngou) {
        this.tngou = tngou;
    }

    public static class TngouEntity extends Model{
        public String description;
        public int id;
        public String keywords;
        public String name;
        public int seq;
        public String title;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }


        public void setId(int id) {
            this.id = id;
        }


        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }


        public void setName(String name) {
            this.name = name;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
