package com.example.movieapp;

public class FavoriteMovie {
    public int movieId;
    public String title;
    public String posterUrl;
    public String releaseYear;
    public double rating;

    public FavoriteMovie() {
        // Required for Firebase
    }

    public FavoriteMovie(int movieId, String title, String posterUrl, String releaseYear, double rating) {
        this.movieId = movieId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.releaseYear = releaseYear;
        this.rating = rating;
    }
}

