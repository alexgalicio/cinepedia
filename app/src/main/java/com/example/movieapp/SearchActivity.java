package com.example.movieapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        searchView = findViewById(R.id.searchView);
        recyclerGenres = findViewById(R.id.recyclerGenres);

        recyclerGenres.setLayoutManager(new GridLayoutManager(this, 2));

        loadGenres();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchActivity.this, MovieListActivity.class);
                intent.putExtra("search_query", query);
                intent.putExtra("title", "Results for \"" + query + "\"");
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_search);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, Dashboard.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadGenres() {
        TMDbApi api = ApiClient.getClient().create(TMDbApi.class);
        Call<GenreResponse> call = api.getGenres("79422230266c641ea333c4e89efbdd58");

        call.enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = response.body().getGenres();
                    GenreAdapter adapter = new GenreAdapter(genres, genre -> {
                        Intent intent = new Intent(SearchActivity.this, MovieListActivity.class);
                        intent.putExtra("genre_id", genre.getId());
                        intent.putExtra("title", genre.getName());
                        startActivity(intent);
                    });
                    recyclerGenres.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Failed to load genres", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
