package com.baronzhang.android.commonbusiness.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         2017/3/13
 */
public class HouseDetail implements Parcelable {

    private String houseId;
    private String communityName;
    private int area;

    public HouseDetail() {
    }

    public HouseDetail(String houseId, String communityName, int area) {
        this.area = area;
        this.communityName = communityName;
        this.houseId = houseId;
    }

    @Override
    public String toString() {
        return "HouseDetail{" +
                "房源ID：'" + houseId + '\'' +
                ", 小区：'" + communityName + '\'' +
                ", 面积：" + area +
                '}';
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.houseId);
        dest.writeString(this.communityName);
        dest.writeInt(this.area);
    }

    protected HouseDetail(Parcel in) {
        this.houseId = in.readString();
        this.communityName = in.readString();
        this.area = in.readInt();
    }

    public static final Parcelable.Creator<HouseDetail> CREATOR = new Parcelable.Creator<HouseDetail>() {
        @Override
        public HouseDetail createFromParcel(Parcel source) {
            return new HouseDetail(source);
        }

        @Override
        public HouseDetail[] newArray(int size) {
            return new HouseDetail[size];
        }
    };
}
