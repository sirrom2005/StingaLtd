package com.stingaltd.stingaltd.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.stingaltd.stingaltd.Common.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.stingaltd.stingaltd.Common.Common.PHOTO_PATH;
import static com.stingaltd.stingaltd.Common.Common.TEMP_PHOTO_PATH;

public class PhotoProcessor {
    public File createTmpImageFile() {
        File storageDir = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_PATH);

        if(!storageDir.exists()){
            if(storageDir.mkdir()){
                Log.d(Common.LOG_TAG, String.format("Dir created %s", storageDir));
            }
        }

        File image = null;
        try {
            image = File.createTempFile(
                    Common.ImageName(), /* prefix */
                    ".jpg",       /* suffix */
                    storageDir           /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public boolean MoveFile(Context c, String path, int workId, String photoFolder)
    {
        InputStream in;
        OutputStream out;
        try {
            File dir = new File(c.getFilesDir(),PHOTO_PATH);
            if(!dir.exists()){
                if(dir.mkdir()){
                    Log.d(Common.LOG_TAG, String.format("Dir created %s", dir));
                }
            }

            File WorkDir = new File(dir, String.valueOf(workId));
            if(!WorkDir.exists()){
                if(WorkDir.mkdir()){
                    Log.d(Common.LOG_TAG, String.format("Dir created %s", WorkDir));
                }
            }

            File imgDir = new File(WorkDir,photoFolder);
            if(!imgDir.exists()){
                if(imgDir.mkdir()){
                    Log.d(Common.LOG_TAG, String.format("Dir created %s", imgDir));
                }
            }

            File PhotoName = new File(imgDir, String.format("%s.jpg", Common.ImageName()));

            in  = new FileInputStream(path);
            out = new FileOutputStream(PhotoName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file
            out.flush();
            out.close();

            // delete the original file
            if(new File(path).delete()){
                return true;
            }
        } catch (Exception e) {
            Log.e(Common.LOG_TAG, e.getMessage());
        }
        return false;
    }
}
