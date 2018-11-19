package com.stingaltd.stingaltd.Classes;

import android.content.Context;
import android.util.Log;

import com.stingaltd.stingaltd.Common.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Data {
    public static void PostData(final String json, final int work_id, final String path){
        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("data", json)
                .add("work_id", String.valueOf(work_id))
                .build();

        final Request request = new Request.Builder()
                .url(Common.BASE_URL + path)
                .post(requestBody)
                .build();

        try{
            Response response   = client.newCall(request).execute();
            String body         = null;
            if (response.body() != null) {
                body = response.body().string();
            }
            Log.e(Common.LOG_TAG, body);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void SaveData(Context c, final String json, final int work_id, String filename){
        try {
            File dir = new File(c.getFilesDir(),String.valueOf(work_id));
            if(!dir.exists()){
                if(dir.mkdir()){
                    Log.d(Common.LOG_TAG, String.format("Directory created %s", dir));
                }
            }
            File filePath = new File(dir, filename);
            FileOutputStream fOut = new FileOutputStream(filePath);
            OutputStreamWriter writer = new OutputStreamWriter(fOut);
            writer.write(json);
            writer.close();
        }
        catch (IOException e) {
            Log.e(Common.LOG_TAG, "File write failed: " + e.toString());
        }
    }
}
