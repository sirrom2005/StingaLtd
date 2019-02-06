package com.stingaltd.stingaltd.JobScheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.R;

public class Util
{
    public static void PhotoUploadScheduleJob(Context context, String FileName) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("FileName", FileName);

        ComponentName serviceComponent = new ComponentName(context, PhotoUploadJobService.class);
        JobInfo.Builder jobInfo = new JobInfo.Builder(GetRandJobId(), serviceComponent);

        jobInfo.setPersisted(true);
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobInfo.setExtras(bundle);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(Common.LOG_TAG, "Job scheduled!");
        } else {
            Log.d(Common.LOG_TAG, "Job not scheduled");
        }
    }

    public static void PhotoDeleteScheduleJob(Context context, int workId, String FileName) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("FileName", FileName);
        bundle.putInt("WorkId", workId);

        ComponentName serviceComponent = new ComponentName(context, PhotoDeleteJobService.class);
        JobInfo.Builder jobInfo = new JobInfo.Builder(GetRandJobId(), serviceComponent);

        jobInfo.setPersisted(true);
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobInfo.setExtras(bundle);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(Common.LOG_TAG, "Job scheduled!");
        } else {
            Log.d(Common.LOG_TAG, "Job not scheduled");
        }
    }

    public static void UpdateStatusScheduleJob(Context context, int workId, String Comment) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("Comment", Comment);
        bundle.putInt("WorkId", workId);

        ComponentName serviceComponent = new ComponentName(context, JobStatusJobService.class);
        JobInfo.Builder jobInfo = new JobInfo.Builder(GetRandJobId(), serviceComponent);

        jobInfo.setPersisted(true);
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobInfo.setExtras(bundle);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(Common.LOG_TAG, "Job scheduled!");
        } else {
            Log.d(Common.LOG_TAG, "Job not scheduled");
        }
    }

    public static void AddJsonDataScheduleJob(Context context, String JsonData, int workId, String API) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("JsonData", JsonData);
        bundle.putInt("WorkId", workId);
        bundle.putString("API", API);

        ComponentName serviceComponent = new ComponentName(context, AddJsonDataJobService.class);
        JobInfo.Builder jobInfo = new JobInfo.Builder(GetRandJobId(), serviceComponent);

        jobInfo.setPersisted(true);
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobInfo.setExtras(bundle);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Common.MessageBox(context, context.getString(R.string.form_submitted));
            final Button action = Common.confirmation.get().findViewById(R.id.action);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.alert.dismiss();
                }
            });
        } else {
            Log.d(Common.LOG_TAG, "Job not scheduled");
        }
    }

    public static void Test(Context context) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("ID", 555);

        ComponentName serviceComponent = new ComponentName(context, PhotoUploadJobService.class);
        JobInfo.Builder jobInfo = new JobInfo.Builder(101, serviceComponent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo.setMinimumLatency(5000);
            jobInfo.setOverrideDeadline(3000);
        }else {
            jobInfo.setPeriodic(5000);
        }

        jobInfo.setPersisted(true);
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobInfo.setExtras(bundle);


        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(Common.LOG_TAG, "Job scheduled!");
        } else {
            Log.d(Common.LOG_TAG, "Job not scheduled");
        }
    }

    private static int GetRandJobId() { return (int) (1000 + Math.random() * 9000); }
}
