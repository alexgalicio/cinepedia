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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilmographyAdapter extends RecyclerView.Adapter<FilmographyAdapter.FilmViewHolder> {

    private List<PersonCredits.Cast> filmList;

    public FilmographyAdapter(List<PersonCredits.Cast> filmList) {
        this.filmList = filmList;

        Collections.sort(filmList, new Comparator<PersonCredits.Cast>() {
            @Override
            public int compare(PersonCredits.Cast o1, PersonCredits.Cast o2) {
                String year1 = o1.getYear();
                String year2 = o2.getYear();

                // Handle null or empty years safely
                if (year1 == null || year1.isEmpty()) return 1;
                if (year2 == null || year2.isEmpty()) return -1;

                try {
                    int y1 = Integer.parseInt(year1);
                    int y2 = Integer.parseInt(year2);
                    return Integer.compare(y2, y1); // Descending order
                } catch (NumberFormatException e) {
                    return 0; // Treat as equal if year isn't a valid number
                }
            }
        });
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filmography, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        PersonCredits.Cast film = filmList.get(position);
        holder.bind(film);
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    class FilmViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPoster;
        private TextView txtTitle;
        private TextView txtCharacter;
        private TextView txtYear;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCharacter = itemView.findViewById(R.id.txtCharacter);
            txtYear = itemView.findViewById(R.id.txtYear);

            // Set click listener to open movie details
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    PersonCredits.Cast film = filmList.get(position);

                    // Launch MovieDetailActivity
                    Intent intent = new Intent(itemView.getContext(), MovieDetailActivity.class);
                    intent.putExtra("movie_id", film.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void bind(PersonCredits.Cast film) {
            txtTitle.setText(film.getTitle());

            String character = film.getCharacter();
            if (character != null && !character.isEmpty()) {
                txtCharacter.setText("as " + character);
                txtCharacter.setVisibility(View.VISIBLE);
            } else {
                txtCharacter.setVisibility(View.GONE);
            }

            String year = film.getYear();
            if (!year.isEmpty()) {
                txtYear.setText(year);
                txtYear.setVisibility(View.VISIBLE);
            } else {
                txtYear.setVisibility(View.GONE);
            }

            String posterUrl = film.getPosterUrl();
            if (posterUrl != null) {
                Glide.with(itemView.getContext())
                        .load(posterUrl)
                        .placeholder(R.drawable.no_poster)
                        .error(R.drawable.no_poster)
                        .transform(new RoundedCorners(20))
                        .into(imgPoster);
            } else {
                imgPoster.setImageResource(R.drawable.no_poster);
            }
        }
    }
}
