package com.example.movieapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbApi {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("region") String region
    );

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey
    );

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey
    );

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlaying(
            @Query("api_key") String apiKey,
            @Query("region") String region
    );

    @GET("movie/popular")
    Call<MovieResponse> getPopularMoviesPaged(
            @Query("api_key") String apiKey,
            @Query("region") String region,
            @Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlaying(
            @Query("api_key") String apiKey,
            @Query("region") String region,
            @Query("page") int page
    );

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("trending/movie/week")
    Call<MovieResponse> getTrendingThisWeek(
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}")
    Call<MovieDetailModel> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getMovieCredits(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/similar")
    Call<MovieResponse> getSimilarMovies(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("genre/movie/list")
    Call<GenreResponse> getGenres(
            @Query("api_key") String apiKey
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("api_key") String apiKey,
            @Query("with_genres") int genreId,
            @Query("page") int page
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    // Get person details
    @GET("person/{person_id}")
    Call<PersonDetail> getPersonDetails(
            @Path("person_id") int personId,
            @Query("api_key") String apiKey
    );

    // Get person movie credits
    @GET("person/{person_id}/movie_credits")
    Call<PersonCredits> getPersonCredits(
            @Path("person_id") int personId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/external_ids")
    Call<ExternalIdsResponse> getMovieExternalIds(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

}
