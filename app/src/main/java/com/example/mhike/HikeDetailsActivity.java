package com.example.mhike;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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


        // Retrieve data from intent
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

            // populate hike data
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

        recyclerView.setAdapter(observationRecyclerViewAdapter);


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
                } else {
                    // Get the values from the obs inputs
                    String observationText = observationName.getText().toString();
                    String observationT = observationTime.getText().toString();
                    String additionalComment = additionalCmt.getText().toString();


                    ObservationDataModel observationDataModel = new ObservationDataModel(0, (int) hike_id, observationText, observationT, additionalComment);

                    // Insert the observation into the database
                    long result = databaseHelper.insertObservation(observationDataModel);

                    if (result != -1) {

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


    // display time picker dialog:
    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the time selection
                        String selectedTime = hourOfDay + ":" + minute;
                        observationTime.setText(selectedTime);
                    }
                },
                // current time
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }
    @Override
    public void onDeleteClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HikeDetailsActivity.this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (position >= 0 && position < observationDataModelArrayList.size()) {
                            //observation to delete
                            int observationIdToDelete = observationDataModelArrayList.get(position).getObservationId();
                            // delete observation
                            int result = databaseHelper.deleteObservations(observationIdToDelete);

                            if (result > 0) {
                                Toast.makeText(HikeDetailsActivity.this, "Observation deleted successfully", Toast.LENGTH_SHORT).show();

                                // Update RecyclerView
                                observationDataModelArrayList.remove(position);

                                observationRecyclerViewAdapter.notifyItemRemoved(position);
                                observationRecyclerViewAdapter.updateData(observationDataModelArrayList);
                            } else {

                                Toast.makeText(HikeDetailsActivity.this, "Failed to delete observation", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onEditClick(int position) {
        if (position >= 0 && position < observationDataModelArrayList.size()) {
            //observation to edit
            ObservationDataModel observationToEdit = observationDataModelArrayList.get(position);

            // populate observation data
            observationName.setText(observationToEdit.getObservationText());
            observationTime.setText(observationToEdit.getObservationTime());
            additionalCmt.setText(observationToEdit.getAdditionalComment());

            // get observation data id
            int observationIdToEdit = observationToEdit.getObservationId();
            //update button text
            addObservation.setText("Update Observation");


            addObservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(observationName.getText().toString())
                            || TextUtils.isEmpty(observationTime.getText().toString())
                            || TextUtils.isEmpty(additionalCmt.getText().toString())) {
                        Toast.makeText(HikeDetailsActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    } else {
                        // Get values from inputs
                        String updatedObservationText = observationName.getText().toString();
                        String updatedObservationTime = observationTime.getText().toString();
                        String updatedAdditionalComment = additionalCmt.getText().toString();

                        // ObservationDataModel to update
                        ObservationDataModel updatedObservation = new ObservationDataModel(
                                observationIdToEdit, (int) hike_id, updatedObservationText, updatedObservationTime, updatedAdditionalComment
                        );

                        int result = databaseHelper.updateObservation(updatedObservation);

                        if (result > 0) {
                            Toast.makeText(HikeDetailsActivity.this, "Observation updated successfully", Toast.LENGTH_SHORT).show();

                            // Clear the input fields
                            observationName.setText("");
                            observationTime.setText("");
                            additionalCmt.setText("");
                            addObservation.setText("Add Observation");

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
