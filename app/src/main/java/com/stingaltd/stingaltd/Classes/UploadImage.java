package com.stingaltd.stingaltd.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadImage {
    private Context c;

    public UploadImage(Context c) {
        this.c = c;
    }

    public void Upload(final String filename) {
        @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                final OkHttpClient client = new OkHttpClient();

                try
                {
                    ImageData obj = (ImageData) Common.readObjectFromFile(c, filename);

                    if(obj.Uploaded()==0)
                    {
                        RequestBody requestBody = new FormBody.Builder()
                                .add("JobId",       String.valueOf(obj.getJobId()))
                                .add("DateCreated", obj.getDateCreated())
                                .add("PhotoType",   obj.getPhotoType())
                                .add("Location",    obj.getLocation())
                                .add("Label",       obj.getLabel())
                                .add("Thumb",       obj.getThumb())
                                .add("LargeImage",  obj.getLargeImage())
                                .build();

                        final Request request = new Request.Builder()
                                .url(Common.BASE_URL + "upload_image.php")
                                .post(requestBody)
                                .build();

                        try {
                            Response response = client.newCall(request).execute();
                            String body = null;
                            if (response.body() != null) {
                                body = response.body().string();
                                obj.setUploaded(1);
                                Common.SaveObjectAsFile(c, obj, filename);
                            }
                            Log.e(Common.LOG_TAG, body);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (IOException | ClassNotFoundException ex) {
                    Log.e(Common.LOG_TAG, ex.getMessage());
                }
                return null;
            }
        };

        task.execute();
    }
}
