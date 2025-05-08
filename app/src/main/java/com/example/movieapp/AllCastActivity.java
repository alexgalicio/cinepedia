package com.example.movieapp;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllCastActivity extends AppCompatActivity {

    private RecyclerView recyclerAllCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_cast);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        recyclerAllCast = findViewById(R.id.recyclerAllCast);
        recyclerAllCast.setLayoutManager(new GridLayoutManager(this, 3));

        ArrayList<CreditsResponse.Cast> castList = getIntent().getParcelableArrayListExtra("cast_list");

        if (castList != null && !castList.isEmpty()) {
            CastAdapter adapter = new CastAdapter(castList);
            recyclerAllCast.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No cast information available", Toast.LENGTH_SHORT).show();
        }

        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }
}
