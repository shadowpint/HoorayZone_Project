package com.horrayzone.horrayzone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Neeraj on 19/03/17.
 */
public class Event implements Parcelable {
    @SerializedName("city_id")
    private String city;
    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    @SerializedName("tags")
    private String tag;
    @SerializedName("date")
    private String date;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private Float lat;
    @SerializedName("lng")
    private Float lng;
    @SerializedName("description")
    private String description;
    @SerializedName("leadImageUrl")
    private String leadImageUrl;
    @SerializedName("id")
    private String id;

    public Event() {
    }

    protected Event(Parcel in) {
        city = in.readString();
        code = in.readString();
        name = in.readString();
        tag = in.readString();
        date = in.readString();
        address = in.readString();
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
        description = in.readString();
        leadImageUrl = in.readString();
        id = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLeadImageUrl() {
        return leadImageUrl;
    }

    public void setLeadImageUrl(String leadImageUrl) {
        this.leadImageUrl = leadImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event(String city, String code, String name, String tag, String date, String address, Float lat, Float lng, String description, String leadImageUrl, String id) {
        this.city = city;
        this.code = code;
        this.name = name;
        this.tag = tag;
        this.date = date;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
        this.leadImageUrl = leadImageUrl;
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(tag);
        dest.writeString(date);
        dest.writeString(address);
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
        dest.writeString(description);
        dest.writeString(leadImageUrl);
        dest.writeString(id);
    }
}
