package com.stingaltd.stingaltd.Common;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.stingaltd.stingaltd.Models.Account;
import com.stingaltd.stingaltd.Models.JobItem;
import com.stingaltd.stingaltd.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class Common {
    public static final String LOG_TAG = "StingaLog";
    public static final String BASE_URL = "http://104.248.118.6/api/public/";
    public static final String TEMP_PHOTO_PATH = "tmp";
    public static final String JOB_ITEM = "com.stingaltd.stingaltd.JOB_ITEM";
    public static final String INVENTORY_ITEM = "com.stingaltd.stingaltd.INVENTORY_ITEM";
    public static final String RODREADING_ITEM = "com.stingaltd.stingaltd.RODREADING_ITEM";
    public static final String SELECTED_IMG_FILE = "com.stingaltd.stingaltd.filename";
    public static final String WORK_ID_INTENT = "com.stingaltd.stingaltd.work_id";
    public static final String EXPENSE_AMOUNT_ITEM = "com.stingaltd.stingaltd.expense_amount_id";
    public static final String IMG_PRE = "_pre";
    public static final String IMG_POST = "_post";
    public static final String POST_DATA = "_post_data_";
    public static final String POST_WORK_ID = "_work_id_";
    public static final String POST_FILENAME = "_work_id_";
    public static final String JOB_LIST_INDEX = "joblistidx";
    public static final int TIME_OUT = 5000;

    public static AlertDialog alert= null;
    public static WeakReference<View> confirmation = null;

    public static float convertPixelsToDp(float px, Context c){
        return px / ((float) c.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static boolean isInternetAvailable() {
        final String command = "ping -c 1 104.248.118.6";
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException | IOException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            return false;
        }
    }

    public static String getLocation(Context c) {
        double lat = 0, lon = 0;
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(lastKnownLocation!=null) {
                    lat = lastKnownLocation.getLatitude();
                    lon = lastKnownLocation.getLongitude();
                }
            }
        }
        return String.format("%s,%s", lat, lon) ;
    }

    public static float convertDpToPixel(float dp, Context c){
        Resources resources = c.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String getFileNameFromEmail(String email){
        //get first part of email use as file name
        String name = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
        return String.format("%s.json", name);
    }

    public static String getUserJobsFileName(Context c){
        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.preference_email), Context.MODE_PRIVATE);
        String email = sharedPref.getString(c.getString(R.string.preference_email_key), "");
        return "jobs_" + Common.getFileNameFromEmail(email);
    }

    public static Account getAccount(Context c){
        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.preference_email), Context.MODE_PRIVATE);
        String email = sharedPref.getString(c.getString(R.string.preference_email_key), "");
        Account obj = (Account) Common.readObjectFromFile(c, Common.getFileNameFromEmail(email));
        return obj;
    }

    public static JobItem FilterJobById(Context c, int id){
        List<JobItem> obj = (List<JobItem>) readObjectFromFile(c, getUserJobsFileName(c));
        JobItem T = null;
        for (JobItem x : obj) {
            if (id == x.getId()) {
                T = x;
                break;
            }
        }
        return T;
    }

    public static int GetJobByIdx(Context c, int id){
        List<JobItem> obj = (List<JobItem>) readObjectFromFile(c, getUserJobsFileName(c));
        int idx = 0;
        for (JobItem x : obj) {
            if (id == x.getId()) {
                return idx;
            }
            idx++;
        }
        return 999;
    }

    public static Object readObjectFromFile(Context c, String fileName){
        File path = new File(c.getFilesDir(),fileName);
        Object obj = null;

        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, String.format("%s => %s", "Common readObjectFromFile", e.getMessage()));
        } catch (IOException e) {
            Log.e(LOG_TAG, String.format("%s => %s", "Common readObjectFromFile", e.getMessage()));
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, String.format("%s => %s", "Common readObjectFromFile", e.getMessage()));
        }

        return obj;
    }

    public static void SaveObjectAsFile(Context c, Object obj, String fileName) throws IOException {
        File path = new File(c.getFilesDir(),fileName);
        FileOutputStream fos  = new FileOutputStream(path);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(obj);
        os.close();
    }

    public static void ConfirmMsg(Context c, String msg) {
        confirmation = new WeakReference<>(View.inflate(c, R.layout.confirmation_box_layout, null));

        TextView text = confirmation.get().findViewById(R.id.message);
        text.setText(msg);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.setView(confirmation.get());
        alert.setCancelable(false);
        alert.show();
    }

    public static void MessageBox(Context c, String msg){
        confirmation = new WeakReference<>(View.inflate(c, R.layout.message_box_layout, null));

        TextView text = confirmation.get().findViewById(R.id.message);
        text.setText(msg);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.setView(confirmation.get());
        alert.setCancelable(false);
        alert.show();
    }
}
