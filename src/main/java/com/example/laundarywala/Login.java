package com.example.laundarywala;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView registerNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.Progressbar);
        registerNow = findViewById(R.id.RegisterNow);

        btnLogin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString().trim() : "";

            if (email.isEmpty()) {
                editTextEmail.setError("Email is required");
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to the next activity after login
                            startActivity(new Intent(getApplicationContext(), dashboard.class));

                            finish();
                        } else {
                            Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        registerNow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Registration.class));
            finish();
        });
    }
}
