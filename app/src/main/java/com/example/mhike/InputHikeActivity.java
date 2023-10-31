package com.example.mhike;


import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InputHikeActivity extends AppCompatActivity {

    private EditText et_hikeName, et_location, et_hikeLength, et_hikeDate, et_equipment, et_desc;
    private RadioGroup rdg_parking;
    private RadioButton rd_yes, rd_no;
    private Button btn_submit;
    private RatingBar ratingBar;
    private Spinner spn_difficulty;
    private RecyclerViewAdapter recyclerViewAdapter;

    DatabaseHelper databaseHelper;

    SQLiteDatabase db;
    ArrayList<HikeDataModel> hikeDataModelArrayList;
    int hike_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_hike);

        et_hikeName = findViewById(R.id.et_hikeName);
        et_location = findViewById(R.id.et_location);
        et_hikeLength = findViewById(R.id.etnd_parking);
        et_hikeDate = findViewById(R.id.et_hikeDate);

        rdg_parking = findViewById(R.id.rdg_parking);
        rd_yes = findViewById(R.id.rd_yes);
        rd_no = findViewById(R.id.rd_no);

        et_equipment = findViewById(R.id.et_equipments);
        spn_difficulty = findViewById(R.id.spn_difficulty);
        et_desc = findViewById(R.id.et_desc);
        ratingBar = findViewById(R.id.ratingBar);
        btn_submit = findViewById(R.id.btn_submit);

        databaseHelper = new DatabaseHelper(this);
        hikeDataModelArrayList = databaseHelper.getHikes();


        //set up spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.difficulty, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_difficulty.setAdapter(adapter);

        //set date pop up
        et_hikeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Retrieve data from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hike_id = extras.getInt("hikeId");
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

            et_hikeName.setText(hikeName);
            et_location.setText(hikeLocation);
            et_hikeLength.setText(String.valueOf(hikeLength));
            et_hikeDate.setText(hikeDate);

            if (parkingAvailable) {
                rd_yes.setChecked(true);
            } else {
                rd_no.setChecked(true);
            }

            int position = adapter.getPosition(hikeDifficulty);
            spn_difficulty.setSelection(position);

            ratingBar.setRating(hikeRating);
            et_equipment.setText(hikeEquipment);
            et_desc.setText(hikeDescription);
        }


        //submit button click
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(et_hikeName.getText().toString())
                        || TextUtils.isEmpty(et_location.getText().toString())
                        || TextUtils.isEmpty(et_desc.getText().toString())
                        || TextUtils.isEmpty(et_hikeLength.getText().toString())
                        || TextUtils.isEmpty(et_hikeDate.getText().toString())
                        || TextUtils.isEmpty(spn_difficulty.getSelectedItem().toString())
                        || rdg_parking.getCheckedRadioButtonId() == -1) {


                    Toast.makeText(InputHikeActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                } else {
                    String dialogMessage = "Name of hike: " + et_hikeName.getText().toString() + "\n" +
                            "Location: " + et_location.getText().toString() + "\n" +
                            "Hike length (km): " + et_hikeLength.getText().toString() + "\n" +
                            "Date of hike: " + et_hikeDate.getText().toString() + "\n" +
                            "Parking available: " + (rd_yes.isChecked() ? "Yes" : "No") + "\n" +
                            "Equipments: " + et_equipment.getText().toString() + "\n" +
                            "Difficulty: " + spn_difficulty.getSelectedItem().toString() + "\n" +
                            "Description (Optional): " + et_desc.getText().toString() + "\n" +
                            "Rating: " + ratingBar.getRating();

                    showInputDetails(dialogMessage);

                }
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                et_hikeDate.setText(selectedDate);
            }
        },

                2023, 0, 1
        );


        datePickerDialog.show();
    }

    private void showInputDetails(String dialogMessage) {
        Dialog dialog = new Dialog(InputHikeActivity.this);
        dialog.setContentView(R.layout.confirmation_dialog);

        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView tv_inputDetails = dialog.findViewById(R.id.tv_inputDetails);

        tv_inputDetails.setText(dialogMessage);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Check if you received a hikeId, indicating editing
                boolean isEditing = false;
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    Integer hikeId = extras.getInt("hikeId");
                    if (hikeId != null && hikeId > 0) {
                        isEditing = true;
                    }
                }

// Now, you can use the isEditing flag to update or insert data accordingly
                if (isEditing) {
                    // Perform update
                    HikeDataModel hikeDataModel = new HikeDataModel(hike_id, et_hikeName.getText().toString(), et_location.getText().toString(), Double.parseDouble(et_hikeLength.getText().toString()), et_hikeDate.getText().toString(), rd_yes.isChecked(), et_equipment.getText().toString(), spn_difficulty.getSelectedItem().toString(), et_desc.getText().toString(), ratingBar.getRating());
                    long rowsAffected = databaseHelper.updateHike(hikeDataModel);

                    if (rowsAffected > 0) {
                        Toast.makeText(InputHikeActivity.this, "Data updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(InputHikeActivity.this, "Data update failed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Perform insert
                    HikeDataModel hikeDataModel = new HikeDataModel(0, et_hikeName.getText().toString(), et_location.getText().toString(), Double.parseDouble(et_hikeLength.getText().toString()), et_hikeDate.getText().toString(), rd_yes.isChecked(), et_equipment.getText().toString(), spn_difficulty.getSelectedItem().toString(), et_desc.getText().toString(), ratingBar.getRating());
                    long newRowId = databaseHelper.insertHike(hikeDataModel);

                    if (newRowId != -1) {
                        Toast.makeText(InputHikeActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(InputHikeActivity.this, "Data save failed.", Toast.LENGTH_SHORT).show();
                    }
                }

// Finish the activity and navigate back to HomeActivity

                Intent intent = new Intent(InputHikeActivity.this, HomeActivity.class);
                startActivity(intent);
               // finish();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}