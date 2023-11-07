package com.example.mhike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "hike_data.db";
    public static final String TABLE_HIKES = "hikes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HIKE_NAME = "hike_name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_HIKE_LENGTH = "hike_length";
    public static final String COLUMN_HIKE_DATE = "hike_date";
    public static final String COLUMN_PARKING_AVAILABLE = "parking_available";
    public static final String COLUMN_EQUIPMENT = "equipment";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_RATING = "rating";

    public static final String TABLE_OBSERVATION = "observations";
    public static final String COLUMN_OBSERVATION_ID = "id";
    public static final String COLUMN_HIKE_ID = "hike_id";
    public static final String COLUMN_OBSERVATION_TEXT = "observation_text";
    public static final String COLUMN_OBSERVATION_TIME = "observation_time";
    public static final String COLUMN_ADDITIONAL_CMT = "additional_cmt";


    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_HIKES = "CREATE TABLE " + TABLE_HIKES +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_HIKE_NAME + " TEXT NOT NULL," +
                COLUMN_LOCATION + " TEXT NOT NULL," +
                COLUMN_HIKE_LENGTH + " REAL NOT NULL," +
                COLUMN_HIKE_DATE + " TEXT NOT NULL," +
                COLUMN_PARKING_AVAILABLE + " INTEGER NOT NULL," +
                COLUMN_EQUIPMENT + " TEXT," +
                COLUMN_DIFFICULTY + " TEXT NOT NULL," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_RATING + " REAL" +
                ")";
        db.execSQL(CREATE_TABLE_HIKES);

        String CREATE_TABLE_OBSERVATIONS = "CREATE TABLE " + TABLE_OBSERVATION +
                "(" +
                COLUMN_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_HIKE_ID + " INTEGER NOT NULL," +
                COLUMN_OBSERVATION_TEXT + " TEXT NOT NULL," +
                COLUMN_OBSERVATION_TIME + " TEXT NOT NULL," +
                COLUMN_ADDITIONAL_CMT + " TEXT," +
                "FOREIGN KEY(" + COLUMN_HIKE_ID + ") REFERENCES " + TABLE_HIKES + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_TABLE_OBSERVATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropHikesTable = "DROP TABLE IF EXISTS " + TABLE_HIKES;
        db.execSQL(dropHikesTable);
        String dropObservationsTable = "DROP TABLE IF EXISTS " + TABLE_OBSERVATION;
        db.execSQL(dropObservationsTable);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;"); //foreign key
    }

    // insert hike
    public long insertHike(HikeDataModel hikeDataModel) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_HIKE_NAME, hikeDataModel.getHikeName());
        cv.put(COLUMN_LOCATION, hikeDataModel.getLocation());
        cv.put(COLUMN_HIKE_LENGTH, hikeDataModel.getHikeLength());
        cv.put(COLUMN_HIKE_DATE, hikeDataModel.getHikeDate());
        cv.put(COLUMN_PARKING_AVAILABLE, hikeDataModel.isParkingAvailable() ? 1 : 0);
        cv.put(COLUMN_EQUIPMENT, hikeDataModel.getEquipment());
        cv.put(COLUMN_DIFFICULTY, hikeDataModel.getDifficulty());
        cv.put(COLUMN_DESCRIPTION, hikeDataModel.getDescription());
        cv.put(COLUMN_RATING, hikeDataModel.getRating());


        long hikeId = db.insertOrThrow(TABLE_HIKES, null, cv);

        return hikeId;
    }

    //insert observations
    public long insertObservation(ObservationDataModel observationDataModel) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(COLUMN_OBSERVATION_TEXT, observationDataModel.getObservationText());
        values.put(COLUMN_OBSERVATION_TIME, observationDataModel.getObservationTime());
        values.put(COLUMN_ADDITIONAL_CMT, observationDataModel.getAdditionalComment());
        values.put(COLUMN_HIKE_ID, observationDataModel.getHikeId());

        long observationId = db.insertOrThrow(TABLE_OBSERVATION, null, values);

        return observationId;
    }

    //get all hikes
    public ArrayList<HikeDataModel> getHikes() {
        db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HIKES, new String[]{
                COLUMN_ID,
                COLUMN_HIKE_NAME,
                COLUMN_LOCATION,
                COLUMN_HIKE_LENGTH,
                COLUMN_HIKE_DATE,
                COLUMN_PARKING_AVAILABLE,
                COLUMN_EQUIPMENT,
                COLUMN_DIFFICULTY,
                COLUMN_DESCRIPTION,
                COLUMN_RATING
        }, null, null, null, null, null);

        ArrayList<HikeDataModel> hikeDataModelArrayList = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String hikeName = cursor.getString(1);
            String hikeLocation = cursor.getString(2);
            Double hikeLength = cursor.getDouble(3);
            String hikeDate = cursor.getString(4);
            Boolean parking = cursor.getInt(5) == 1;
            String equipment = cursor.getString(6);
            String difficulty = cursor.getString(7);
            String desc = cursor.getString(8);
            Float rating = cursor.getFloat(9);

            //add it to the list
            HikeDataModel hikeDataModel = new HikeDataModel(id, hikeName, hikeLocation, hikeLength, hikeDate, parking, equipment, difficulty, desc, rating);
            hikeDataModelArrayList.add(hikeDataModel);
            cursor.moveToNext();
        }
        cursor.close();

        return hikeDataModelArrayList;
    }

    //get all observations
    public ArrayList<ObservationDataModel> getObservations(int hikeId) {
        db = this.getReadableDatabase();

        String selection = COLUMN_HIKE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(hikeId)};

        Cursor cursor = db.query(
                TABLE_OBSERVATION,
                new String[]{
                        COLUMN_OBSERVATION_ID,
                        COLUMN_OBSERVATION_TEXT,
                        COLUMN_OBSERVATION_TIME,
                        COLUMN_ADDITIONAL_CMT,
                        COLUMN_HIKE_ID},
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        ArrayList<ObservationDataModel> observationDataModelArrayList = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int observationId = cursor.getInt(0);
            String observationText = cursor.getString(1);
            String observationTime = cursor.getString(2);
            String additionalComment = cursor.getString(3);
            int hike_id = cursor.getInt(4);


            ObservationDataModel observationList = new ObservationDataModel(observationId, hike_id, observationText, observationTime, additionalComment);

            observationDataModelArrayList.add(observationList);
            cursor.moveToNext();
        }
        cursor.close();

        return observationDataModelArrayList;

    }

    //Delete hike
    public long deleteHike(int hikeId) {
        db = this.getWritableDatabase();

        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(hikeId)};
        long deletedHike = db.delete(TABLE_HIKES, whereClause, whereArgs);
        return deletedHike;
    }

    //delete observation
    public int deleteObservations(int observationId) {
        db = this.getWritableDatabase();

        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(observationId)};
        int deletedObs = db.delete(TABLE_OBSERVATION, whereClause, whereArgs);
        return deletedObs;
    }

    //update hike
    public int updateHike(HikeDataModel hikeDataModel) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_HIKE_NAME, hikeDataModel.getHikeName());
        cv.put(COLUMN_LOCATION, hikeDataModel.getLocation());
        cv.put(COLUMN_HIKE_LENGTH, hikeDataModel.getHikeLength());
        cv.put(COLUMN_HIKE_DATE, hikeDataModel.getHikeDate());
        cv.put(COLUMN_PARKING_AVAILABLE, hikeDataModel.isParkingAvailable() ? 1 : 0);
        cv.put(COLUMN_EQUIPMENT, hikeDataModel.getEquipment());
        cv.put(COLUMN_DIFFICULTY, hikeDataModel.getDifficulty());
        cv.put(COLUMN_DESCRIPTION, hikeDataModel.getDescription());
        cv.put(COLUMN_RATING, hikeDataModel.getRating());

        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(hikeDataModel.getId())};

        int updatedHike = db.update(TABLE_HIKES, cv, whereClause, whereArgs);

        db.close();

        return updatedHike;
    }

    ///Observation update
    public int updateObservation(ObservationDataModel observationDataModel) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_OBSERVATION_TEXT, observationDataModel.getObservationText());
        values.put(COLUMN_OBSERVATION_TIME, observationDataModel.getObservationTime());
        values.put(COLUMN_ADDITIONAL_CMT, observationDataModel.getAdditionalComment());
        values.put(COLUMN_HIKE_ID, observationDataModel.getHikeId());

        String whereClause = COLUMN_OBSERVATION_ID + " = ?";
        String[] whereArgs = {String.valueOf(observationDataModel.getObservationId())};

        int rowsUpdated = db.update(TABLE_OBSERVATION, values, whereClause, whereArgs);

        db.close();

        return rowsUpdated;
    }

    //delete all
    public void deleteAllUsers() {
        db.delete(TABLE_HIKES, null, null);

    }

}
