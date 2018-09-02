package com.horrayzone.horrayzone.model;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("id")
    private String serverId;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private String price;

    @SerializedName("leadImageUrl")
    private String leadImageUrl;

    @SerializedName("brandId")
    private String brandId;

    @SerializedName("subcategoryId")
    private String subcategoryId;



    public Product() {

    }

    public Product(Long serverId, String code, String name, String price, String leadImageUrl, String brandId, String subcategoryId) {
        this.serverId = Long.toString(serverId);
        this.code = code;
        this.name = name;
        this.price = price;
        this.leadImageUrl = leadImageUrl;
        this.brandId = brandId;
        this.subcategoryId= subcategoryId;
    }

    public Long getServerId() {
        return Long.parseLong(serverId);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }
    public String getLeadImageUrl() {
        return leadImageUrl;
    }
    public String getBrandId() {
        return brandId;

    }

    public String getSubcategoryId() {
        return subcategoryId;

    }







}