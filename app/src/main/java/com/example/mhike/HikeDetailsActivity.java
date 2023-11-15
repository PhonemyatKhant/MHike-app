package com.example.mhike;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class HikeDetailsActivity extends AppCompatActivity implements ObservationRecyclerViewInterface {
    private TextView hikeTitle,location, length, date, parking, difficulty, rating, equipment, description;

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
            rating.setText(String.valueOf(hikeRating)+"/5");
            equipment.setText(hikeEquipment);
            description.setText(hikeDescription);
        }
        //get observation data
        observationDataModelArrayList = databaseHelper.getObservations(hike_id);

        // Set up the RecyclerView

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observationRecyclerViewAdapter = new ObservationRecyclerViewAdapter(this, observationDataModelArrayList, this);

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
                        || TextUtils.isEmpty(additionalCmt.getText().toString())) {

                    Toast.makeText(HikeDetailsActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                } else {// All required fields are filled, so you can proceed to insert the observation data into the database

                    // Get the values from the UI
                    String observationText = observationName.getText().toString();
                    String observationT = observationTime.getText().toString();
                    String additionalComment = additionalCmt.getText().toString();


                    ObservationDataModel observationDataModel = new ObservationDataModel(0, (int) hike_id, observationText, observationT, additionalComment);

                    // Insert the observation into the database
                    long newRowId = databaseHelper.insertObservation(observationDataModel);

                    if (newRowId != -1) {

                        Toast.makeText(HikeDetailsActivity.this, "Observation added successfully", Toast.LENGTH_SHORT).show();

                        observationName.setText("");
                        observationTime.setText("");
                        additionalCmt.setText("");

                        observationDataModelArrayList = databaseHelper.getObservations(hike_id);
                        observationRecyclerViewAdapter.updateData(observationDataModelArrayList);

                    } else {
                        Toast.makeText(HikeDetailsActivity.this, "Failed to add observation" + hike_id, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onDeleteClick(int position) {

        // Check if the position is valid
        if (position >= 0 && position < observationDataModelArrayList.size()) {
            // Get the observation ID from the selected position
            int observationIdToDelete = observationDataModelArrayList.get(position).getObservationId();


            // Delete the observation from the database
            int deletedRows = databaseHelper.deleteObservations(observationIdToDelete);

            if (deletedRows > 0) {
                // Deletion was successful
                Toast.makeText(HikeDetailsActivity.this, "Observation deleted successfully", Toast.LENGTH_SHORT).show();

                // Update the RecyclerView with the latest data
                observationDataModelArrayList.remove(position);

                observationRecyclerViewAdapter.notifyItemRemoved(position);
                observationRecyclerViewAdapter.updateData(observationDataModelArrayList);
            } else {
                // Deletion failed
                Toast.makeText(HikeDetailsActivity.this, "Failed to delete observation", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onEditClick(int position) {
        if (position >= 0 && position < observationDataModelArrayList.size()) {
            ObservationDataModel observationToEdit = observationDataModelArrayList.get(position);

            // Set the existing observation data to the input fields
            observationName.setText(observationToEdit.getObservationText());
            observationTime.setText(observationToEdit.getObservationTime());
            additionalCmt.setText(observationToEdit.getAdditionalComment());

            // Store the observation ID in a variable for later use in the update
            int observationIdToEdit = observationToEdit.getObservationId();

            // Optionally, you can also update the button text to indicate editing
            addObservation.setText("Update Observation");

            // Define an update action for the "Update Observation" button
            addObservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(observationName.getText().toString())
                            || TextUtils.isEmpty(observationTime.getText().toString())
                            || TextUtils.isEmpty(additionalCmt.getText().toString())) {
                        Toast.makeText(HikeDetailsActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    } else {
                        // Get the updated values from the UI
                        String updatedObservationText = observationName.getText().toString();
                        String updatedObservationTime = observationTime.getText().toString();
                        String updatedAdditionalComment = additionalCmt.getText().toString();

                        // Create an updated ObservationDataModel
                        ObservationDataModel updatedObservation = new ObservationDataModel(
                                observationIdToEdit, (int) hike_id, updatedObservationText, updatedObservationTime, updatedAdditionalComment
                        );

                        // Call your updateObservation method here to update the observation in the database
                        int rowsUpdated = databaseHelper.updateObservation(updatedObservation);

                        if (rowsUpdated > 0) {
                            Toast.makeText(HikeDetailsActivity.this, "Observation updated successfully", Toast.LENGTH_SHORT).show();

                            // Clear the input fields and restore the button text
                            observationName.setText("");
                            observationTime.setText("");
                            additionalCmt.setText("");
                            addObservation.setText("Add Observation");

                            // Optionally, you can update the RecyclerView with the latest data
                            observationDataModelArrayList = databaseHelper.getObservations(hike_id);
                            observationRecyclerViewAdapter.updateData(observationDataModelArrayList);
                            Intent intent = getIntent();
                            finish(); // Finish the current activity
                            startActivity(intent); // Start the activity again
                        } else {
                            Toast.makeText(HikeDetailsActivity.this, "Failed to update observation", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

}
