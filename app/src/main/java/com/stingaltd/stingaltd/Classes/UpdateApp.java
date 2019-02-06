package com.stingaltd.stingaltd.Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.stingaltd.stingaltd.Common.Common.TEMP_PHOTO_PATH;

public class UpdateApp extends AsyncTask<Void, Void, Boolean> {
    private String PATH = "";
    private String FileName = "stinga1.apk";
    String destination;
    private WeakReference<Context> context;

    public UpdateApp(Context c) {
        context = new WeakReference<>(c);
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        try {
            URL url = new URL("http://104.248.118.6/apk/"+FileName);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            PATH = String.format("%s/%s", Environment.getExternalStorageDirectory(), TEMP_PHOTO_PATH);
            Log.d(Common.LOG_TAG, ">>> " + PATH);
            File file = new File(PATH);
            if(file.mkdirs()){
                Log.d(Common.LOG_TAG, PATH + " created");
            }
            File outputFile = new File(file, FileName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            return outputFile.exists();
        } catch (IOException ex) {
            Log.e(Common.LOG_TAG, PATH + ">>>" + ex.getMessage());
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean) {
            Uri apkURI = FileProvider.getUriForFile(context.get(),
                    context.get().getResources().getString(R.string.file_provider),
                    new File(String.format("%s/%s", PATH, FileName)));

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            install.setDataAndType(apkURI,"application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.get().startActivity(install);
        }else
        {
            Toast.makeText(context.get(), "No updates found", Toast.LENGTH_LONG).show();
        }
    }
}
/*destination = String.format("%s/%s/%s", Environment.getExternalStorageDirectory(), TEMP_PHOTO_PATH, FileName);
Log.e(Common.LOG_TAG,   "=>>>" + destination);
final Uri uri = Uri.parse("file://" + destination);
DownloadManager.Request request = new  DownloadManager.Request(Uri.parse("http://104.248.118.6/apk/"+FileName));
request.setTitle("title");
request.setDescription("description");
request.setDestinationUri(uri);
manager = (DownloadManager) context.get().getSystemService(Context.DOWNLOAD_SERVICE);
downloadId = manager.enqueue(request);*/