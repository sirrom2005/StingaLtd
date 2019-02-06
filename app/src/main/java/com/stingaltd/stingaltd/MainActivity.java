package com.stingaltd.stingaltd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stingaltd.stingaltd.Adapter.JobItemAdapter;
import com.stingaltd.stingaltd.Apis.GetJobList;
import com.stingaltd.stingaltd.Classes.UpdateApp;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.Account;
import com.stingaltd.stingaltd.Models.JobItem;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;

public class MainActivity extends AppCompatActivity
{
    private static Account mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView vLable = findViewById(R.id.label);
        vLable.setText(R.string.job_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUser = Common.getAccount(getApplicationContext());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, _Fragment.newInstance())
                    .commit();
        }
    }

    /**
     * Fragment
     * */
    public static class _Fragment extends Fragment {
        private Activity mActivity;

        public static _Fragment newInstance() {
            return new _Fragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.recycler_view, container, false);
            RecyclerView recyclerView = viewRoot.findViewById(R.id.recycler_view);

            new AsyncTask<Void, Void, Boolean>()
            {
                List<JobItem> obj;
                GetJobList getJobList = new GetJobList(){
                    @Override
                    public void onResponse(Call<List<JobItem>> call, Response<List<JobItem>> response) {
                        super.onResponse(call, response);
                        try {
                            obj = response.body();
                            Common.SaveObjectAsFile(getContext(), obj, Common.getUserJobsFileName(getContext()));
                            loadAdapter();
                        } catch (IOException ex) {
                            Log.e(LOG_TAG, "Error>> "+ex.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<JobItem>> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                };

                @Override
                protected Boolean doInBackground(Void... voids) {
                    if(Common.isInternetAvailable()) {
                        getJobList.start(mUser.getTechnicianId());
                        return true;
                    }else{
                        obj = (List<JobItem>) Common.readObjectFromFile(getContext(), Common.getUserJobsFileName(getContext()));
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if(!aBoolean) {
                        loadAdapter();
                    }
                }

                private void loadAdapter() {
                    JobItemAdapter jobItemAdapter = new JobItemAdapter(mActivity);
                    recyclerView.setAdapter(jobItemAdapter);
                    jobItemAdapter.loadData(obj);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                }
            }.execute();

            return viewRoot;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_update) {
            //SyncAdapter.syncImmediately(getApplicationContext(), mWorkId);
            new UpdateApp(getBaseContext());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Common.ConfirmMsg(this, getString(R.string.exit_app));

        final Button action = Common.confirmation.get().findViewById(R.id.action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
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

    private void exitApp(){
        System.exit(0);
    }
}
