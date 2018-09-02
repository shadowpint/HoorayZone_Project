package com.horrayzone.horrayzone.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Neeraj on 19/03/17.
 */
public class Country {
    @SerializedName("country_name")
    private String name;
    @SerializedName("country_flag")
    private String url;
    @SerializedName("country_graph1")
    private String graph1;
    @SerializedName("country_graph2")
    private String graph2;
    @SerializedName("id")
    private String id;

    public Country() {
    }

    public String getId() {
        return id;
    }

    public String getGraph1() {
        return graph1;
    }

    public void setGraph1(String graph1) {
        this.graph1 = graph1;
    }

    public String getGraph2() {
        return graph2;
    }

    public void setGraph2(String graph2) {
        this.graph2 = graph2;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Country(String name, String url, String id, String graph1, String graph2) {
        this.name = name;
        this.url= url;
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.id=id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
