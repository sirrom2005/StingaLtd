package com.stingaltd.stingaltd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stingaltd.stingaltd.Classes.UploadImage;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class FullScreenImage extends AppCompatActivity {
    private static int WorkId;
    private ArrayList<String> mImageList  = new ArrayList<>();
    private static int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen_image_activity);
        final ViewPager pager = findViewById(R.id.pager);
        //final TextView pagination = findViewById(R.id.pagination);
        final FloatingActionButton fab = findViewById(R.id.fab);


        Intent intent = getIntent();
        String file = intent.getStringExtra(Common.SELECTED_IMG_FILE);
        WorkId = intent.getIntExtra(Common.WORK_ID_INTENT, 0);

        getImageData(WorkId);
        mPosition = mImageList.indexOf(file);

        final MyPagerAdapter MyPagerAdapter = new MyPagerAdapter(this);

        pager.setAdapter(MyPagerAdapter);
        pager.setOffscreenPageLimit(0);
        pager.setCurrentItem(mPosition);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

            @Override
            public void onPageSelected(int position){
                //pagination.setText(String.format(Locale.US, "| %d of %d |",(position + 1), mImageList.size()));
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state){}
        });
        //pagination.setText(String.format(Locale.US, "| %d of %d |", mPosition+1, mImageList.size()));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.ConfirmMsg(FullScreenImage.this, getString(R.string.remove_photo));

                final Button action = Common.confirmation.get().findViewById(R.id.action);
                action.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onClick(View v) {
                        //int loc = 0;
                        if(mImageList.size()>0)
                        {
                            String file = mImageList.get(mPosition);
                            deleteImage(file);

                            MyPagerAdapter.notifyDataSetChanged();
                            if(mImageList.size()>0){
                                //loc = pager.getCurrentItem() + 1;
                            }else {
                                fab.setVisibility(View.GONE);
                            }
                            //pagination.setText(String.format(Locale.US, "| %d of %d |", loc, mImageList.size()));
                        }
                        Common.alert.dismiss();
                    }
                });

                final Button cancel = Common.confirmation.get().findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Common.alert.dismiss();
                    }
                });
            }
        });
    }

    private void deleteImage(String file) {
        File dir = new File(getFilesDir(), String.valueOf(WorkId)+"/img");
        File _file = new File(dir, file);
        if(_file.exists()){
            if(_file.renameTo(new File(dir,String.format("%s%s", Common.DEL_IMG_NAME_PATTERN, System.currentTimeMillis())))){
                mImageList.remove(file);
            }
        }
    }

    private void getImageData(int WorkId) {
        mImageList.clear();
        File dir = new File(getFilesDir(), String.valueOf(WorkId)+"/img");
        for(File file : dir.listFiles()) {
            if(!file.getName().contains(Common.DEL_IMG_NAME_PATTERN)){
                mImageList.add(file.getName());
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        private Context c;
        private LayoutInflater layoutInflater;

        private MyPagerAdapter(Context c) {
            this.c = c;
            layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position)
        {
            View itemView = layoutInflater.inflate(R.layout.full_screen_image_fragment, container, false);
            ImageView imageView = itemView.findViewById(R.id.imageView);
            TextView label = itemView.findViewById(R.id.label);

            try {
                ImageData obj = (ImageData) Common.readObjectFromFile(c, WorkId + "/img/" + mImageList.get(position));
                byte[] decodedString = Base64.decode(obj.getLargeImage(), Base64.NO_WRAP);
                Bitmap img = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(img);
                label.setText(obj.getLabel());

            } catch (IOException | ClassNotFoundException ex) {
                Log.e(Common.LOG_TAG, ex.getMessage());
            }

            container.addView(itemView);
            return itemView;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((CoordinatorLayout) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object){
            return PagerAdapter.POSITION_NONE;
        }
    }
}
