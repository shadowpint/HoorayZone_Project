package com.horrayzone.horrayzone.model;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    private static final String TAG = "CartItem";

    @SerializedName("id")
    private String serverId;

    @SerializedName("productId")
    private String productServerId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("date")
    private String addedOnDate;



    @SerializedName("leadImageUrl")
    private String leadImageUrl;

    public String getAddedOnDate() {
        return addedOnDate;
    }

    public long getServerId() {
        return Long.parseLong(serverId);
    }

    public long getProductServerId() {
        return Long.parseLong(productServerId);
    }

    public int getQuantity() {
        return quantity;
    }

//    public String getRfc3339Date() {
//        return DateUtils.getRfc3339DateFromMySqlObject(addedOnDate);
//    }

//    public long getColorServerId() {
//        return Long.parseLong(colorServerId);
//    }
//
//    public String getColor() {
//        return color;
//    }
//
//    public long getSizeServerId() {
//        return Long.parseLong(sizeServerId);
//    }
//
//    public String getSize() {
//        return size;
//    }

    public String getLeadImageUrl() {
        return leadImageUrl;
    }
}
