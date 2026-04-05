package com.example.laundarywala;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laundarywala.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.textfield.TextInputEditText;

public class Registration extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView loginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.Progressbar);
        loginNow = findViewById(R.id.loginNow);

        btnRegister.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString().trim() : "";

            if (email.isEmpty()) {
                editTextEmail.setError("Email is required");
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (password.isEmpty() || password.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        } else {
                            Toast.makeText(Registration.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        loginNow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }
}
