package com.example.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CheckItem> items;

    private int checkedPos = -1;

    public interface OnItemSelectedListener{
        void onItemSelected(CheckItem item);
    }

    private OnItemSelectedListener  listener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener){
        this.listener = listener;
    }

    public CheckListAdapter(Context context, ArrayList<CheckItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cImage;
        TextView cName;
        TextView cComment;
        CheckBox cCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cImage = itemView.findViewById(R.id.thumbnail1);
            cName = itemView.findViewById(R.id.tags1);
            cComment = itemView.findViewById(R.id.date1);
            cCheck = itemView.findViewById(R.id.itemCheckBox);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.check_list_item, parent, false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckItem currentItem = items.get(position);

        Bitmap photo = currentItem.photo;
        holder.cImage.setImageBitmap(photo);
        holder.cName.setText(currentItem.tags);
        holder.cComment.setText(currentItem.date);

        holder.cCheck.setOnCheckedChangeListener(null);

        holder.cCheck.setChecked(position == checkedPos);


        holder.cCheck.setOnCheckedChangeListener(((buttonView, isChecked) ->{
            if (isChecked){
                Log.d("ADAPTER", "Checkbox selected at pos=" + holder.getAdapterPosition());
                int previous = checkedPos;
                checkedPos = holder.getAbsoluteAdapterPosition();

                if (listener != null){
                    listener.onItemSelected(currentItem);
                }

                notifyItemChanged(previous);
                notifyItemChanged(checkedPos);
            }
        }
        ));

        holder.itemView.setOnClickListener(v -> {
            Log.d("ADAPTER", "Checkbox selected at pos=" + holder.getAdapterPosition());
            int previous = checkedPos;
            checkedPos = holder.getAbsoluteAdapterPosition();

            if (listener != null){
                listener.onItemSelected(currentItem);
            }

            notifyItemChanged(previous);
            notifyItemChanged(checkedPos);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
