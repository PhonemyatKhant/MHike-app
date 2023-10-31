package com.example.mhike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    // Variable to hold the interface
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<HikeDataModel> hikeDataModelArrayList;

    public RecyclerViewAdapter(Context context, ArrayList<HikeDataModel> hikeDataModelArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.hikeDataModelArrayList = hikeDataModelArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflating the layout for each item view
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
        // Binding data to the item view
        HikeDataModel hikeDataModel = hikeDataModelArrayList.get(position);
        holder.h_image.setImageResource(R.drawable.image);
        holder.h_name.setText(hikeDataModel.getHikeName());
        holder.h_date.setText(hikeDataModel.getHikeDate());
        holder.h_location.setText(hikeDataModel.getLocation());
    }

    @Override
    public int getItemCount() {
        // Returning the number of items in the list
        return hikeDataModelArrayList.size();
    }

    // Method to update the dataset when it changes
    public void updateData(ArrayList<HikeDataModel> newData) {
        hikeDataModelArrayList.clear();
        hikeDataModelArrayList.addAll(newData);
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView h_image;
        TextView h_name, h_date, h_location;
        ImageButton btn_edit, btn_delete;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            h_image = itemView.findViewById(R.id.h_image);
            h_name = itemView.findViewById(R.id.tv_observation);
            h_date = itemView.findViewById(R.id.tv_observationTime);
            h_location = itemView.findViewById(R.id.tv_additionalCmt);

            btn_edit = itemView.findViewById(R.id.btn_editObservation);
            btn_delete = itemView.findViewById(R.id.btn_deleteObservation);

            // Set an onClickListener for the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            // Call the onItemClick method when an item is clicked
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

            // Set an onClickListener for the delete button
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            // Call the onDeleteClick method when the delete button is clicked
                            recyclerViewInterface.onDeleteClick(pos);
                        }
                    }
                }
            });

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onEditClick(pos);
                        }
                    }
                }
            });

        }
    }
}
