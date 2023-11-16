package com.example.mhike;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

        // ui elements
        recyclerView = findViewById(R.id.recyclerView);
        btn_add = findViewById(R.id.btn_add);
        btn_deleteAll = findViewById(R.id.btn_deleteAll);
        databaseHelper = new DatabaseHelper(this);

        //rv adapter
        hikeDataModelArrayList = databaseHelper.getHikes();
        recyclerViewAdapter = new RecyclerViewAdapter(this, hikeDataModelArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(recyclerViewAdapter);

        filteredHikes = new ArrayList<>();
        adapter = new RecyclerViewAdapter(this, filteredHikes, this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                performSearch(s.toString());
                Log.d("SearchText", "" + s.toString());
                searchedString = s.toString();

                if (searchedString.isEmpty() || searchedString == "" ) {
                    isSearching = false;
                } else {
                    isSearching = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InputHikeActivity.class);
                startActivity(intent);
            }
        });
        btn_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete all hikes?")
                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User confirmed, proceed with delete-all operation
                                databaseHelper.deleteAllUsers();
                                hikeDataModelArrayList.clear();
                                filteredHikes.clear();

                                adapter.notifyDataSetChanged();
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerViewAdapter != null) {
            ArrayList<HikeDataModel> updatedData = databaseHelper.getHikes();
            recyclerViewAdapter.updateData(updatedData);
        }
    }

    @Override
    public void onItemClick(int position) {

        HikeDataModel hikeData = hikeDataModelArrayList.get(position);

        Intent intent = new Intent(HomeActivity.this, HikeDetailsActivity.class);

        // Pass data to hikedetailsactivity

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

        startActivity(intent);
    }


    public void onDeleteClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete Hike", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isSearching == false) {
                            //data to delete
                            HikeDataModel hikeData = hikeDataModelArrayList.get(position);
                            //id to delete
                            int hikeId = hikeData.getId();
                            long result = databaseHelper.deleteHike(hikeId);


                            if (result != -1) {
                                hikeDataModelArrayList.remove(position);
                                recyclerViewAdapter.notifyItemRemoved(position);
                                recyclerViewAdapter.notifyItemRangeChanged(position, hikeDataModelArrayList.size());
                                //recyclerView.setAdapter(recyclerViewAdapter);
                            }
                        } else {
                            HikeDataModel hikeData = filteredHikes.get(position);

                            int hikeId = hikeData.getId();
                            int originalPosition = findPositionById(hikeId, hikeDataModelArrayList);
                            long result = databaseHelper.deleteHike(hikeId);

                            if (result != -1) {
                                etSearch.setText("");


                                hikeDataModelArrayList.remove(originalPosition);
                                recyclerViewAdapter.notifyItemRemoved(originalPosition);
                                recyclerViewAdapter.notifyItemRangeChanged(originalPosition, hikeDataModelArrayList.size());
                                recyclerView.setAdapter(recyclerViewAdapter);
                                //isSearching = false;
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private int findPositionById(int hikeId, ArrayList<HikeDataModel> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == hikeId) {
                return i;
            }
        }
        return -1; //not found
    }

    public void onEditClick(int position) {

        HikeDataModel hikeData;
        hikeData = hikeDataModelArrayList.get(position);
        if (isSearching) {
            hikeData = filteredHikes.get(position);
            int hikeId = hikeData.getId();
            int originalPosition = findPositionById(hikeId, hikeDataModelArrayList);
            hikeData = hikeDataModelArrayList.get(originalPosition);
        }

        Intent intent = new Intent(HomeActivity.this, InputHikeActivity.class);

        // Pass data to input forms
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
            recyclerView.setAdapter(adapter);
        } else {
            // if empty show all hikes
            //filteredHikes.addAll(hikeDataModelArrayList);
            recyclerView.setAdapter(recyclerViewAdapter);
        }


        recyclerViewAdapter.notifyDataSetChanged();
    }


}
