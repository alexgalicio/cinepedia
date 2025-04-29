package com.example.movieapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieModel {
    @SerializedName("title")
    private String title;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("vote_average")
    private float rating;
    @SerializedName("genre_ids")
    private List<Integer> genreIds;
    @SerializedName("runtime")
    private int runtime;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("id")
    private int id;

    public int getRuntime() {
        return runtime;
    }

    public String getBackdropUrl() {
        return "https://image.tmdb.org/t/p/w1280" + backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseYear() {
        return releaseDate != null ? releaseDate.split("-")[0] : "N/A";
    }

    public String getPosterUrl() {
        return "https://image.tmdb.org/t/p/w780" + posterPath;
    }

    public float getRating() {
        return rating;
    }

    public String getGenreNames() {
        if (genreIds == null || genreIds.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (int id : genreIds) {
            String name = GenreUtils.getGenreNameById(id);
            if (name != null) builder.append(name).append(", ");
        }
        if (builder.length() > 2) builder.setLength(builder.length() - 2);
        return builder.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}