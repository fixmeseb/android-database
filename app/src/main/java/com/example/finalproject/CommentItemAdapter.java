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

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class CommentItemAdapter extends RecyclerView.Adapter<CommentItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommentItem> items;

    public CommentItemAdapter(Context context, ArrayList<CommentItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cImage;
        TextView cName;
        TextView cComment;
        TextView cDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cImage = itemView.findViewById(R.id.thumbnail1);
            cName = itemView.findViewById(R.id.tags1);
            cComment = itemView.findViewById(R.id.date1);
            cDate = itemView.findViewById(R.id.timeStamp);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.comment_item, parent, false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentItem currentItem = items.get(position);

        int photo = currentItem.photo;
        holder.cImage.setImageResource(photo);

        holder.cName.setText(currentItem.name);
        holder.cComment.setText(currentItem.tags);
        holder.cDate.setText(currentItem.timestamp);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
