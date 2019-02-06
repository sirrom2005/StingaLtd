package com.stingaltd.stingaltd.JobScheduler;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.stingaltd.stingaltd.Classes.UploadImage;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;

import java.io.File;

public class PhotoDeleteJobService extends JobService {
    private Task task;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters params) {
        int WorkId = params.getExtras().getInt("WorkId");
        String FileName = params.getExtras().getString("FileName");


        task = new Task(WorkId, FileName){
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
    private class Task extends AsyncTask<Integer, Void, Boolean>
    {
        int WorkId;
        String fileName;

        Task(int workId, String fileName)
        {
            this.WorkId = workId;
            this.fileName = fileName;
        }

        @Override
        protected Boolean doInBackground(Integer... integers)
        {
            String Path = String.valueOf(WorkId)+"/img";
            File dir = new File(getFilesDir(), Path);
            File _file = new File(dir, fileName);
            if(_file.exists()){
                ImageData obj = (ImageData) Common.readObjectFromFile(getBaseContext(), Path+"/"+fileName);
                if(_file.delete()) {
                    return new UploadImage(getBaseContext()).DeletePhoto(obj.getJobId(), obj.getDateCreated());
                }
            }
            return false;
        }
    }
}
