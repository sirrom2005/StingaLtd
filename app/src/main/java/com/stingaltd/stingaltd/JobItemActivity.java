package com.stingaltd.stingaltd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stingaltd.stingaltd.Classes.LoadImageScroller;
import com.stingaltd.stingaltd.Classes.PhotoProcessor;
import com.stingaltd.stingaltd.Classes.UpdateApp;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.JobScheduler.Util;
import com.stingaltd.stingaltd.Models.JobItem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.stingaltd.stingaltd.Common.Common.EXPENSE_AMOUNT_ITEM;
import static com.stingaltd.stingaltd.Common.Common.IMG_POST;
import static com.stingaltd.stingaltd.Common.Common.IMG_PRE;
import static com.stingaltd.stingaltd.Common.Common.INVENTORY_ITEM;
import static com.stingaltd.stingaltd.Common.Common.JOB_ITEM;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
import static com.stingaltd.stingaltd.Common.Common.RODREADING_ITEM;
import static com.stingaltd.stingaltd.Common.Common.TEMP_PHOTO_PATH;
import static com.stingaltd.stingaltd.Common.Common.WORK_ID_INTENT;

public class JobItemActivity extends AppCompatActivity
{
    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2001;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA  = 2000;
    private static final int MY_PERMISSIONS_REQUEST_DEVICE_LOCATION = 2020;

    private LinearLayout    mPre_image_list,
                            mPost_image_list;
    private LoadImageScroller mLoadImageScroller;
    private String mCurrentPhotoPath;
    private String mPhotoType;
    private int mWorkId;
    private int mJobPos;
    private String mJobType;
    private String[] mLabels;
    public static List<String> preImage;
    public static List<String> postImage;

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
        Button submit           = findViewById(R.id.submit);

        RelativeLayout vHolder  = findViewById(R.id.holder);
        TextView vJobType       = findViewById(R.id.job_type);
        TextView vJobId         = findViewById(R.id.job_id);
        TextView vJobItem       = findViewById(R.id.job_item);
        TextView vAssignDate    = findViewById(R.id.assign_date);
        TextView vCustomer      = findViewById(R.id.customer);
        Button vInventory       = findViewById(R.id.inventory);
        Button vExpense         = findViewById(R.id.expense);
        Button vRodReading      = findViewById(R.id.rod_reading);


        int id = getIntent().getIntExtra(JOB_ITEM, 0);
        final JobItem WorkItem = Common.FilterJobById(getBaseContext(), id);
        mWorkId  = WorkItem.getId();
        mJobType = WorkItem.getJob_type();
        mLabels  = Common.getAccount(getApplicationContext()).getGalleryLable().get(mJobType);

        vJobType.setText(WorkItem.getJob_type());
        vJobId.setText(String.format("%s%s", getString(R.string.work_item), WorkItem.getJob_id()));
        vJobItem.setText(WorkItem.getTitle());
        vAssignDate.setText(WorkItem.getStartDate());
        vCustomer.setText(Html.fromHtml(WorkItem.getCustomer()));
        vHolder.setBackground(getDrawable(  (WorkItem.getComplete()==1)?
                                            R.drawable.task_background_close:
                                            R.drawable.task_background_open));

        vInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobItemActivity.this, InventoryActivity.class);
                intent.putExtra(WORK_ID_INTENT, mWorkId);
                intent.putExtra(INVENTORY_ITEM, (Serializable) WorkItem.getInventory());
                startActivity(intent);
            }
        });

        vRodReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobItemActivity.this, RodReadActivity.class);
                intent.putExtra(WORK_ID_INTENT, mWorkId);
                intent.putExtra(RODREADING_ITEM, (Serializable) WorkItem.getRodReading());
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Context c = getBaseContext();
                if(preImage.size()==mLabels.length && postImage.size()==mLabels.length)
                {
                    View _view = View.inflate(c, R.layout.technicion_note_layout, null);

                    final TextView comment = _view.findViewById(R.id.job_comment);
                    Button action = _view.findViewById(R.id.action);
                    Button cancel = _view.findViewById(R.id.cancel);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(JobItemActivity.this);
                    final AlertDialog alert = dialog.create();
                    alert.setView(_view);
                    alert.setCancelable(false);
                    alert.show();

                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.MessageBox(JobItemActivity.this, getString(R.string.job_status_updated));
                            Button action = Common.confirmation.get().findViewById(R.id.action);
                            action.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Util.UpdateStatusScheduleJob(getBaseContext(), mWorkId, comment.getText().toString());
                                    Common.alert.dismiss();
                                }
                            });

                            alert.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });
                }else{
                    Common.MessageBox(JobItemActivity.this, getString(R.string.missing_job_phot));
                    Button action = Common.confirmation.get().findViewById(R.id.action);
                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.alert.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLoadImageScroller = new LoadImageScroller(getApplicationContext(), mWorkId);
        mLoadImageScroller.PrepareGalleryLabel(mPre_image_list, mPost_image_list, mLabels);
        LoadGalleryImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void LoadGalleryImages()
    {
        ImageView preBtn, postBtn;
        preImage = new ArrayList<>();
        postImage = new ArrayList<>();

        for(int i=0; i<mPre_image_list.getChildCount(); i++){
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

            mLoadImageScroller.LoadImage(mPre_image_list.getChildAt(i) , String.format("%s%s.json", i,Common.IMG_PRE) ,false, Common.IMG_PRE);
            mLoadImageScroller.LoadImage(mPost_image_list.getChildAt(i), String.format("%s%s.json", i,Common.IMG_POST),false, Common.IMG_POST);
        }
    }

    public void launchCamera()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
        {
            if( ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_DEVICE_LOCATION);
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
            File photoFile = createTmpImageFile();

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
        // If request is cancelled, the result arrays are empty.
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
            break;
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }
            break;
            case MY_PERMISSIONS_REQUEST_DEVICE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String Filename = String.format(Locale.US, "/%d/img/%s", mWorkId, String.format("%s%s.json",mJobPos,mPhotoType));
            //Save image as json data
            @SuppressLint("StaticFieldLeak")
            PhotoProcessor T = new PhotoProcessor(getBaseContext(), mCurrentPhotoPath, mWorkId, mPhotoType, mJobPos, mJobType){
                @Override
                protected void onPostExecute(Boolean aBool) {
                    super.onPostExecute(aBool);
                    if(aBool){
                        Util.PhotoUploadScheduleJob(getBaseContext(), Filename);
                    }else {
                        Log.e(Common.LOG_TAG, getResources().getString(R.string.photo_error_local_folder));
                    }
                }
            };
            T.execute();
        }
    }

    public File createTmpImageFile() {
        File storageDir = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_PATH);

        if(!storageDir.exists()){
            if(storageDir.mkdir()){
                Log.d(Common.LOG_TAG, String.format("Dir created %s", storageDir));
            }
        }

        File image;
        try {
            image = File.createTempFile(
                    "TMP_PHOTO",/* prefix */
                    ".jpg",       /* suffix */
                    storageDir           /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return image;
    }
}
