package com.example.mhike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements RecyclerViewInterface {
    private ImageButton btn_add, btn_viewAll;
    private RecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;

    ArrayList<HikeDataModel> hikeDataModelArrayList;
    ArrayList<HikeDataModel> filteredHikes;

    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the UI components and database helper
        recyclerView = findViewById(R.id.recyclerView);
        btn_add = findViewById(R.id.btn_add);
        btn_viewAll = findViewById(R.id.btn_viewAll);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve a list of hikes from the database
        hikeDataModelArrayList = databaseHelper.getHikes();

        // Set up the RecyclerView to display the list of hikes
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        // Create and set the adapter for the RecyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(this, hikeDataModelArrayList, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Handle button clicks
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the InputHikeActivity when the "Add" button is clicked
                Intent intent = new Intent(HomeActivity.this, InputHikeActivity.class);
                startActivity(intent);
            }
        });
        btn_viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ViewAllHikes activity when the "View All" button is clicked
                Intent intent = new Intent(HomeActivity.this, ViewAllHikes.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            // Fetch the updated data from the database
            ArrayList<HikeDataModel> updatedData = databaseHelper.getHikes();

            // Update the dataset in the adapter
            adapter.updateData(updatedData);
        }
    }

    @Override
    public void onItemClick(int position) {
        // Retrieve the selected hike based on the position
        HikeDataModel hikeData = hikeDataModelArrayList.get(position);

        // Create an intent to navigate to a new activity to view hike details
        Intent intent = new Intent(HomeActivity.this, HikeDetailsActivity.class);

        // Pass data to the new activity
        intent.putExtra("hikeId", hikeData.getId());
        intent.putExtra("hikeName", hikeData.getHikeName());
        intent.putExtra("location", hikeData.getLocation());
        intent.putExtra("hikeLength", hikeData.getHikeLength());
        intent.putExtra("hikeDate", hikeData.getHikeDate());
        intent.putExtra("parkingAvailable", hikeData.isParkingAvailable());
        intent.putExtra("equipment", hikeData.getEquipment());
        intent.putExtra("difficulty", hikeData.getDifficulty());
        intent.putExtra("description", hikeData.getDescription());
        intent.putExtra("rating", hikeData.getRating());

        // Start the new activity
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


    public void onEditClick(int position) {
        // Retrieve the selected hike based on the position
        HikeDataModel hikeData = hikeDataModelArrayList.get(position);

        // Create an intent to navigate to a new activity to view hike details
        Intent intent = new Intent(HomeActivity.this, InputHikeActivity.class);

        // Pass data to the new activity
        intent.putExtra("hikeId", hikeData.getId());
        intent.putExtra("hikeName", hikeData.getHikeName());
        intent.putExtra("location", hikeData.getLocation());
        intent.putExtra("hikeLength", hikeData.getHikeLength());
        intent.putExtra("hikeDate", hikeData.getHikeDate());
        intent.putExtra("parkingAvailable", hikeData.isParkingAvailable());
        intent.putExtra("equipment", hikeData.getEquipment());
        intent.putExtra("difficulty", hikeData.getDifficulty());
        intent.putExtra("description", hikeData.getDescription());
        intent.putExtra("rating", hikeData.getRating());

        // Start the new activity
        startActivity(intent);
    }



}
