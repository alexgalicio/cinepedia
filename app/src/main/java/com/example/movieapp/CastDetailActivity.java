package com.example.movieapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class CastDetailActivity extends AppCompatActivity {

    private static final String API_KEY = "79422230266c641ea333c4e89efbdd58";

    private ImageView imgProfile;
    private TextView txtName, txtBirthplace, txtBiography;
    private RecyclerView recyclerFilmography;
    private ProgressBar progressBar;

    private TMDbApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_detail);

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Initialize views
        imgProfile = findViewById(R.id.imgProfile);
        txtName = findViewById(R.id.txtName);
        txtBirthplace = findViewById(R.id.txtBirthplace);
        txtBiography = findViewById(R.id.txtBiography);
        recyclerFilmography = findViewById(R.id.recyclerFilmography);
        progressBar = findViewById(R.id.progressBar);

        recyclerFilmography.setLayoutManager(new LinearLayoutManager(this));

        // Get cast ID from intent
        int castId = getIntent().getIntExtra("cast_id", -1);
        String castName = getIntent().getStringExtra("cast_name");
        String profilePath = getIntent().getStringExtra("profile_path");

        if (castId == -1) {
            Toast.makeText(this, "Error: Cast information not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set the name and profile image immediately from intent extras
        if (castName != null) {
            txtName.setText(castName);
        }

        if (profilePath != null && !profilePath.isEmpty()) {
            String imageUrl = "https://image.tmdb.org/t/p/w342" + profilePath;
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.no_profile)
                    .error(R.drawable.no_profile)
                    .transform(new RoundedCorners(20))
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.no_profile);
        }

        // Initialize API service - make sure you have RetrofitClient class
        apiService = ApiClient.getClient().create(TMDbApi.class);

        // Load cast details
        loadCastDetails(castId);

        // Load filmography
        loadFilmography(castId);

        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    private void loadCastDetails(int castId) {
        progressBar.setVisibility(View.VISIBLE);

        Call<PersonDetail> call = apiService.getPersonDetails(castId, API_KEY);
        call.enqueue(new Callback<PersonDetail>() {
            @Override
            public void onResponse(Call<PersonDetail> call, Response<PersonDetail> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    PersonDetail person = response.body();
                    displayPersonDetails(person);
                } else {
                    Toast.makeText(CastDetailActivity.this,
                            "Failed to load cast details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PersonDetail> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CastDetailActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFilmography(int castId) {
        Call<PersonCredits> call = apiService.getPersonCredits(castId, API_KEY);

        call.enqueue(new Callback<PersonCredits>() {
            @Override
            public void onResponse(Call<PersonCredits> call, Response<PersonCredits> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PersonCredits.Cast> castCredits = response.body().getCast();
                    if (castCredits != null && !castCredits.isEmpty()) {
                        FilmographyAdapter adapter = new FilmographyAdapter(castCredits);
                        recyclerFilmography.setAdapter(adapter);
                    } else {
                        // No filmography found
                        TextView emptyText = new TextView(CastDetailActivity.this);
                        emptyText.setText("No filmography available");
                        ((ViewGroup) recyclerFilmography.getParent()).addView(emptyText);
                    }
                } else {
                    Toast.makeText(CastDetailActivity.this,
                            "Failed to load filmography", Toast.LENGTH_SHORT).show();
                }

                Log.d("API_ERROR", "Response code: " + response.code());

            }

            @Override
            public void onFailure(Call<PersonCredits> call, Throwable t) {
                Toast.makeText(CastDetailActivity.this,
                        "Failed to load filmography: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });


    }

    private void displayPersonDetails(PersonDetail person) {
        // Name already set from intent, but update it if needed
        txtName.setText(person.getName());

        String birthplace = person.getPlaceOfBirth();
        if (birthplace != null && !birthplace.isEmpty()) {
            txtBirthplace.setText(birthplace);
        } else {
            txtBirthplace.setText("Unknown");
        }

        String biography = person.getBiography();
        if (biography != null && !biography.isEmpty()) {
            txtBiography.setText(biography);
        } else {
            txtBiography.setText("No biography available");
        }

        // Update profile image if available
        if (person.getProfilePath() != null) {
            String imageUrl = person.getProfileUrl();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.no_profile)
                    .error(R.drawable.no_profile)
                    .transform(new RoundedCorners(20))
                    .into(imgProfile);
        }
    }
}