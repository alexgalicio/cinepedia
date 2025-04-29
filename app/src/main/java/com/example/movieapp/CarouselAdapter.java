package com.example.movieapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    private List<MovieModel> movies;
    private static final String API_KEY = "79422230266c641ea333c4e89efbdd58";

    public CarouselAdapter(List<MovieModel> movies) {
        this.movies = movies;
    }

    @NonNull
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(view);
    }

    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        MovieModel movie = movies.get(position);

        Glide.with(holder.itemView)
                .load(movie.getBackdropUrl())
                .into(holder.poster);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getReleaseYear());
        holder.genre.setText(movie.getGenreNames());

        TMDbApi api = ApiClient.getClient().create(TMDbApi.class);
        api.getMovieDetails(movie.getId(), API_KEY).enqueue(new Callback<MovieDetailModel>() {
            @Override
            public void onResponse(Call<MovieDetailModel> call, Response<MovieDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int runtime = response.body().getRuntime();
                    holder.duration.setText(formatRuntime(runtime));
                }
            }

            @Override
            public void onFailure(Call<MovieDetailModel> call, Throwable t) {
                holder.duration.setText("N/A");
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
            intent.putExtra("movie_id", movie.getId());  // pass movie ID
            v.getContext().startActivity(intent);
        });


    }

    public int getItemCount() {
        return movies.size();
    }

    private String formatRuntime(int runtime) {
        int hours = runtime / 60;
        int minutes = runtime % 60;
        return hours + "h " + minutes + "m";
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, year, duration, genre;

        CarouselViewHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.imagePoster);
            title = view.findViewById(R.id.textTitle);
            year = view.findViewById(R.id.textYear);
            genre = view.findViewById(R.id.textGenre);
            duration = view.findViewById(R.id.textDuration);
        }
    }
}
