package com.stingaltd.stingaltd.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.util.Base64.encodeToString;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;

public class PhotoProcessor extends AsyncTask<Void, Void, Boolean>
{
    private WeakReference<Context> WeakContext;
    private int WorkId;
    private int JobPos;
    private String ImagePath;
    private String PhotoType;
    private String JobType;

    public PhotoProcessor(Context c, String imagePath, int workId, String photoType, int jobPos, String jobType) {
        this.WeakContext= new WeakReference<>(c);
        this.ImagePath  = imagePath;
        this.WorkId     = workId;
        this.PhotoType  = photoType;
        this.JobPos     = jobPos;
        this.JobType    = jobType;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Context c = WeakContext.get();
        try {
            float angle         = GetImageRotation(ImagePath);
            Bitmap bitmap       = BitmapFactory.decodeFile(ImagePath);
            String LargeImage   = GetImage(bitmap, angle, 2000);
            String Thumb        = GetImage(bitmap, angle, 200);
            String FilePath     = String.format("/%s/img/%s%s.json",WorkId, JobPos, PhotoType);

            File dir = new File(c.getFilesDir(),String.valueOf(WorkId));
            if(!dir.exists()){
                if(dir.mkdir()){
                    Log.d(Common.LOG_TAG, String.format("%s >> Directory created %s", "PhotoProcessor", dir));
                }
            }

            File imgDir = new File(dir,"img");
            if(!imgDir.exists()){
                if(imgDir.mkdir()){
                    Log.d(Common.LOG_TAG, String.format("%s >> Directory created %s", "PhotoProcessor", imgDir));
                }
            }

            //get list of job type from account just type list
            String PhotoLabel = Objects.requireNonNull(Common.getAccount(c).getGalleryLable().get(JobType))[JobPos];

            String DateCreated = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());

            ImageData imageData = new ImageData(WorkId, DateCreated, PhotoType, Common.getLocation(c), PhotoLabel,0,Thumb,LargeImage);
            Common.SaveObjectAsFile(c, imageData, FilePath);
            File f = new File(ImagePath);
            if(f.delete()){
                //Delete photo image
                Log.d(Common.LOG_TAG, String.format("%s >> File deleted %s", "PhotoProcessor", f.toString()));
            }
        }catch (IOException | NullPointerException ex)
        {
            Log.e(Common.LOG_TAG, ex.getMessage());
            return false;
        }
        return true;
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
