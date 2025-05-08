package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText etFirstName, etLastName, etEmail, etPassword;
    TextView tvLogin;
    Button btnSignUp;
    FirebaseAuth mAuth;

    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        initialize();
    }

    private void initialize() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvLogin = findViewById(R.id.tvLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
        loadingOverlay = findViewById(R.id.loadingOverlay);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName, lastName, email, password;
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(firstName)) {
                    etFirstName.setError("First name is required");
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    etLastName.setError("Last name is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    showLoading();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = user.getUid();

                                    user.sendEmailVerification() // Send verification email
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> emailTask) {
                                                    if (emailTask.isSuccessful()) {
                                                        // Save user data to Firebase Realtime Database
                                                        User userInfo = new User(firstName, lastName, email);
                                                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");

                                                        dbRef.child(uid).setValue(userInfo)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> dbTask) {
                                                                        if (dbTask.isSuccessful()) {
                                                                            Toast.makeText(Register.this, "Verification email sent. Please verify before logging in.", Toast.LENGTH_LONG).show();

                                                                            new Handler().postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mAuth.signOut(); // Sign out to prevent auto-login
                                                                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                                                                    finish();
                                                                                }
                                                                            }, 1500);
                                                                        } else {
                                                                            Toast.makeText(Register.this, "Failed to save user data: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(Register.this, "Failed to send verification email: " + emailTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

}
