package com.stingaltd.stingaltd.Classes;

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

public class Data {
    public boolean PostData(final String json, final int work_id, final String API){
        final OkHttpClient client = new OkHttpClient().newBuilder()
                                        .connectTimeout(TIME_OUT,   TimeUnit.MILLISECONDS)
                                        .readTimeout(TIME_OUT,      TimeUnit.MILLISECONDS)
                                        .writeTimeout(TIME_OUT,     TimeUnit.MILLISECONDS)
                                        .build();

        RequestBody requestBody = new FormBody.Builder()
                .add("data", json)
                .add("work_id", String.valueOf(work_id))
                .build();

        final Request request = new Request.Builder()
                .url(Common.BASE_URL + API)
                .post(requestBody)
                .build();

        try{
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                String body = response.body().string();
                Log.e(Common.LOG_TAG, String.format("%s >> %s", API, body));
                if(Integer.parseInt(body)>0){
                    return true;
                }
            }
        } catch(IOException ex) {
            Log.e(Common.LOG_TAG, ex.getMessage());
        }
        return false;
    }
}
