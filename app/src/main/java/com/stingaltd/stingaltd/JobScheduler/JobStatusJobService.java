package com.stingaltd.stingaltd.JobScheduler;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.stingaltd.stingaltd.Classes.UpdateJobStatus;

public class JobStatusJobService extends JobService {
    private Task task;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters params) {
        int WorkId = params.getExtras().getInt("WorkId");
        String Comment = params.getExtras().getString("Comment");

        task = new Task(WorkId, Comment){
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
    private class Task extends AsyncTask<String, Void, Boolean> {
        private int workId;
        private String comment;

        Task(int workId, String comment) {
            this.workId = workId;
            this.comment = comment;
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            return new UpdateJobStatus(getBaseContext()).Save(workId, comment);
        }
    }
}
