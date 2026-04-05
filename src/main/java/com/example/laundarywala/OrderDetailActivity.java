package com.example.laundarywala;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class OrderDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toast.makeText(this,"Details started", Toast.LENGTH_SHORT).show();

        TextView textServiceType = findViewById(R.id.textDetailServiceType);
        TextView textAddress = findViewById(R.id.textDetailAddress);
        TextView textPickup = findViewById(R.id.textDetailPickup);
        TextView textPhotoStatus = findViewById(R.id.textDetailPhotoStatus);
        ImageView imagePhoto = findViewById(R.id.imageDetailPhoto);





        String serviceType = getIntent().getStringExtra("serviceType");
        String pickupDate = getIntent().getStringExtra("pickupDate");
        String pickupTime = getIntent().getStringExtra("pickupTime");
        String address = getIntent().getStringExtra("address");
        boolean photoTaken = getIntent().getBooleanExtra("photoTaken", false);
        String photoUrl = getIntent().getStringExtra("photoUrl");

        if(photoTaken && photoUrl != null && !photoUrl.isEmpty()){
            imagePhoto.setVisibility(View.VISIBLE);
            // Use Glide or Picasso to load the image from the URL
            Glide.with(this).load(photoUrl).into(imagePhoto);
           // imagePhoto.setVisibility(View.VISIBLE);
           // Glide.with(this).load("https://picsum.photos/300").into(imagePhoto);
        }else{
            imagePhoto.setVisibility(View.GONE);
        }

        textServiceType.setText("Service: " + serviceType);
        textAddress.setText("Address: " + address);
        textPickup.setText("Pickup: " + pickupDate + " at " + pickupTime);
        textPhotoStatus.setText("Photos attached: " + (photoTaken ? "Yes" : "No"));

        Toast.makeText(this,"Details loaded", Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"photoUrl:" + photoUrl, Toast.LENGTH_LONG).show();
    }
}