package com.horrayzone.horrayzone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Neeraj on 19/03/17.
 */
public class City implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("leadImageUrl")
    private String url;
    @SerializedName("lat")
    private Float lat;
    @SerializedName("lng")
    private Float lng;
    @SerializedName("id")
    private String id;

    public City() {
    }

    private City(Parcel in) {
        name = in.readString();
        url = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readFloat();
        }
        if (in.readByte() == 0) {
            lng = null;
        } else {
            lng = in.readFloat();
        }
        id = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public City(String name, String url, Float lat, Float lng, String id) {
        this.name = name;
        this.url = url;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(lat);
        }
        if (lng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(lng);
        }
        dest.writeString(id);
    }
}
