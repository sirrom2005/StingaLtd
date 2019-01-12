package com.stingaltd.stingaltd.SyncData;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.stingaltd.stingaltd.Classes.DeletePhoto;
import com.stingaltd.stingaltd.Classes.UploadImage;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.ImageData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncData {
    public static void UploadPhoto(Context c, int WorkId)
    {
        if(!Common.isInternetAvailable()){ return; }
        File dir = new File(c.getFilesDir(),String.valueOf(WorkId));
        File imgDir = new File(dir,"img");
        if(imgDir.list()==null){ return; }
        int sleep = (imgDir.list().length>1)? 300 : 0;

        Log.e(Common.LOG_TAG, String.format(Locale.US, "/%d/img/", WorkId));

        if(imgDir.exists()){
            for(String FileName : imgDir.list())
            {
                if(!FileName.contains(Common.DEL_IMG_NAME_PATTERN))
                {
                    Log.e(Common.LOG_TAG, String.format(Locale.US, "/%d/img/%s", WorkId, FileName));
                    if(new UploadImage(c).Upload(String.format(Locale.US, "/%d/img/%s", WorkId, FileName))) {
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void DeletePhoto(Context c, int WorkId)
    {
        if(!Common.isInternetAvailable()){ return; }
        File dir = new File(c.getFilesDir(),String.valueOf(WorkId));
        File imgDir = new File(dir,"img");
        if(imgDir.list()==null){ return; }
        String DateCreated = "";
        List<File> fileList =  new ArrayList<>();

        for(File _file : imgDir.listFiles()) {
            if(_file.getName().contains(Common.DEL_IMG_NAME_PATTERN)){
                Log.e(Common.LOG_TAG, _file.getName() + "<<<");
                fileList.add(_file);
                try {
                    ImageData obj = (ImageData) Common.readObjectFromFile(c, WorkId + "/img/" + _file.getName());
                    DateCreated = DateCreated.concat(String.format("%s ", obj.getDateCreated().trim()));
                } catch (IOException | ClassNotFoundException ex) {
                    Log.e(Common.LOG_TAG, String.format("%s => %s", "DeletePhoto", ex.getMessage()));
                }
            }
        }

        if(DateCreated.length() > 10){
            if(new UploadImage(c).DeletePhoto(WorkId, DateCreated)){
                for(File f : fileList) {
                    if(f.delete()){
                        Log.e(Common.LOG_TAG, String.format("%s => %s", "File Removed", f.getName()));
                    }
                }
            }

            /*ImageData img = new ImageData(WorkId, DateCreated, null, null,null,0,null, null);

            new DeletePhoto().Post(img, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(Common.LOG_TAG, "HERER =>> " + response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });*/
        }
    }
}
