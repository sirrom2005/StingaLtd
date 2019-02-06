package com.stingaltd.stingaltd.JobScheduler;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.stingaltd.stingaltd.Classes.Data;

public class AddJsonDataJobService extends JobService {
    private Task task;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters params)
    {
        int WorkId = params.getExtras().getInt("WorkId");
        String JsonData = params.getExtras().getString("JsonData");
        String API = params.getExtras().getString("API");


        task = new Task(WorkId, JsonData, API){
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean){
                    jobFinished(params, false);
                }else{
                    jobFinished(params, true);
                }
            }
        };

        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        task.cancel(true);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class Task extends AsyncTask<String, Void, Boolean>
    {
        int workId;
        String jsonData;
        String API;

        public Task(int workId, String jsonData, String API) {
            this.workId     = workId;
            this.jsonData   = jsonData;
            this.API        = API;
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            return new Data().PostData(jsonData, workId, API);
        }
    }
}
