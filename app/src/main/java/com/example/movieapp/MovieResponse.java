package com.example.movieapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<MovieModel> results;
    public List<MovieModel> getResults() { return results; }

    private int total_pages;
    public int getTotalPages() { return total_pages; }

}
