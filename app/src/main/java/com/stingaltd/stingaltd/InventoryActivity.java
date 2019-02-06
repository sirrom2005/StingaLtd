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
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stingaltd.stingaltd.JobScheduler.Util;
import com.stingaltd.stingaltd.Models.Inventory;
import com.stingaltd.stingaltd.Common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.stingaltd.stingaltd.Common.Common.INVENTORY_ITEM;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
import static com.stingaltd.stingaltd.Common.Common.WORK_ID_INTENT;

public class InventoryActivity extends AppCompatActivity
{
    private static List<com.stingaltd.stingaltd.Models.Inventory> mInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView vLable = findViewById(R.id.label);
        vLable.setText(R.string.inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //if(getSupportActionBar()!=null) {
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //}

        int mWorkId = getIntent().getIntExtra(WORK_ID_INTENT,0);
        mInventory = (List<com.stingaltd.stingaltd.Models.Inventory>) getIntent().getSerializableExtra(INVENTORY_ITEM);


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
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View viewRoot = inflater.inflate(R.layout.fab_button_form, container, false);
            final LinearLayout contentArea = viewRoot.findViewById(R.id.content);
            FloatingActionButton fab = viewRoot.findViewById(R.id.fab);

            LayoutInflater inflat = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Log.d(LOG_TAG, " >> " + mInventory.size());
            for(int i =0; i<mInventory.size(); i++)
            {
                Inventory item = mInventory.get(i);
                View layout = inflat.inflate(R.layout.inventory_item_layout, null);
                TextView vCode = layout.findViewById(R.id.code);
                TextView vDescription   = layout.findViewById(R.id.description);
                TextView vInventoryId   = layout.findViewById(R.id.inventory_id);
                TextView vAmountIssued  = layout.findViewById(R.id.amount_issued);
                TextView vAmountUsed    = layout.findViewById(R.id.amount_used);

                vCode.setText(item.getCode());
                vDescription.setText(item.getDescription());
                vInventoryId.setText(String.valueOf(item.getInventoryId()));
                vAmountIssued.setText(String.valueOf(item.getQuantity()));
                vAmountUsed.setText(String.valueOf(item.getQuantityUsed()));
                vAmountUsed.setFilters(new InputFilter[]{ new InputFilterMinMax(0, item.getQuantity())});

                contentArea.addView(layout);
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        final JSONObject jsonObject = new JSONObject();
                        for (int i = 0; i < contentArea.getChildCount(); i++) {
                            TextView key = contentArea.getChildAt(i).findViewById(R.id.inventory_id);
                            TextView val = contentArea.getChildAt(i).findViewById(R.id.amount_used);
                            jsonObject.put(key.getText().toString(), val.getText().toString());
                        }
                        Util.AddJsonDataScheduleJob(getContext(), jsonObject.toString(), WorkId, "update_inventory");
                    }
                    catch(JSONException ex){
                        Log.e(Common.LOG_TAG, "JsonEx " + ex.getMessage());
                    }
                }
            });

            return viewRoot;
        }


        private static class InputFilterMinMax implements InputFilter {
            private int min;
            private int max;

            public InputFilterMinMax(int min, int max) {
                this.min = min;
                this.max = max;
            }

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //noinspection EmptyCatchBlock
                try {
                    int input = Integer.parseInt(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()));
                    if (isInRange(min, max, input))
                        return null;
                } catch (NumberFormatException nfe) { }
                return "";
            }

            private boolean isInRange(int a, int b, int c) {
                return b > a ? c >= a && c <= b : c >= b && c <= a;
            }
        }
    }
}
