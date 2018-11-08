package com.stingaltd.stingaltd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class FullScreenImage extends AppCompatActivity {
    private static int WorkId;
    private static ArrayList mImageList  = new ArrayList();
    private static int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen_image_activity);
        final ViewPager pager = findViewById(R.id.pager);
        final TextView pagination = findViewById(R.id.pagination);
        FloatingActionButton fab = findViewById(R.id.fab);


        Intent intent = getIntent();
        String file = intent.getStringExtra(Common.SELECTED_IMG_FILE);
        WorkId = intent.getIntExtra(Common.WORK_ID_INTENT, 0);

        getImageData(WorkId);
        mPosition = mImageList.indexOf(file);

        final MyPagerAdapter MyPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(MyPagerAdapter);
        pager.setOffscreenPageLimit(mImageList.size());
        pager.setCurrentItem(mPosition);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

            @Override
            public void onPageSelected(int position){
                pagination.setText(String.format(Locale.US, "| %d of %d |",(position + 1), mImageList.size()));
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state){}
        });

        pagination.setText(String.format(Locale.US, "| %d of %d |", mPosition+1, mImageList.size()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.ConfirmMsg(FullScreenImage.this, getString(R.string.remove_photo));

                final Button action = Common.confirmation.findViewById(R.id.action);
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteImage(mImageList.get(mPosition).toString());
                        MyPagerAdapter.notifyDataSetChanged();
                        pagination.setText(String.format(Locale.US, "| %d of %d |",(pager.getCurrentItem()), mImageList.size()));
                        Common.alert.dismiss();
                    }
                });

                final Button cancel = Common.confirmation.findViewById(R.id.cancel);
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
        File dir = new File(getFilesDir(), String.valueOf(WorkId));
        File path = new File(dir, file);
        if(path.exists()){
            if(path.delete()){
                mImageList.remove(file);
            }
        }
    }

    private void getImageData(int WorkId) {
        mImageList.clear();
        File dir = new File(getFilesDir(), String.valueOf(WorkId));
        for(File file : dir.listFiles()) {
            mImageList.add(file.getName());
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FullScreenImageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }
    }

    public static class FullScreenImageFragment extends Fragment
    {
        private Context c;
        private int mPos;
        public static Fragment newInstance(int pos) {
            FullScreenImageFragment f = new FullScreenImageFragment();
            Bundle args = new Bundle();
            args.putInt("ID",pos);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            c = getContext();
            Bundle bundle = getArguments();
            mPos = bundle.getInt("ID",0);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.full_screen_image_fragment, container, false);
            ImageView imageView = rootView.findViewById(R.id.imageView);
            TextView label = rootView.findViewById(R.id.label);


            try {
                ImageData obj = (ImageData) Common.readObjectFromFile(c, WorkId + "/" + mImageList.get(mPos));
                byte[] decodedString = Base64.decode(obj.getLargeImage(), Base64.NO_WRAP);
                Bitmap img = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(img);
                label.setText(obj.getLabel());
            } catch (IOException | ClassNotFoundException ex) {
                Log.e(Common.LOG_TAG, ex.getMessage());
            }

            return rootView;
        }
    }
}
