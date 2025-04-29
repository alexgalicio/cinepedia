package com.example.movieapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditsResponse {
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
        @SerializedName("name")
        private String name;

        @SerializedName("character")
        private String character;

        @SerializedName("profile_path")
        private String profilePath;

        public String getName() {
            return name;
        }

        public String getCharacter() {
            return character;
        }

        public String getProfilePath() {
            return profilePath;
        }

        public String getProfileUrl() {
            return "https://image.tmdb.org/t/p/w342" + profilePath;
        }
    }

    public static class Crew {
        @SerializedName("name")
        private String name;

        @SerializedName("job")
        private String job;

        public String getName() {
            return name;
        }

        public String getJob() {
            return job;
        }
    }
}
