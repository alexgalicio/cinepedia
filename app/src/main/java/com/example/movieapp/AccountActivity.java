package com.example.movieapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail;
    private Button buttonSave, buttonLogout, buttonChangePassword;

    Exception exception;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        buttonSave = findViewById(R.id.buttonSave);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        loadUserInfo();

        buttonSave.setOnClickListener(v -> saveUserInfo());
        buttonChangePassword.setOnClickListener(v -> resetPassword());
        buttonLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_account);
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
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_account) {
                return true;
            }
            return false;
        });
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String email = auth.getCurrentUser().getEmail();

                    editFirstName.setText(firstName);
                    editLastName.setText(lastName);
                    editEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void saveUserInfo() {
        String newFirstName = editFirstName.getText().toString();
        String newLastName = editLastName.getText().toString();
        String newEmail = editEmail.getText().toString();

        // Update Firebase Auth email
        Objects.requireNonNull(auth.getCurrentUser()).updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update database
                userRef.child("firstName").setValue(newFirstName);
                userRef.child("lastName").setValue(newLastName);
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            } else {
                exception = task.getException();
                String errorMessage = (exception != null) ? exception.getMessage() : "Unknown error occurred";
                Toast.makeText(this, "Failed to update email: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetPassword() {
        String email = auth.getCurrentUser().getEmail();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                auth.signOut(); // <--- LOGOUT here

                // Optional: Redirect to login screen after logout
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close current activity
            } else {
                exception = task.getException();
                String errorMessage = (exception != null) ? exception.getMessage() : "Unknown error occurred";
                Toast.makeText(this, "Failed to send reset email: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

}
