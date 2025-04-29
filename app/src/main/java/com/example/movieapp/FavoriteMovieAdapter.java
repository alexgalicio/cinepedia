package com.example.movieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FavoriteMovieAdapter extends RecyclerView.Adapter<FavoriteMovieAdapter.FavoriteViewHolder> {

    private List<MovieDetailModel> movies;
    private OnFavoriteRemovedListener listener;
    private OnMovieClickListener movieClickListener;

    // Interface for the remove button click callback
    public interface OnFavoriteRemovedListener {
        void onFavoriteRemoved(int movieId);
    }

    public interface OnMovieClickListener {
        void onMovieClick(MovieDetailModel movie);
    }

    public FavoriteMovieAdapter(List<MovieDetailModel> movies, OnFavoriteRemovedListener listener,  OnMovieClickListener movieClickListener) {
        this.movies = movies;
        this.listener = listener;
        this.movieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_movie, parent, false);
        return new FavoriteViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MovieDetailModel movie = movies.get(position);

        // Load poster image
        Glide.with(holder.itemView).
                load(movie.getPosterUrl())
                .into(holder.ivPoster);

        // Set title
        holder.tvTitle.setText(movie.getTitle());

        // Set year (extract from release date)
        String year = "";
        if (movie.getReleaseYear() != null && movie.getReleaseYear().length() >= 4) {
            year = movie.getReleaseYear().substring(0, 4);
        }
        holder.tvYear.setText(year);

        // Set rating
        holder.tvRating.setText(String.format("%.1f/10", movie.getRating()));

        // Set duration (format the runtime directly from MovieDetailModel)
        int runtime = movie.getRuntime();
        holder.tvDuration.setText(formatRuntime(runtime));

        // Setup remove button
        holder.btnRemove.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onFavoriteRemoved(movie.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                movieClickListener.onMovieClick(movie);
            }
        });

    }

    private String formatRuntime(int runtime) {
        if (runtime <= 0) {
            return "N/A";
        }
        int hours = runtime / 60;
        int minutes = runtime % 60;
        return hours + "h " + String.format("%02d", minutes) + "m";
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvYear;
        TextView tvRating;
        TextView tvDuration;
        ImageButton btnRemove;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivFavoritePoster);
            tvTitle = itemView.findViewById(R.id.tvFavoriteTitle);
            tvYear = itemView.findViewById(R.id.tvFavoriteYear);
            tvRating = itemView.findViewById(R.id.tvFavoriteRating);
            tvDuration = itemView.findViewById(R.id.tvFavoriteDuration);
            btnRemove = itemView.findViewById(R.id.btnRemoveFavorite);
        }
    }
}