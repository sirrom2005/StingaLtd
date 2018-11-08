package com.stingaltd.stingaltd.Common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stingaltd.stingaltd.Models.Account;
import com.stingaltd.stingaltd.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Common {
    public static final String LOG_TAG = "stinga";
    public static final String BASE_URL = "http://104.248.118.6/api/public/";
    public static final String TEMP_PHOTO_PATH = "stinga";
    public static final String JOB_ITEM = "com.stingaltd.stingaltd.JOB_ITEM";
    public static final String SELECTED_IMG_FILE = "com.stingaltd.stingaltd.filename";
    public static final String WORK_ID_INTENT = "com.stingaltd.stingaltd.work_id";
    public static final String IMG_PRE = "_pre";
    public static final String IMG_POST = "_post";

    public static AlertDialog alert= null;
    public static View confirmation = null;

    public static String GetFileName(){
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
    }

    public static float convertPixelsToDp(float px, Context c){
        return px / ((float) c.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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

    public static Account getAccount(Context c){
        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.preference_email), c.MODE_PRIVATE);
        String email = sharedPref.getString(c.getString(R.string.preference_email_key), "");

        Account obj = null;
        try {
            obj = (Account) Common.readObjectFromFile(c, Common.getFileNameFromEmail(email));
        } catch (IOException | ClassNotFoundException ex) {
            Log.e(Common.LOG_TAG, ex.getMessage());
        }

        return obj;
    }

    public static Object readObjectFromFile(Context c, String fileName) throws IOException, ClassNotFoundException {
        File path = new File(c.getFilesDir(),fileName);
        FileInputStream fis = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    public static void SaveObjectAsFile(Context c, Object obj, String fileName) throws IOException {
        File path = new File(c.getFilesDir(),fileName);
        FileOutputStream fos  = new FileOutputStream(path);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(obj);
        os.close();
    }

    public static void ConfirmMsg(Context c, String msg) {
        confirmation = View.inflate(c, R.layout.confirmation_box_layout, null);
        final boolean[] task = {false};

        TextView text = confirmation.findViewById(R.id.message);
        text.setText(msg);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.setView(confirmation);
        alert.setCancelable(false);
        alert.show();
    }
}
