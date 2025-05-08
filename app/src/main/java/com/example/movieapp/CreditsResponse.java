package com.example.movieapp;

import android.os.Parcel;
import android.os.Parcelable;

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

    public static class Cast implements Parcelable {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("character")
        private String character;

        @SerializedName("profile_path")
        private String profilePath;

        public int getId() {
            return id;
        }

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

        // Parcelable implementation
        protected Cast(Parcel in) {
            id = in.readInt();
            name = in.readString();
            character = in.readString();
            profilePath = in.readString();
        }

        public static final Creator<Cast> CREATOR = new Creator<Cast>() {
            @Override
            public Cast createFromParcel(Parcel in) {
                return new Cast(in);
            }

            @Override
            public Cast[] newArray(int size) {
                return new Cast[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeString(character);
            dest.writeString(profilePath);
        }
    }

    public static class Crew implements Parcelable {
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

        // Parcelable implementation
        protected Crew(Parcel in) {
            name = in.readString();
            job = in.readString();
        }

        public static final Creator<Crew> CREATOR = new Creator<Crew>() {
            @Override
            public Crew createFromParcel(Parcel in) {
                return new Crew(in);
            }

            @Override
            public Crew[] newArray(int size) {
                return new Crew[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(job);
        }
    }
}