package com.stingaltd.stingaltd.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;
import com.stingaltd.stingaltd.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.util.Base64.encodeToString;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
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
                    Common.GetFileName(), /* prefix */
                    ".jpg",       /* suffix */
                    storageDir           /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public void SavePhotoJsonData(final Context c, final String ImagePath, final int WorkId, final String PhotoType, final int JobPos, final String JobType)
    {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids)
            {
                try {
                    float angle         = GetImageRotation(ImagePath);
                    Bitmap bitmap       = BitmapFactory.decodeFile(ImagePath);
                    String LargeImage   = GetImage(bitmap, angle, 2000);
                    String Thumb        = GetImage(bitmap, angle, 240);
                    String FilePath     = String.format("/%s/%s%s.json",WorkId, JobPos, PhotoType);

                    File dir = new File(c.getFilesDir(),String.valueOf(WorkId));
                    if(!dir.exists()){
                        if(dir.mkdir()){
                            Log.d(Common.LOG_TAG, String.format("Directory created %s", dir));
                        }
                    }

                    //get list of jo0b type from account just type list
                    String PhotoLabel = Objects.requireNonNull(Common.getAccount(c).getGalleryLable().get(JobType))[JobPos];

                    String DateCreated = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());

                    ImageData imageData = new ImageData(WorkId, DateCreated, PhotoType, "", PhotoLabel,0,Thumb,LargeImage);
                    Common.SaveObjectAsFile(c, imageData, FilePath);
                }catch (IOException | NullPointerException ex)
                {
                    Log.e(Common.LOG_TAG, ex.getMessage());
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBool) {
                super.onPostExecute(aBool);
                if(!aBool)
                {
                    Toast.makeText(c,R.string.photo_error_local_folder,Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.execute();
    }

    private float GetImageRotation(String imagePath)
    {
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90f;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180f;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270f;
                case ExifInterface.ORIENTATION_NORMAL:
                    return 0f;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    return 0f;
                default:
                    return 0f;
            }
        } catch (IOException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return 0f;
    }

    private String GetImage(Bitmap source, float angle, int width)
    {
        Matrix matrix = new Matrix();
        if(source.getWidth() > width){
            float scale  = ((float) width)  / source.getWidth();
            matrix.postScale(scale, scale);
        }
        matrix.postRotate(angle);

        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,true);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        return encodeToString(byteArray.toByteArray(), Base64.NO_WRAP);
    }
}
