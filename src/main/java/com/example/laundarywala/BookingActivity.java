package com.example.laundarywala;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private Spinner spinnerService;
    private EditText editPickupDate, editPickupTime, editAddress;
    private RecyclerView recyclerPhotos;
    private PhotoAdapter photoAdapter;
    private List<Uri> photoUriList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Views
        spinnerService = findViewById(R.id.spinner_service_type);
        editPickupDate = findViewById(R.id.edittext_pickup_date);
        editPickupTime = findViewById(R.id.edittext_pickup_time);
        editAddress = findViewById(R.id.edittext_address);
        recyclerPhotos = findViewById(R.id.img_laundry_photo);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Spinner example items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Wash & Fold", "Dry Cleaning", "Ironing"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService.setAdapter(adapter);

        // RecyclerView setup
        photoAdapter = new PhotoAdapter(photoUriList);
        recyclerPhotos.setAdapter(photoAdapter);
        recyclerPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // ActivityResultLaunchers
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri photoUri = result.getData().getData();
                        if (photoUri != null) {
                            photoUriList.add(photoUri);
                            photoAdapter.notifyDataSetChanged();
                        }
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            photoUriList.add(selectedImage);
                            photoAdapter.notifyDataSetChanged();
                        }
                    }
                });

        // Buttons
        Button btnTakePhoto = findViewById(R.id.btn_take_photo);
        Button btnPickGallery = findViewById(R.id.btn_pick_gallery);
        Button btnConfirm = findViewById(R.id.btn_confirm_booking);
        Button btnGetLocation = findViewById(R.id.btn_get_location);

        btnTakePhoto.setOnClickListener(v -> checkPermissionAndOpenCamera());
        btnPickGallery.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnConfirm.setOnClickListener(v -> submitBooking(
                spinnerService.getSelectedItem().toString(),
                editPickupDate.getText().toString(),
                editPickupTime.getText().toString(),
                editAddress.getText().toString()
        ));
        btnGetLocation.setOnClickListener(v -> getLocation());

        // Date & Time pickers
        editPickupDate.setOnClickListener(v -> showDatePicker());
        editPickupTime.setOnClickListener(v -> showTimePicker());
    }

    // Date Picker
    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> editPickupDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    // Time Picker
    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> editPickupTime.setText(hourOfDay + ":" + String.format("%02d", minute)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        dialog.show();
    }

    // Location
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2001);
        } else {
            fusedLocationClient.getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    null
            ).addOnSuccessListener(location -> {
                if (location != null) {
                    editAddress.setText(location.getLatitude() + ", " + location.getLongitude());
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1001);
        }
    }

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1002);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else if (requestCode == 1002 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (requestCode == 2001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitBooking(String serviceType, String pickupDate, String pickupTime, String address) {
        String orderId = db.collection("orders").document().getId();
        uploadPhotosAndSaveBooking(orderId, serviceType, pickupDate, pickupTime, address);
    }

    private void uploadPhotosAndSaveBooking(String orderId, String serviceType, String pickupDate,
                                            String pickupTime, String address) {
        List<String> downloadUrls = new ArrayList<>();
        if (photoUriList.isEmpty()) {
            saveBookingToFirestore(orderId, serviceType, pickupDate, pickupTime, address, downloadUrls);
            return;
        }

        final int totalPhotos = photoUriList.size();
        final int[] photosUploaded = {0};

        for (Uri photoUri : photoUriList) {
            StorageReference photoRef = storage.getReference()
                    .child("order_photos/" + orderId + "/" + photoUri.getLastPathSegment());

            photoRef.putFile(photoUri).addOnSuccessListener(taskSnapshot ->
                    photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        downloadUrls.add(uri.toString());
                        photosUploaded[0]++;
                        if (photosUploaded[0] == totalPhotos) {
                            saveBookingToFirestore(orderId, serviceType, pickupDate, pickupTime, address, downloadUrls);
                        }
                    }).addOnFailureListener(e ->
                            Toast.makeText(BookingActivity.this, "Failed to get URL: " + e.getMessage(), Toast.LENGTH_SHORT).show())
            ).addOnFailureListener(e ->
                    Toast.makeText(BookingActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveBookingToFirestore(String orderId, String serviceType, String pickupDate,
                                        String pickupTime, String address, List<String> photoURLs) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("serviceType", serviceType);
        booking.put("pickupDate", pickupDate);
        booking.put("pickupTime", pickupTime);
        booking.put("address", address);
        booking.put("photoURLs", photoURLs);
        booking.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection("orders").document(orderId)
                .set(booking)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Booking created successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // RecyclerView Adapter
    public static class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
        private List<Uri> photos;

        public PhotoAdapter(List<Uri> photos) { this.photos = photos; }

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(200, 200);
            params.setMargins(8, 8, 8, 8);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new PhotoViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            Glide.with(holder.imageView.getContext())
                    .load(photos.get(position))
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() { return photos.size(); }

        static class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            public PhotoViewHolder(@NonNull ImageView itemView) {
                super(itemView);
                imageView = itemView;
            }
        }
    }
}
