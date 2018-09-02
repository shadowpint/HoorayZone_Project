package com.horrayzone.horrayzone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Neeraj on 19/03/17.
 */
public class Booking implements Parcelable {
    @SerializedName("user")
    private String userId;
    @SerializedName("price")
    private String priceId;
    @SerializedName("transaction_id")
    private String transactionId;
    @SerializedName("event")
    private String eventId;
    @SerializedName("active")
    private Boolean status;
    @SerializedName("id")
    private String id;

    protected Booking(Parcel in) {
        userId = in.readString();
        priceId = in.readString();
        transactionId = in.readString();
        eventId = in.readString();
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
        id = in.readString();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Booking() {
    }

    public Booking(String userId, String priceId, String transactionId, String eventId, Boolean status, String id) {
        this.userId = userId;
        this.priceId = priceId;
        this.transactionId = transactionId;
        this.eventId = eventId;
        this.status = status;
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(priceId);
        dest.writeString(transactionId);
        dest.writeString(eventId);
        dest.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
        dest.writeString(id);
    }
}
