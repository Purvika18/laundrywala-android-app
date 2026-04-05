package com.example.laundarywala;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class dashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button btnLogout, btnMyOrders, btnPlaceOrder;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Enable edge-to-edge after setContentView
        EdgeToEdge.enable(this);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Initialize views
        btnLogout = findViewById(R.id.btn_Logout);
        btnMyOrders = findViewById(R.id.btn_MyOrders);
        btnPlaceOrder = findViewById(R.id.btn_PlaceOrder);
        tvEmail = findViewById(R.id.EmailDisplay);

        // Login check
        if (mUser == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
            return;
        } else {
            tvEmail.setText(mUser.getEmail());
        }

        // Logout button
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        // My Orders button
        btnMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(dashboard.this, MyOrders.class));
        });

        // Place Order button (BookingActivity)
        btnPlaceOrder.setOnClickListener(v -> {
            startActivity(new Intent(dashboard.this, BookingActivity.class));
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            if (insets != null) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });
    }
}
