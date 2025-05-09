package com.example.movieapp;

import com.google.gson.annotations.SerializedName;

public class ExternalIdsResponse {
    @SerializedName("imdb_id")
    private String imdbId;

    @SerializedName("facebook_id")
    private String facebookId;

    @SerializedName("twitter_id")
    private String twitterId;

    @SerializedName("instagram_id")
    private String instagramId;

    // Add getters for all fields

    public String getImdbId() {
        return imdbId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getInstagramId() {
        return instagramId;
    }
}