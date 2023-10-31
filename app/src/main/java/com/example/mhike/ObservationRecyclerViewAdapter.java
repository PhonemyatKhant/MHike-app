package com.example.mhike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ObservationRecyclerViewAdapter extends RecyclerView.Adapter<ObservationRecyclerViewAdapter.MyViewHolder> {

   // private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<ObservationDataModel> observationDataModelArrayList;

    public ObservationRecyclerViewAdapter(Context context, ArrayList<ObservationDataModel> observationDataModelArrayList) {
        this.context = context;
        this.observationDataModelArrayList = observationDataModelArrayList;
    }

    @NonNull
    @Override
    public ObservationRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflating the layout for each item view
        View view = LayoutInflater.from(context).inflate(R.layout.observation_item, parent, false);
        return new ObservationRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationRecyclerViewAdapter.MyViewHolder holder, int position) {

        // Binding data to the item view
        ObservationDataModel observationDataModel = observationDataModelArrayList.get(position);
        holder.observationName.setText(observationDataModel.getObservationText());
        holder.observationTime.setText(observationDataModel.getObservationTime());
        holder.additionalCmt.setText(observationDataModel.getAdditionalComment());

    }

    @Override
    public int getItemCount() {
        return observationDataModelArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView observationName, observationTime, additionalCmt;
        ImageButton btn_edit, btn_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            observationName = itemView.findViewById(R.id.tv_observation);
            observationTime = itemView.findViewById(R.id.tv_observationTime);
            additionalCmt = itemView.findViewById(R.id.tv_additionalCmt);
        }
    }
    public void updateData(ArrayList<ObservationDataModel> newData) {
        observationDataModelArrayList.clear();
        observationDataModelArrayList.addAll(newData);
        notifyDataSetChanged();
    }
}

