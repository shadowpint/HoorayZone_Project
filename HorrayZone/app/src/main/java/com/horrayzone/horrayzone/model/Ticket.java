package com.horrayzone.horrayzone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Neeraj on 19/03/17.
 */
public class Ticket implements Parcelable {
    @SerializedName("price_name")
    private String priceName;
    @SerializedName("price_cost")
    private String priceCost;
    @SerializedName("transaction_id")
    private String transactionId;
    @SerializedName("event_name")
    private String eventName;
    @SerializedName("event_time")
    private String eventTime;
    @SerializedName("event_image")
    private String eventImage;
    @SerializedName("event_genre")
    private String eventGenre;
    @SerializedName("event_venue")
    private String eventVenue;
    @SerializedName("id")
    private String Id;

    protected Ticket(Parcel in) {
        priceName = in.readString();
        priceCost = in.readString();
        transactionId = in.readString();
        eventName = in.readString();
        eventTime = in.readString();
        eventImage = in.readString();
        eventGenre = in.readString();
        eventVenue = in.readString();
        Id = in.readString();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public String getPriceCost() {
        return priceCost;
    }

    public void setPriceCost(String priceCost) {
        this.priceCost = priceCost;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public Ticket(String priceName, String priceCost, String transactionId, String eventName, String eventTime, String eventImage, String eventGenre, String eventVenue, String id) {
        this.priceName = priceName;
        this.priceCost = priceCost;
        this.transactionId = transactionId;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventImage = eventImage;
        this.eventGenre = eventGenre;
        this.eventVenue = eventVenue;
        Id = id;
    }

    public String getEventGenre() {
        return eventGenre;
    }

    public void setEventGenre(String eventGenre) {
        this.eventGenre = eventGenre;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(priceName);
        dest.writeString(priceCost);
        dest.writeString(transactionId);
        dest.writeString(eventName);
        dest.writeString(eventTime);
        dest.writeString(eventImage);
        dest.writeString(eventGenre);
        dest.writeString(eventVenue);
        dest.writeString(Id);
    }
}
