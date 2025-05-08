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

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<MovieModel> movies;

    public MovieAdapter(List<MovieModel> movies) {
        this.movies = movies;
    }

    @NonNull
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getReleaseYear());
        double formRating = movie.getRating();
        String formattedRating = String.format("⭐ %.1f", formRating);
        holder.rating.setText(formattedRating);

        Glide.with(holder.itemView).
                load(movie.getPosterUrl())
                .placeholder(R.drawable.no_poster)
                .error(R.drawable.no_poster)
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
            intent.putExtra("movie_id", movie.getId());  // pass movie ID
            v.getContext().startActivity(intent);
        });

    }

    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, year, rating;

        MovieViewHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.imagePoster);
            title = view.findViewById(R.id.textTitle);
            year = view.findViewById(R.id.textYear);
            rating = view.findViewById(R.id.textRating);
        }
    }
}
