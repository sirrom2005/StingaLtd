package com.stingaltd.stingaltd.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.stingaltd.stingaltd.SyncData.SyncData;

import static com.stingaltd.stingaltd.Common.Common.POST_WORK_ID;

public class PhotoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context c, final Intent intent) {
        Toast.makeText(c, "PhotoReceiver - BroadcastReceiver", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int id = intent.getIntExtra(POST_WORK_ID, 0 );
                SyncData.UploadPhoto(c, id);
            }
        }).start();
    }
}
