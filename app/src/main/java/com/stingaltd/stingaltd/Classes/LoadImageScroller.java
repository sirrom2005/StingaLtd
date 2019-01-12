package com.stingaltd.stingaltd.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.FullScreenImage;
import com.stingaltd.stingaltd.JobItemActivity;
import com.stingaltd.stingaltd.Models.ImageData;
import com.stingaltd.stingaltd.R;

import java.io.File;
import java.io.IOException;

import static com.stingaltd.stingaltd.Common.Common.convertDpToPixel;

public class LoadImageScroller
{
    private Context c;
    private int WorkId;

    public LoadImageScroller(Context c, int WorkId){
        this.c = c;
        this.WorkId =  WorkId;
    }

    public void PrepareGalleryLabel(LinearLayout mPre_image_list, LinearLayout mPost_image_list, final String[] keys)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int)convertDpToPixel(120, c),
                (int)convertDpToPixel(160, c));
        layoutParams.setMargins((int)convertDpToPixel(0,  c), 0, (int)convertDpToPixel(7,  c), 0);

        mPre_image_list.removeAllViews();
        mPost_image_list.removeAllViews();

        for(String key : keys) {
            View placeHolder_pre  = View.inflate(c, R.layout.gallery_place_holder, null);
            View placeHolder_post = View.inflate(c, R.layout.gallery_place_holder, null);
            placeHolder_pre.setLayoutParams(layoutParams);
            placeHolder_post.setLayoutParams(layoutParams);

            TextView label_pre = placeHolder_pre.findViewById(R.id.label);
            TextView label_post = placeHolder_post.findViewById(R.id.label);

            label_pre.setText(key);
            label_post.setText(key);

            mPre_image_list.addView(placeHolder_pre);
            mPost_image_list.addView(placeHolder_post);
        }
    }

    public void LoadImage(final View v, final String img, final boolean ShowProgress, final String PhotoType)
    {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Bitmap> Task = new AsyncTask<Void, Void, Bitmap>()
        {
            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = v.findViewById(R.id.progress_bar);
                if(ShowProgress) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                File dir = new File(c.getFilesDir(), String.valueOf(WorkId));
                String FilePath = "/img/" + img;
                File path = new File(dir, FilePath);
                ImageData obj;
                Bitmap decodedByte = null;
                if(path.exists())
                {
                    try {
                        obj = (ImageData) Common.readObjectFromFile(c, WorkId+FilePath);
                        byte[] decodedString = Base64.decode(obj.getThumb(), Base64.NO_WRAP);
                        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    } catch (IOException | ClassNotFoundException ex) {
                        Log.e(Common.LOG_TAG, ex.getMessage());
                    }
                }
                return decodedByte;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null) {
                    ImageView photo    = v.findViewById(R.id.photo);
                    ImageView add_icon = v.findViewById(R.id.add_icon);
                    ImageView add_btn  = v.findViewById(R.id.add_btn);

                    add_icon.setVisibility(View.GONE);
                    photo.setImageBitmap(bitmap);
                    if(PhotoType.equals(Common.IMG_PRE)){
                        JobItemActivity.preImage.add(img);
                    }else{
                        JobItemActivity.postImage.add(img);
                    }

                    Log.d(Common.LOG_TAG, JobItemActivity.preImage.size() + "  " + JobItemActivity.postImage.size() );

                    add_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(c, FullScreenImage.class);
                            intent.putExtra(Common.SELECTED_IMG_FILE,img);
                            intent.putExtra(Common.WORK_ID_INTENT,WorkId);
                            c.startActivity(intent);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                }
            }
        };

        Task.execute();
    }
}
