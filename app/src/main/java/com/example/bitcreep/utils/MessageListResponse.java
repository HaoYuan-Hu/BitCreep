package com.example.bitcreep.utils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageListResponse {
    @SerializedName("feeds")
    public List<Message> feeds;
    @SerializedName("success")
    public boolean success;
}
