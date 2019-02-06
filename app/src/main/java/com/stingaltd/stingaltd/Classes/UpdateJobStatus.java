package com.stingaltd.stingaltd.Classes;

import android.content.Context;
import android.util.Log;

import com.stingaltd.stingaltd.Common.Common;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.stingaltd.stingaltd.Common.Common.TIME_OUT;

public class UpdateJobStatus
{
    private Context c;
    private OkHttpClient mClient;

    public UpdateJobStatus(Context c) {
        this.c = c;
        this.mClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    public boolean Save(int WorkId, String Comment)
    {
        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("WorkId",  String.valueOf(WorkId))
                    .add("Comment", Comment)
                    .build();

            Request request = new Request.Builder()
                    .url(Common.BASE_URL + "update_job_status.php")
                    .post(requestBody)
                    .build();

            if (RequestResponse(request)) {
                return true;
            }
        } catch (Exception ex) {
            Log.e(Common.LOG_TAG, String.format("Error updating job status >> %s", ex.getMessage()));
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
