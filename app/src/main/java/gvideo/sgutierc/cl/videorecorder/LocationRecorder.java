package gvideo.sgutierc.cl.videorecorder;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.util.Log;

import java.io.File;

public class LocationRecorder implements LocationHandler {
    private Activity activity;
    private String TAG = LocationRecorder.class.getName();
    private DBHelper dbHelper = null;
    private SQLiteDatabase database = null;
    private String insertStatement = null;

    /**
     * @param activity
     */
    public LocationRecorder(Activity activity) {
        this.activity = activity;
    }

    /**
     * @return
     */
    public boolean init() {
        boolean safeInit = true;
        try {
            database = createTempDB();
            insertStatement = activity.getString(R.string.insert_values);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            safeInit = false;
        }

        return safeInit;
    }

    private SQLiteDatabase createTempDB() throws Exception {
        File outputDir = activity.getCacheDir(); // context being the Activity pointer
        File tmpDB = File.createTempFile("location", "tmp", outputDir);
        SQLiteDatabase database = null;
        try {
            dbHelper = new DBHelper(activity, tmpDB.getPath());
            database = dbHelper.getWritableDatabase();
            database = SQLiteDatabase.openDatabase(tmpDB.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException ex) {
            Log.e(TAG, ex.getMessage(), ex);
            // error means tables does not exits
        }
        return database;
    }

    @Override
    public void handleLocation(Location location, Event event) {
        //TODO guardar en bd temporal

        try {
            long elapsedTime = location.getElapsedRealtimeNanos();
            if (event == Event.START)
                elapsedTime = 0;//marca el inicio

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude = location.getAltitude();
            double accuracy = location.getAccuracy();
            double speed = location.getSpeed();
            
            database.execSQL(insertStatement, new Object[]{elapsedTime, altitude, longitude, latitude, speed, accuracy});

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }
}
