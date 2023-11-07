package com.example.mhike;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements RecyclerViewInterface {
    private ImageButton btn_add, btn_deleteAll;
    private EditText etSearch;

    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;

    ArrayList<HikeDataModel> hikeDataModelArrayList;
    ArrayList<HikeDataModel> filteredHikes;

    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerViewAdapter adapter;

    boolean isSearching = false;

    String searchedString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        etSearch = findViewById(R.id.etSearch);

        // Initialize the UI components and database helper
        recyclerView = findViewById(R.id.recyclerView);
        btn_add = findViewById(R.id.btn_add);
        btn_deleteAll = findViewById(R.id.btn_deleteAll);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve a list of hikes from the database
        hikeDataModelArrayList = databaseHelper.getHikes();
        filteredHikes = new ArrayList<>();
        adapter = new RecyclerViewAdapter(this, filteredHikes, this);


        // Set up the RecyclerView to display the list of hikes
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        // Create and set the adapter for the RecyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(this, hikeDataModelArrayList, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Add a TextWatcher to the search EditText
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform the search when text changes

                performSearch(s.toString());
                Log.d("SearchText", "" + s.toString());
                searchedString = s.toString();

                if (searchedString.isEmpty()) {
                    isSearching = false;
                } else {
                    isSearching = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Handle button clicks
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the InputHikeActivity when the "Add" button is clicked
                Intent intent = new Intent(HomeActivity.this, InputHikeActivity.class);
                startActivity(intent);
            }
        });
        btn_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteAllUsers();
                hikeDataModelArrayList.clear();
                filteredHikes.clear();
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
                recyclerViewAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerViewAdapter != null) {
            // Fetch the updated data from the database
            ArrayList<HikeDataModel> updatedData = databaseHelper.getHikes();

            // Update the dataset in the adapter
            recyclerViewAdapter.updateData(updatedData);


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


    public void onDeleteClick(int position) {
        if (isSearching == false) {
            HikeDataModel hikeData = hikeDataModelArrayList.get(position);

            int hikeId = hikeData.getId();
            long result = databaseHelper.deleteHike(hikeId);


            if (result != -1) {
                hikeDataModelArrayList.remove(position);
                recyclerViewAdapter.notifyItemRemoved(position);
                recyclerViewAdapter.notifyItemRangeChanged(position, hikeDataModelArrayList.size());
            }
        } else {
            HikeDataModel hikeData = filteredHikes.get(position);

            int hikeId = hikeData.getId();
            int originalPosition = findPositionById(hikeId, hikeDataModelArrayList);
            long result = databaseHelper.deleteHike(hikeId);

            if (result != -1) {

//                if (!searchedString.isEmpty()) {
//
//                    adapter.notifyDataSetChanged();
//                    filteredHikes.remove(position);
//                    adapter.notifyItemRemoved(position);
//                    adapter.notifyItemRangeChanged(position, filteredHikes.size());
//                } else{
                    recyclerView.setAdapter(recyclerViewAdapter);

                    hikeDataModelArrayList.remove(originalPosition);
                    recyclerViewAdapter.notifyItemRemoved(originalPosition);
                    recyclerViewAdapter.notifyItemRangeChanged(originalPosition, hikeDataModelArrayList.size());
                    isSearching = false;
               // }


            }
        }
    }

    private int findPositionById(int hikeId, ArrayList<HikeDataModel> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == hikeId) {
                return i;
            }
        }
        return -1; // Return -1 if the item is not found in the list
    }

    public void onEditClick(int position) {
        HikeDataModel hikeData;
        // Retrieve the selected hike based on the position
        hikeData = hikeDataModelArrayList.get(position);
        if (isSearching) {
            hikeData = filteredHikes.get(position);
            int hikeId = hikeData.getId();
            int originalPosition = findPositionById(hikeId, hikeDataModelArrayList);
            hikeData = hikeDataModelArrayList.get(originalPosition);
        }

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

    private void performSearch(String query) {
        filteredHikes.clear();


        Log.d("COUNT1", "" + filteredHikes.size());
        if (!query.isEmpty()) {

            for (HikeDataModel hike : hikeDataModelArrayList) {
                if (hike.getHikeName().toLowerCase().contains(query.toLowerCase())) {
                    filteredHikes.add(hike);
                }
            }
            Log.d("COUNT2", "" + filteredHikes.size());
        } else {
            // If the query is empty, show all hikes
            filteredHikes.addAll(hikeDataModelArrayList);
        }

        recyclerView.setAdapter(adapter);
        // Refresh the RecyclerView to reflect the changes
        recyclerViewAdapter.notifyDataSetChanged();
    }


}
