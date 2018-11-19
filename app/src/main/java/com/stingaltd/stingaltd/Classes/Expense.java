package com.stingaltd.stingaltd.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Expense
{
    public static void UpdateExpense(final Context c, final LinearLayout contentArea, final int workId)
    {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... voids) {
                final JSONObject jsonObject = new JSONObject();
                try {
                    for (int i = 0; i < contentArea.getChildCount(); i++) {
                        TextView key = contentArea.getChildAt(i).findViewById(R.id.expense_id);
                        TextView val = contentArea.getChildAt(i).findViewById(R.id.amount);
                        jsonObject.put(key.getText().toString(), val.getText().toString());
                    }

                    if(Common.isInternetAvailable()){
                        Data.PostData(jsonObject.toString(), workId, "update_expense");
                    }else{
                        Data.SaveData(c, jsonObject.toString(), workId, "expense.json");
                    }
                }
                catch (JSONException ex){
                    Log.e(Common.LOG_TAG, "JsonEx " + ex.getMessage());
                }
                return null;
            }
        };

        task.execute();
    }
}
