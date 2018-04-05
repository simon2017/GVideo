package gvideo.sgutierc.cl.location;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.util.Log;

import java.io.File;

import gvideo.sgutierc.cl.videorecorder.DBHelper;
import gvideo.sgutierc.cl.videorecorder.R;

public class LocationRecorder implements LocationHandler {
    private Activity activity;
    private String TAG = LocationRecorder.class.getName();
    private DBHelper dbHelper = null;
    private SQLiteDatabase database = null;
    private String TABLE_NAME;
    private String LATITUDE;
    private String LONGITUDE;
    private String ALTITUDE;
    private String SPEED;
    private String ACCURACY;
    private String LAPSETIME;

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
            TABLE_NAME = activity.getString(R.string.TABLE_NAME);
            LATITUDE = activity.getString(R.string.LATITUDE);
            LONGITUDE = activity.getString(R.string.LONGITUDE);
            ALTITUDE = activity.getString(R.string.ALTITUDE);
            SPEED = activity.getString(R.string.SPEED);
            ACCURACY = activity.getString(R.string.ACCURACY);
            LAPSETIME = activity.getString(R.string.LAPSETIME);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            safeInit = false;
        }

        return safeInit;
    }

    private SQLiteDatabase createTempDB() throws Exception {
        File outputDir = activity.getCacheDir(); // context being the Activity pointer
        File tmpDB = File.createTempFile("location", "tmp", outputDir);
        Log.i(TAG,tmpDB.getAbsolutePath());
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

            ContentValues values = new ContentValues();
            values.put(LAPSETIME, elapsedTime);
            values.put(LATITUDE, latitude);
            values.put(LONGITUDE, longitude);
            values.put(ALTITUDE, altitude);
            values.put(ACCURACY, accuracy);
            values.put(SPEED, speed);

            database.insert(TABLE_NAME, null, values);

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }
}
