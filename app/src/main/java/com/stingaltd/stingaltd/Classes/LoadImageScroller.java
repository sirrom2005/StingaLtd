package com.stingaltd.stingaltd.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
import static com.stingaltd.stingaltd.Common.Common.PHOTO_PATH;
import static com.stingaltd.stingaltd.Common.Common.convertDpToPixel;

public class LoadImageScroller extends AsyncTask<Void, ImageView, Void>
{
    private WeakReference<LinearLayout> weakLayout;
    private WeakReference<Context> weakContext;
    private String imgPath;
    private String WordId;
    private LinearLayout.LayoutParams layoutParams;

    public LoadImageScroller(LinearLayout l, String imgPath, Context c, int wordId) {
        this.weakLayout  = new WeakReference<>(l);
        this.weakContext = new WeakReference<>(c);
        this.imgPath = imgPath;
        this.WordId = String.valueOf(wordId);
        layoutParams = new LinearLayout.LayoutParams(
                (int)convertDpToPixel(80,  c),
                (int)convertDpToPixel(120, c));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        weakLayout.get().removeAllViews();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Context c = weakContext.get();
        File path = new File(new File(new File(c.getFilesDir(),PHOTO_PATH), String.valueOf(WordId)),imgPath);

        if (path.exists()) {
            for (File file : path.listFiles()) {
                System.out.println(file.toString());
                ImageView img = new ImageView(c);
                img.setLayoutParams(layoutParams);
                img.setImageBitmap(reSizeImage(file.getAbsolutePath(),120));
                publishProgress(img);
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(ImageView... values) {
        super.onProgressUpdate(values);
        if(values[0]!=null) {
            weakLayout.get().addView(values[0]);
        }
    }

    private Bitmap reSizeImage(String imagePath, int scaleSize) {
        float fraction;
        //Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int imgWidth = bmOptions.outWidth;
        int imgHeight = bmOptions.outHeight;
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        Log.d(LOG_TAG, "Width " + imgWidth);

        if(imgWidth > imgHeight){
            if(imgWidth>scaleSize){
                fraction = (float) scaleSize/imgWidth;
                int newHeight = (int) (imgHeight*fraction);
                bitmap = Bitmap.createScaledBitmap(bitmap,scaleSize,newHeight,false);
            }
        }else{
            if(imgHeight>scaleSize){
                fraction = (float) scaleSize/imgHeight;
                int newWidth = (int) (imgWidth*fraction);
                bitmap = Bitmap.createScaledBitmap(bitmap,newWidth, scaleSize,false);
            }
        }
        return bitmap;
    }
}
