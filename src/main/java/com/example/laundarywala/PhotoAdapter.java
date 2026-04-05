package com.example.laundarywala;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<Bitmap> photoList;


    public PhotoAdapter(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

   @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.imageView.setImageBitmap(photoList.get(position));
        holder.btnDelete.setOnClickListener(v -> {
            // Show confirm dialog before removing
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Photo")
                    .setMessage("Do you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        photoList.remove(pos);
                        notifyItemRemoved(pos);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,btnDelete;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}