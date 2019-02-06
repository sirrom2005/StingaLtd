package com.stingaltd.stingaltd;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stingaltd.stingaltd.Adapter.RodReadingAdapter;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Models.RodReading;

import java.util.ArrayList;
import java.util.List;

import static com.stingaltd.stingaltd.Common.Common.RODREADING_ITEM;
import static com.stingaltd.stingaltd.Common.Common.WORK_ID_INTENT;

public class RodReadActivity extends AppCompatActivity {
    private static int mWorkId;
    private static List<RodReading> mRodReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rod_reading);
        TextView vLable = findViewById(R.id.label);
        vLable.setText(getString(R.string.rod_reading));

        mWorkId = getIntent().getIntExtra(WORK_ID_INTENT, 0);
        mRodReading = (List<RodReading>) getIntent().getSerializableExtra(RODREADING_ITEM);
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
        private List<RodReading> obj;

        public static _Fragment newInstance() {
            return new _Fragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            obj = new ArrayList<>();
            if(mRodReading.size()==0) {
                obj.add(new RodReading("System Reading", "0"));
                obj.add(new RodReading("Neutral Reading", "0"));
                obj.add(new RodReading("Ground/Earth Rod readings 1", "0"));
                obj.add(new RodReading("Ground/Earth Rod readings 2", "0"));
                obj.add(new RodReading("Ground/Earth Rod readings 3", "0"));
                //obj.add(new RodReading("Ground/Earth Rod readings 4", ""));
                //obj.add(new RodReading("Ground/Earth Rod readings 5", ""));
            }else{
                for(RodReading T : mRodReading){
                    obj.add(new RodReading(T.getKey(), T.getValue()));
                }
            }
            obj.add(new RodReading("", ""));//Add empty one to display button
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.recycler_view, container, false);
            RecyclerView recyclerView = viewRoot.findViewById(R.id.recycler_view);

            RodReadingAdapter rodReadingAdapter = new RodReadingAdapter(getContext(), mWorkId);

            recyclerView.setAdapter(rodReadingAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            rodReadingAdapter.loadData(obj);
            rodReadingAdapter.notifyDataSetChanged();

            return viewRoot;
        }
    }
}
