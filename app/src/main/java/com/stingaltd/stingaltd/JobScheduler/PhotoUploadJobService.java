package com.stingaltd.stingaltd.JobScheduler;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.stingaltd.stingaltd.Classes.UploadImage;

public class PhotoUploadJobService extends JobService {
    private Task task;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters params) {
        String FileName = params.getExtras().getString("FileName");

        task = new Task(){
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

        task.execute(FileName);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        task.cancel(true);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class Task extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings)
        {
            return new UploadImage(getBaseContext()).Upload(strings[0]);
        }
    }
}
