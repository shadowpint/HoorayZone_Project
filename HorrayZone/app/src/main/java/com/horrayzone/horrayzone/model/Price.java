package com.horrayzone.horrayzone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Neeraj on 19/03/17.
 */
public class Price implements Parcelable {
    public static final Creator<Price> CREATOR = new Creator<Price>() {
        @Override
        public Price createFromParcel(Parcel in) {
            return new Price(in);
        }

        @Override
        public Price[] newArray(int size) {
            return new Price[size];
        }
    };
    @SerializedName("event_id")
    private String event;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("id")
    private String id;

    public Price() {
    }

    @SerializedName("price")
    private String price;

    protected Price(Parcel in) {
        event = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        id = in.readString();
    }

    public Price(String event, String name, String description, String price, String id) {
        this.event = event;
        this.name = name;
        this.description = description;
        this.price = price;
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(id);
    }
}
