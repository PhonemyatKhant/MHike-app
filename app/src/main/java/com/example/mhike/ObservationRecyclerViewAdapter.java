package com.example.mhike;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ObservationRecyclerViewAdapter extends RecyclerView.Adapter<ObservationRecyclerViewAdapter.MyViewHolder> {

    private final ObservationRecyclerViewInterface observationRecyclerViewInterface;
    Context context;
    ArrayList<ObservationDataModel> observationDataModelArrayList;

    public ObservationRecyclerViewAdapter(Context context, ArrayList<ObservationDataModel> observationDataModelArrayList, ObservationRecyclerViewInterface observationRecyclerViewInterface) {
        this.context = context;
        this.observationDataModelArrayList = observationDataModelArrayList;
        this.observationRecyclerViewInterface = observationRecyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.observation_item, parent, false);
        return new MyViewHolder(view, observationRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObservationDataModel observationDataModel = observationDataModelArrayList.get(position);
        holder.observationName.setText(observationDataModel.getObservationText());
        holder.observationTime.setText(observationDataModel.getObservationTime());
        holder.additionalCmt.setText(observationDataModel.getAdditionalComment());
    }

    @Override
    public int getItemCount() {
        return observationDataModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView observationName, observationTime, additionalCmt;
        ImageButton btn_editObservation, btn_deleteObservation;

        public MyViewHolder(@NonNull View itemView, ObservationRecyclerViewInterface observationRecyclerViewInterface) {
            super(itemView);

            observationName = itemView.findViewById(R.id.tv_observation);
            observationTime = itemView.findViewById(R.id.tv_observationTime);
            additionalCmt = itemView.findViewById(R.id.tv_additionalCmt);
            btn_editObservation = itemView.findViewById(R.id.btn_editObservation);
            btn_deleteObservation = itemView.findViewById(R.id.btn_deleteObservation);

            btn_editObservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (observationRecyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            observationRecyclerViewInterface.onEditClick(pos);
                        }
                    }
                }
            });

            btn_deleteObservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (observationRecyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            observationRecyclerViewInterface.onDeleteClick(pos);
                            Log.d("MyApp", "Delete");

                        }
                    }
                }
            });
        }
    }

    public void updateData(ArrayList<ObservationDataModel> newData) {
        observationDataModelArrayList.clear();
        observationDataModelArrayList.addAll(newData);
        notifyDataSetChanged();
    }
}
