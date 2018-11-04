package com.stingaltd.stingaltd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.stingaltd.stingaltd.Adapter.JobItemAdapter;
import com.stingaltd.stingaltd.Apis._JobItem;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.Account;
import com.stingaltd.stingaltd.Models.JobItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.stingaltd.stingaltd.Common.Common.JOB_ITEM;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;

public class MainActivity extends AppCompatActivity
{
    private static Account user;
    private static JobItemAdapter jobItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            user = readUserObjectFile();
        } catch (IOException | ClassNotFoundException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, _Fragment.newInstance())
                    .commit();
        }
    }

    private Account readUserObjectFile() throws IOException, ClassNotFoundException {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_email), Context.MODE_PRIVATE);
        String email = sharedPref.getString(getString(R.string.preference_email_key), "");

        FileInputStream fis = openFileInput(Common.getFileNameFromEmail(email));
        ObjectInputStream ois = new ObjectInputStream(fis);
        return (Account) ois.readObject();
    }

    /**
     * Fragment
     * */
    public static class _Fragment extends Fragment {
        private Activity mActivity;
        private Context mContext;

        public static _Fragment newInstance() {
            return new _Fragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.recycler_view, container, false);
            RecyclerView recyclerView = viewRoot.findViewById(R.id.recycler_view);

            jobItemAdapter = new JobItemAdapter(mActivity);

            recyclerView.setAdapter(jobItemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

            Log.e(LOG_TAG, "HERE");
            new ExecApi().execute();
            return viewRoot;
        }
    }

    @Override
    public void onBackPressed() {
        Common.ConfirmMsg(this, getString(R.string.exit_app));

        final Button action = Common.confirmation.findViewById(R.id.action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
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

    static class ExecApi extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... voids) {
            new _JobItem().start(user.technician_id, jobItemAdapter);
            return null;
        }
    }

    private void exitApp(){
        System.exit(0);
    }
}
