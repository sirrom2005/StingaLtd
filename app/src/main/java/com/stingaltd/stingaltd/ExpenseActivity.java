package com.stingaltd.stingaltd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stingaltd.stingaltd.Classes.Expense;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.JobScheduler.Util;
import com.stingaltd.stingaltd.Models.ExpenseAmount;
import com.stingaltd.stingaltd.Models.Expenses;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stingaltd.stingaltd.Common.Common.EXPENSE_AMOUNT_ITEM;
import static com.stingaltd.stingaltd.Common.Common.INVENTORY_ITEM;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
import static com.stingaltd.stingaltd.Common.Common.WORK_ID_INTENT;

public class ExpenseActivity extends AppCompatActivity
{
    private static List<Expenses> mExpenses;
    private static List<ExpenseAmount> mExpensesAmount;
    private static Map<Integer, Double> mExpensesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView vLable = findViewById(R.id.label);
        vLable.setText(R.string.expense);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //if(getSupportActionBar()!=null) {
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //}

        List<ExpenseAmount> expensesAmount = (List<ExpenseAmount>) getIntent().getSerializableExtra(EXPENSE_AMOUNT_ITEM);
        int mWorkId = getIntent().getIntExtra(WORK_ID_INTENT,0);
        mExpenses = Common.getAccount(getApplicationContext()).getExpenses();

        for (ExpenseAmount a : expensesAmount) {
            mExpensesMap.put(a.getId(), a.getAmount());
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, _Fragment.newInstance(mWorkId))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Common.LOG_TAG,  "onResume >> ");
    }

    /**
     * Fragment
     * */
    public static class _Fragment extends Fragment {
        private Activity mActivity;
        private static int WorkId;

        public static _Fragment newInstance(int id) {
            WorkId = id;
            return new _Fragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.fab_button_form, container, false);
            final LinearLayout contentArea = viewRoot.findViewById(R.id.content);
            FloatingActionButton fab = viewRoot.findViewById(R.id.fab);

            LayoutInflater inflat = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Log.d(LOG_TAG, " >> " + mExpenses.size());
            for(int i =0; i<mExpenses.size(); i++)
            {
                Expenses item = mExpenses.get(i);
                View layout = inflat.inflate(R.layout.expense_item_layout, null);
                TextView vName = layout.findViewById(R.id.name);
                TextView vAmount = layout.findViewById(R.id.amount);
                TextView vExpenseId = layout.findViewById(R.id.expense_id);

                double val = (mExpensesMap.get(item.getId())==null)? 0 : mExpensesMap.get(item.getId());

                vName.setText(item.getName());
                vAmount.setText(String.valueOf(val));
                vExpenseId.setText(String.valueOf(item.getId()));

                contentArea.addView(layout);
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        final JSONObject jsonObject = new JSONObject();
                        for (int i = 0; i < contentArea.getChildCount(); i++) {
                            TextView key = contentArea.getChildAt(i).findViewById(R.id.expense_id);
                            TextView val = contentArea.getChildAt(i).findViewById(R.id.amount);
                            jsonObject.put(key.getText().toString(), val.getText().toString());
                        }

                        Util.AddJsonDataScheduleJob(getContext(), jsonObject.toString(), WorkId, "update_expense");
                    }
                    catch(JSONException ex){
                        Log.e(Common.LOG_TAG, "JsonEx " + ex.getMessage());
                    }
                }
            });

            return viewRoot;
        }
    }
}
