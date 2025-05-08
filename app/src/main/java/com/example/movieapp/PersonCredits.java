package com.example.movieapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonCredits {

    @SerializedName("cast")
    private List<Cast> cast;

    @SerializedName("crew")
    private List<Crew> crew;

    public List<Cast> getCast() {
        return cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public static class Cast {

        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("character")
        private String character;

        @SerializedName("poster_path")
        private String posterPath;

        @SerializedName("release_date")
        private String releaseDate;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getCharacter() {
            return character;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getPosterUrl() {
            if (posterPath != null && !posterPath.isEmpty()) {
                return "https://image.tmdb.org/t/p/w185" + posterPath;
            }
            return null;
        }

        public String getYear() {
            if (releaseDate != null && releaseDate.length() >= 4) {
                return releaseDate.substring(0, 4);
            }
            return "";
        }
    }

    public static class Crew {

        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("job")
        private String job;

        @SerializedName("poster_path")
        private String posterPath;

        @SerializedName("release_date")
        private String releaseDate;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getJob() {
            return job;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public String getReleaseDate() {
            return releaseDate;
        }
    }
}
