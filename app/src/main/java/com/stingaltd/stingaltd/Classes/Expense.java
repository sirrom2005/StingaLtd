package com.stingaltd.stingaltd.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... voids) {
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
                    return true;
                }
                catch (JSONException ex){
                    Log.e(Common.LOG_TAG, "JsonEx " + ex.getMessage());
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean bool) {
                super.onPostExecute(bool);
                if(bool){
                    Common.MessageBox(c, "Form submitted");

                    final Button action = Common.confirmation.get().findViewById(R.id.action);
                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.alert.dismiss();
                        }
                    });
                }
            }
        };

        task.execute();
    }
}
