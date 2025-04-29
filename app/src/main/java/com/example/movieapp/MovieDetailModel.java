package com.example.movieapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailModel {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("vote_average")
    private float voteAverage;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("spoken_languages")
    private List<Language> spokenLanguages;

    @SerializedName("production_countries")
    private List<Country> productionCountries;

    @SerializedName("production_companies")
    private List<Company> productionCompanies;

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getReleaseYear() {
        return releaseDate != null && releaseDate.length() >= 4 ? releaseDate.substring(0, 4) : "";
    }

    public float getRating() {
        return voteAverage;
    }

    public String getPosterUrl() {
        return "https://image.tmdb.org/t/p/w780" + posterPath;
    }

    public String getBackdropUrl() {
        return "https://image.tmdb.org/t/p/w1280" + backdropPath;
    }

    public List<String> getGenresList() {
        List<String> genreNames = new ArrayList<>();
        for (Genre genre : genres) {
            genreNames.add(genre.getName());
        }
        return genreNames;
    }


    public static class Genre {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class Language {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class Country {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class Company {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public List<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    public List<Country> getProductionCountries() {
        return productionCountries;
    }

    public List<Company> getProductionCompanies() {
        return productionCompanies;
    }

    public MovieModel toMovieModel() {
        MovieModel movie = new MovieModel();
        movie.setId(this.getId());
        movie.setTitle(this.getTitle());
        movie.setPosterPath(this.getPosterUrl());
        movie.setReleaseDate(this.getReleaseYear());
        movie.setRating(this.getRating());
        return movie;
    }

}
