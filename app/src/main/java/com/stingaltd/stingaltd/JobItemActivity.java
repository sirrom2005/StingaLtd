package com.stingaltd.stingaltd;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stingaltd.stingaltd.Classes.PhotoProcessor;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.Account;
import com.stingaltd.stingaltd.Models.JobItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.stingaltd.stingaltd.Common.Common.JOB_ITEM;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
import static com.stingaltd.stingaltd.Common.Common.convertDpToPixel;

public class JobItemActivity extends AppCompatActivity
{
    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2001;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA  = 2000;

    private LinearLayout    mPre_image_list,
                            mPost_image_list;
    PhotoProcessor photoProcessor = new PhotoProcessor();
    private String mCurrentPhotoPath;
    private String mPhotoType;
    private int WordId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPre_image_list    = findViewById(R.id.pre_image_list);
        mPost_image_list   = findViewById(R.id.post_image_list);
        //Button btn_pre_img = findViewById(R.id.btn_pre_img);
        //Button btn_post_img= findViewById(R.id.btn_post_img);

        TextView vJobType       = findViewById(R.id.job_type);
        TextView vJobId         = findViewById(R.id.job_id);
        TextView vJobItem       = findViewById(R.id.job_item);
        TextView vAssignDate    = findViewById(R.id.assign_date);
        TextView vCustomer      = findViewById(R.id.customer);
        Button vEmail           = findViewById(R.id.send_email);
        Button vMobile          = findViewById(R.id.make_call);

        JobItem mItem = (JobItem) getIntent().getSerializableExtra(JOB_ITEM);

        vJobType.setText(mItem.getJob_type());
        vJobId.setText(String.format("%s%s", getString(R.string.work_item), mItem.getJob_id()));
        vJobItem.setText(mItem.getTitle());
        vAssignDate.setText(mItem.getAssign_date());
        vCustomer.setText(Html.fromHtml(mItem.getCustomer()));
        vEmail.setVisibility(View.VISIBLE);
        vMobile.setVisibility(View.VISIBLE);

/*        btn_pre_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoType = IMG_PRE;
                launchCamera();
            }
        });

        btn_post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoType = IMG_POST;
                launchCamera();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_email), Context.MODE_PRIVATE);
        String email = sharedPref.getString(getString(R.string.preference_email_key), "");

        Account obj = null;
        try {
            obj = Common.readObjectFromFile(this, Common.getFileNameFromEmail(email));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int)convertDpToPixel(120,  this),
                (int)convertDpToPixel(160, this));
        layoutParams.setMargins((int)convertDpToPixel(0,  this), 0, (int)convertDpToPixel(7,  this), 0);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        final String[] keys = obj.getGalleryLable().get("Electric Fence Installation");

        for(int i =0; i<keys.length; i++)
        {
            View placeHolder_pre = inflater.inflate(R.layout.gallery_place_holder, null);
            View placeHolder_post = inflater.inflate(R.layout.gallery_place_holder, null);
            placeHolder_pre.setLayoutParams(layoutParams);
            placeHolder_post.setLayoutParams(layoutParams);

            TextView label_pre  = placeHolder_pre.findViewById(R.id.label);
            TextView label_post = placeHolder_post.findViewById(R.id.label);

            ImageView add_btn_pre  = placeHolder_pre.findViewById(R.id.add_btn);
            ImageView add_btn_post = placeHolder_post.findViewById(R.id.add_btn);

            label_pre.setText(keys[i]);
            label_post.setText(keys[i]);

            final int finalI = i;
            add_btn_pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "T " + keys[finalI], Toast.LENGTH_SHORT).show();
                }
            });

            add_btn_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "B " + keys[finalI], Toast.LENGTH_SHORT).show();
                }
            });

            mPre_image_list.addView(placeHolder_pre);
            mPost_image_list.addView(placeHolder_post);
        }

        //new LoadImageScroller(mPre_image_list, IMG_PRE,this, WordId).execute();
        //new LoadImageScroller(mPost_image_list, IMG_POST,this, WordId).execute();
    }

    private void launchCamera()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
        {
            if( ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }else{
                ActivityCompat.requestPermissions( this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }else{
             ActivityCompat.requestPermissions( this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            photoFile = photoProcessor.createTmpImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                try {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            getResources().getString(R.string.file_provider),
                            photoFile);
                    //Add permission to intent resolver
                    List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    //Save the image
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }catch (IllegalArgumentException ex){
                    Toast.makeText(this, R.string.camera_failed, Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = null;
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if(photoProcessor.MoveFile(getBaseContext(), mCurrentPhotoPath, WordId, mPhotoType))
            {
                Toast.makeText(this,R.string.photo_error_local_folder,Toast.LENGTH_SHORT);
            }
        }
    }
}
