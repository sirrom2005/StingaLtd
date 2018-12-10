package com.stingaltd.stingaltd.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.FullScreenImage;
import com.stingaltd.stingaltd.MainActivity;
import com.stingaltd.stingaltd.Models.ImageData;
import com.stingaltd.stingaltd.R;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import static com.stingaltd.stingaltd.Common.Common.convertDpToPixel;

public class LoadImageScroller
{
    private WeakReference<LinearLayout> weakLayout;
    private WeakReference<Context> weakContext;
    private String imgPath;
    private String WordId;
    private LinearLayout.LayoutParams layoutParams;
    private Context c;
    private File dir;
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

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dir = new File(c.getFilesDir(), String.valueOf(WorkId));

        mPre_image_list.removeAllViews();
        mPost_image_list.removeAllViews();
        for(int i =0; i<keys.length; i++)
        {
            View placeHolder_pre = inflater.inflate(R.layout.gallery_place_holder, null);
            View placeHolder_post = inflater.inflate(R.layout.gallery_place_holder, null);
            placeHolder_pre.setLayoutParams(layoutParams);
            placeHolder_post.setLayoutParams(layoutParams);

            TextView label_pre  = placeHolder_pre.findViewById(R.id.label);
            TextView label_post = placeHolder_post.findViewById(R.id.label);

            label_pre.setText(keys[i]);
            label_post.setText(keys[i]);

            mPre_image_list.addView(placeHolder_pre);
            mPost_image_list.addView(placeHolder_post);
        }
    }

    public void LoadImage(final View v, final String img, final boolean ShowProgress)
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
                String FilePath = "/img/" + img;
                File path = new File(dir, FilePath);
                ImageData obj;
                Bitmap decodedByte = null;
                Log.e(Common.LOG_TAG, path.toString());
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

                    new UploadImage(c).Upload(String.format(Locale.US, "/%d/img/%s", WorkId, img));
                }
            }
        };

        Task.execute();
    }
}
