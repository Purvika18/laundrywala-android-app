package com.example.laundarywala;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<OrderModel> orderList;

    public OrdersAdapter(List<OrderModel> orderList) {
        this.orderList = orderList;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textServiceType, textPickupTime, textAddress;
        ImageView imageOrderPhoto;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textServiceType = itemView.findViewById(R.id.textServiceType);
            textPickupTime = itemView.findViewById(R.id.textPickupTime);
            textAddress = itemView.findViewById(R.id.textAddress);
            imageOrderPhoto = itemView.findViewById(R.id.imageOrderPhoto);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.textServiceType.setText(order.getServiceType());
        holder.textPickupTime.setText(order.getPickupTime());
        holder.textAddress.setText(order.getAddress());

        List<String> photos = order.getPhotoURLs();
        if (photos != null && !photos.isEmpty()) {
            // Load first photo as thumbnail
            Glide.with(holder.imageOrderPhoto.getContext())
                    .load(photos.get(0))
                    .placeholder(R.drawable.placeholder_image)  // Add placeholder in drawable
                    .into(holder.imageOrderPhoto);
            holder.imageOrderPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.imageOrderPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
