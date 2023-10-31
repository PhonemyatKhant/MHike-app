package com.example.mhike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class ViewAllHikes extends AppCompatActivity implements RecyclerViewInterface {

    private RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;

    ArrayList<HikeDataModel> hikeDataModelArrayList;

    RecyclerViewAdapter recyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_hikes);


        recyclerView = findViewById(R.id.rv_allHikes);

        databaseHelper = new DatabaseHelper(this);

        hikeDataModelArrayList = databaseHelper.getHikes();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.hasFixedSize();

        recyclerViewAdapter = new RecyclerViewAdapter(this, hikeDataModelArrayList, this);

        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public void onItemClick(int position) {
        hikeDataModelArrayList = databaseHelper.getHikes();
        HikeDataModel hikeData = hikeDataModelArrayList.get(position); // Get the clicked item

        Intent intent = new Intent(ViewAllHikes.this, HikeDetailsActivity.class);
        intent.putExtra("hikeName", hikeData.getHikeName());
        intent.putExtra("location", hikeData.getLocation());
        startActivity(intent);

    }

    @Override
    public void onDeleteClick(int position) {
        HikeDataModel hikeData = hikeDataModelArrayList.get(position); // Get the clicked item
        int hikeId = hikeData.getId(); // Get the ID of the clicked hike

        // Call the deleteHike method from the DatabaseHelper to delete the hike
        long result = databaseHelper.deleteHike(hikeId);

        if (result != -1) {
            // Hike deleted successfully, now refresh the RecyclerView
            hikeDataModelArrayList.remove(position);
            recyclerViewAdapter.notifyItemRemoved(position);
            recyclerViewAdapter.notifyItemRangeChanged(position, hikeDataModelArrayList.size());
        } else {
            // Handle deletion error
            // You can show a Toast message or perform any other error handling here.
        }
    }

    @Override
    public void onEditClick(int position) {

    }


}