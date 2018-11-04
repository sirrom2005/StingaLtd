package com.stingaltd.stingaltd.Interface;
import com.stingaltd.stingaltd.Models.JobItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IJobItem {
    @GET("job/{technician_id}")
    Call<List<JobItem>> repo(@Path("technician_id") int technician_id);
}
