package com.horrayzone.horrayzone.model;

import com.google.gson.annotations.SerializedName;
import com.horrayzone.horrayzone.Config;

public class BlogEntry {

    @SerializedName("id")
    private String serverId;

    @SerializedName("name")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("url")
    private String imageUrl;

    public BlogEntry() {

    }

    public BlogEntry(Long serverId, String title, String content, String imageUrl) {
        this.serverId = Long.toString(serverId);
        this.title= title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public Long getServerId() {
        return Long.parseLong(serverId);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return Config.buildBlogEntryImageUrl(imageUrl);
    }
}
