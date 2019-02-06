package com.stingaltd.stingaltd.Apis;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stingaltd.stingaltd.Adapter.JobItemAdapter;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Interface.IJobItem;
import com.stingaltd.stingaltd.MainActivity;
import com.stingaltd.stingaltd.Models.JobItem;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.stingaltd.stingaltd.Common.Common.BASE_URL;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;


public class GetJobList implements Callback<List<JobItem>>
{
    public void start(int technician_id)
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IJobItem action = retrofit.create(IJobItem.class);

        Call<List<JobItem>> call = action.repo(technician_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<JobItem>> call, Response<List<JobItem>> response) { }
    @Override
    public void onFailure(Call<List<JobItem>> call, Throwable t) {}
}