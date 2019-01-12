package com.stingaltd.stingaltd.Classes;

import android.content.Context;
import android.util.Log;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.stingaltd.stingaltd.Common.Common.TIME_OUT;

public class UploadImage {
    private Context c;
    private OkHttpClient mClient;

    public UploadImage(Context c) {
        this.c = c;
        this.mClient = new OkHttpClient().newBuilder()
                            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                            .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                            .build();
    }

    public boolean Upload(String FilePath)
    {
        try{
            ImageData obj = (ImageData) Common.readObjectFromFile(c, FilePath);
            if(obj.Uploaded()==0)
            {
                Log.e(Common.LOG_TAG, "Attempting to upload >> " + FilePath);
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

                if(RequestResponse(request)){
                    obj.setUploaded(1);
                    Common.SaveObjectAsFile(c, obj, FilePath);
                    return true;
                }
            }
        }catch (IOException | ClassNotFoundException ex) {
            Log.e(Common.LOG_TAG, String.format("%s >>>> %s", "UploadImage", ex.getMessage()));
        }
        return false;
    }

    public boolean DeletePhoto(int id, String dateCreated)
    {
        try
        {
            RequestBody requestBody = new FormBody.Builder()
                    .add("imgId", String.valueOf(id))
                    .add("dateCreated", dateCreated)
                    .build();

            Request request = new Request.Builder()
                    .url(Common.BASE_URL + "delete_image.php")
                    .post(requestBody)
                    .build();

            if(RequestResponse(request)) {
                Log.e(Common.LOG_TAG, String.format("Delete Photo >> %s id=>[%d] date=>[%s]", "Image deleted", id, dateCreated));
                return true;
            }
        }catch (Exception ex) {
            Log.e(Common.LOG_TAG, String.format("%s >>>> %s", "Delete Photo", ex.getMessage()));
        }
        return false;
    }

    private boolean RequestResponse(Request request){
        try {
            Response response = mClient.newCall(request).execute();
            if (response.body() != null) {
                String rs = response.body().string();
                Log.e(Common.LOG_TAG, String.format("%s >> %s", "Response", rs));
                if (rs.equals("1")) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Log.e(Common.LOG_TAG, String.format("%s >>>> %s", "Response exception >> ", ex.getMessage()));
        }
        return false;
    }
}
