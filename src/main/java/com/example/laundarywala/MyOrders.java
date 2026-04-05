package com.example.laundarywala;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyOrders extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private List<OrderModel> orderList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String serviceType = document.getString("serviceType");
                        String pickupDate = document.getString("pickupDate");
                        String pickupTime = document.getString("pickupTime");
                        String address = document.getString("address");
                        List<String> photoURLs = (List<String>) document.get("photoURLs");
                        String userId = document.getString("userId");

                        orderList.add(new OrderModel(serviceType, pickupDate, pickupTime, address, photoURLs, userId));
                    }
                    adapter = new OrdersAdapter(orderList);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load orders: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
