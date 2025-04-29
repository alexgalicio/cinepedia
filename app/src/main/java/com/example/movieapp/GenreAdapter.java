package com.example.movieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private List<Genre> genres;
    private OnGenreClickListener listener;

    public interface OnGenreClickListener {
        void onGenreClick(Genre genre);
    }

    public GenreAdapter(List<Genre> genres, OnGenreClickListener listener) {
        this.genres = genres;
        this.listener = listener;
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.textGenre.setText(genre.getName());
        holder.itemView.setOnClickListener(v -> listener.onGenreClick(genre));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView textGenre;

        GenreViewHolder(View itemView) {
            super(itemView);
            textGenre = itemView.findViewById(R.id.textGenre);
        }
    }
}
