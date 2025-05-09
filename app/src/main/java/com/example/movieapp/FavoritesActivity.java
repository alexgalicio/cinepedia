package com.example.movieapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity implements FavoriteMovieAdapter.OnFavoriteRemovedListener {

    private RecyclerView recyclerFavorites;
    private FavoriteMovieAdapter adapter;
    private List<MovieDetailModel> favoriteMovies = new ArrayList<>();
    private TextView tvNoFavorites;

    private DatabaseReference favoritesRef;
    private TMDbApi api;

    private ChildEventListener favoritesListener;

    private static final String API_KEY = "79422230266c641ea333c4e89efbdd58";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        recyclerFavorites = findViewById(R.id.recyclerFavorites);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavoriteMovieAdapter(favoriteMovies, this, movie -> {
            Intent intent = new Intent(FavoritesActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie_id", movie.getId());
            startActivity(intent);
        });
        recyclerFavorites.setAdapter(adapter);


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("favorites");

        api = ApiClient.getClient().create(TMDbApi.class);

        tvNoFavorites = findViewById(R.id.tvNoFavorites);

        loadFavoriteMovies();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_favorites);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, Dashboard.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_favorites) {
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadFavoriteMovies() {
        tvNoFavorites.setVisibility(View.VISIBLE);
        favoriteMovies.clear();
        adapter.notifyDataSetChanged();

        favoritesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String movieId = snapshot.getKey();
                fetchMovieDetails(Integer.parseInt(movieId));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String movieId = snapshot.getKey();
                int id = Integer.parseInt(movieId);

                for (int i = 0; i < favoriteMovies.size(); i++) {
                    if (favoriteMovies.get(i).getId() == id) {
                        favoriteMovies.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }

                if (favoriteMovies.isEmpty()) {
                    tvNoFavorites.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        favoritesRef.addChildEventListener(favoritesListener);
    }

    private void fetchMovieDetails(int movieId) {
        api.getMovieDetails(movieId, API_KEY).enqueue(new Callback<MovieDetailModel>() {
            @Override
            public void onResponse(Call<MovieDetailModel> call, Response<MovieDetailModel> response) {

                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailModel movie = response.body();
                    favoriteMovies.add(movie);
                    adapter.notifyItemInserted(favoriteMovies.size() - 1);
                    tvNoFavorites.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MovieDetailModel> call, Throwable t) {
            }
        });
    }


    @Override
    public void onFavoriteRemoved(int movieId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid).child("favorites").child(String.valueOf(movieId));

        movieRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            } else {
                Toast.makeText(this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (favoritesListener != null) {
            favoritesRef.removeEventListener(favoritesListener);
        }
    }

}