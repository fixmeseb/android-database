package com.example.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DatabaseItemAdapter extends RecyclerView.Adapter<DatabaseItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DatabaseItem> items;

    public DatabaseItemAdapter(Context context, ArrayList<DatabaseItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cImage;
        TextView cName;
        TextView cComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cImage = itemView.findViewById(R.id.thumbnail1);
            cName = itemView.findViewById(R.id.tags1);
            cComment = itemView.findViewById(R.id.date1);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item, parent, false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseItem currentItem = items.get(position);

        Bitmap photo = currentItem.photo;
        holder.cImage.setImageBitmap(photo);

        holder.cName.setText(currentItem.name);
        holder.cComment.setText(currentItem.tags);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
