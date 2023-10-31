package com.example.mhike;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class HikeDetailsActivity extends AppCompatActivity {

    private TextView hikeTitle;
    private TextView location, length, date, parking, difficulty, rating, equipment, description;

    private Button addObservation;

    private EditText observationName, observationTime, additionalCmt;

    DatabaseHelper databaseHelper;

    int hike_id;
    private RecyclerViewAdapter adapter;

    RecyclerView recyclerView;

    ArrayList<ObservationDataModel> observationDataModelArrayList;

    ObservationRecyclerViewAdapter observationRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_details);

        hikeTitle = findViewById(R.id.tv_hikeTitle);
        location = findViewById(R.id.ti_location);
        length = findViewById(R.id.ti_length);
        date = findViewById(R.id.ti_date);
        parking = findViewById(R.id.ti_parking);
        difficulty = findViewById(R.id.ti_difficulty);
        rating = findViewById(R.id.ti_rating);
        equipment = findViewById(R.id.ti_equipment);
        description = findViewById(R.id.ti_desc);

        observationName = findViewById(R.id.et_observation);
        observationTime = findViewById(R.id.et_observationTime);
        additionalCmt = findViewById(R.id.et_additionalCmt);
        addObservation = findViewById(R.id.btn_addObs);

        databaseHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rv_observations);




        // Retrieve data from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer hikeId = extras.getInt("hikeId");
            String hikeName = extras.getString("hikeName");
            String hikeLocation = extras.getString("location");
            double hikeLength = extras.getDouble("hikeLength");
            String hikeDate = extras.getString("hikeDate");
            boolean parkingAvailable = extras.getBoolean("parkingAvailable");
            String hikeDifficulty = extras.getString("difficulty");
            float hikeRating = extras.getFloat("rating");
            String hikeEquipment = extras.getString("equipment");
            String hikeDescription = extras.getString("description");

            // Set the data to the corresponding views
            hike_id = hikeId;
            hikeTitle.setText(hikeName);
            location.setText(hikeLocation);
            length.setText(String.valueOf(hikeLength));
            date.setText(hikeDate);
            parking.setText(parkingAvailable ? "Yes" : "No");
            difficulty.setText(hikeDifficulty);
            rating.setText(String.valueOf(hikeRating));
            equipment.setText(hikeEquipment);
            description.setText(hikeDescription);
        }
        //get observation data
        observationDataModelArrayList = databaseHelper.getObservations(hike_id);


        // Set up the RecyclerView to display the list of hikes



        // Create and set the adapter for the RecyclerView

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observationRecyclerViewAdapter = new ObservationRecyclerViewAdapter(this, observationDataModelArrayList);

        recyclerView.setAdapter(observationRecyclerViewAdapter); // Use the correct adapter


        //time pop up
        observationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //add observation
        addObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(observationName.getText().toString())
                        || TextUtils.isEmpty(observationTime.getText().toString())
                        || TextUtils.isEmpty(additionalCmt.getText().toString()))
                        {

                    Toast.makeText(HikeDetailsActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                } else{// All required fields are filled, so you can proceed to insert the observation data into the database

                    // Get the values from the UI
                    String observationText = observationName.getText().toString();
                    String observationT = observationTime.getText().toString();
                    String additionalComment = additionalCmt.getText().toString();


                    ObservationDataModel observationDataModel = new ObservationDataModel((int) hike_id,observationText,observationT,additionalComment);

                    // Insert the observation into the database
                    long newRowId = databaseHelper.insertObservation(observationDataModel);

                    if (newRowId != -1) {
                        // Insertion was successful
                        Toast.makeText(HikeDetailsActivity.this, "Observation added successfully", Toast.LENGTH_SHORT).show();

                        // Optionally, you can clear the input fields
                        observationName.setText("");
                        observationTime.setText("");
                        additionalCmt.setText("");

                        observationDataModelArrayList = databaseHelper.getObservations(hike_id);
                        observationRecyclerViewAdapter.updateData(observationDataModelArrayList);

                    } else {
                        // Insertion failed

                        Toast.makeText(HikeDetailsActivity.this, "Failed to add observation"+hike_id, Toast.LENGTH_SHORT).show();
                    }
                }
                }
            }
        );
    }



    // Define the showTimePickerDialog method to display a time picker dialog:
    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the time selection here
                        String selectedTime = hourOfDay + ":" + minute;
                        observationTime.setText(selectedTime);
                    }
                },
                // Initialize with the current time
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }
}
