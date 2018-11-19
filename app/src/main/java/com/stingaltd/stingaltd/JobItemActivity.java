package com.stingaltd.stingaltd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stingaltd.stingaltd.Classes.LoadImageScroller;
import com.stingaltd.stingaltd.Classes.PhotoProcessor;
import com.stingaltd.stingaltd.Classes.UploadImage;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.JobItem;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.stingaltd.stingaltd.Common.Common.EXPENSE_AMOUNT_ITEM;
import static com.stingaltd.stingaltd.Common.Common.IMG_POST;
import static com.stingaltd.stingaltd.Common.Common.IMG_PRE;
import static com.stingaltd.stingaltd.Common.Common.INVENTORY_ITEM;
import static com.stingaltd.stingaltd.Common.Common.JOB_ITEM;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
import static com.stingaltd.stingaltd.Common.Common.WORK_ID_INTENT;

public class JobItemActivity extends AppCompatActivity
{
    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2001;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA  = 2000;

    private LinearLayout    mPre_image_list,
                            mPost_image_list;
    private PhotoProcessor photoProcessor = new PhotoProcessor();
    private LoadImageScroller loadImageScroller;
    private String mCurrentPhotoPath;
    private String mPhotoType;
    private int mWorkId;
    private int mJobPos;
    private String mJobType;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_item);
        TextView vLable = findViewById(R.id.label);
        vLable.setText(R.string.job_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPre_image_list         = findViewById(R.id.pre_image_list);
        mPost_image_list        = findViewById(R.id.post_image_list);
        //Button submit           = findViewById(R.id.submit);

        TextView vJobType       = findViewById(R.id.job_type);
        TextView vJobId         = findViewById(R.id.job_id);
        TextView vJobItem       = findViewById(R.id.job_item);
        TextView vAssignDate    = findViewById(R.id.assign_date);
        TextView vCustomer      = findViewById(R.id.customer);
        Button vInventory       = findViewById(R.id.inventory);
        Button vExpense         = findViewById(R.id.expense);


        final JobItem WorkItem = (JobItem) getIntent().getSerializableExtra(JOB_ITEM);
        mWorkId  = WorkItem.getId();
        mJobType = WorkItem.getJob_type();

        vJobType.setText(WorkItem.getJob_type());
        vJobId.setText(String.format("%s%s", getString(R.string.work_item), WorkItem.getJob_id()));
        vJobItem.setText(WorkItem.getTitle());
        vAssignDate.setText(WorkItem.getAssign_date());
        vCustomer.setText(Html.fromHtml(WorkItem.getCustomer()));

        vInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobItemActivity.this, InventoryActivity.class);
                intent.putExtra(WORK_ID_INTENT, mWorkId);
                intent.putExtra(INVENTORY_ITEM, (Serializable) WorkItem.getInventory());
                startActivity(intent);
            }
        });

        vExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobItemActivity.this, ExpenseActivity.class);
                intent.putExtra(WORK_ID_INTENT, mWorkId);
                intent.putExtra(EXPENSE_AMOUNT_ITEM, (Serializable) WorkItem.getExpenseAmount());
                startActivity(intent);
            }
        });

        /*submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadImage(getApplicationContext()).Upload();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Common.LOG_TAG,  "onResume >> ");
        LoadGalleryLabels();
    }

    private void LoadGalleryLabels()
    {
        String[] labels = Common.getAccount(getApplicationContext()).getGalleryLable().get(mJobType);
        loadImageScroller = new LoadImageScroller(getApplicationContext(), mWorkId);
        loadImageScroller.PrepareGalleryLabel(mPre_image_list, mPost_image_list, labels);

        ImageView preBtn, postBtn;

        for(int i =0; i<mPre_image_list.getChildCount(); i++){
            preBtn  = mPre_image_list.getChildAt(i).findViewById(R.id.add_btn);
            postBtn = mPost_image_list.getChildAt(i).findViewById(R.id.add_btn);

            final int finalI = i;
            preBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhotoType  = IMG_PRE;
                    mJobPos     = finalI;
                    launchCamera();
                }
            });

            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhotoType  = IMG_POST;
                    mJobPos     = finalI;
                    launchCamera();
                }
            });

            loadImageScroller.LoadImage(mPre_image_list.getChildAt(i),  String.format("%s%s.json", i,Common.IMG_PRE), false);
            loadImageScroller.LoadImage(mPost_image_list.getChildAt(i), String.format("%s%s.json", i,Common.IMG_POST), false);
        }
    }

    public void launchCamera()
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
            File photoFile = photoProcessor.createTmpImageFile();

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
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoProcessor.SavePhotoJsonData(getBaseContext(), mCurrentPhotoPath, mWorkId, mPhotoType, mJobPos, mJobType);
            View v = mPhotoType.equals(Common.IMG_PRE) ? mPre_image_list.getChildAt(mJobPos) : mPost_image_list.getChildAt(mJobPos) ;
            loadImageScroller.LoadImage(v,  String.format("%s%s.json", mJobPos, mPhotoType), true);
        }
    }
}
