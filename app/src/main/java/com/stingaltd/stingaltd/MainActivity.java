package com.stingaltd.stingaltd;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stingaltd.stingaltd.Adapter.JobItemAdapter;
import com.stingaltd.stingaltd.Apis._JobItem;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.Account;

import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;

public class MainActivity extends AppCompatActivity
{
    private static Account mUser;
    private static JobItemAdapter jobItemAdapter;

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

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    static class ExecApi extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... voids) {
            new _JobItem().start(mUser.getTechnicianId(), jobItemAdapter);
            return null;
        }
    }

    private void exitApp(){
        System.exit(0);
    }
}
