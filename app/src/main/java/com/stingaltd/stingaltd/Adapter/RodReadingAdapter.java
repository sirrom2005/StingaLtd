package com.stingaltd.stingaltd.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.JobScheduler.Util;
import com.stingaltd.stingaltd.Models.Expenses;
import com.stingaltd.stingaltd.Models.JobItem;
import com.stingaltd.stingaltd.Models.RodReading;
import com.stingaltd.stingaltd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RodReadingAdapter extends RecyclerView.Adapter<RodReadingAdapter.ViewHolder> {
    private static final int DATA_VIEW  = 1;
    private static final int BTN_VIEW   = 2;
    private LayoutInflater inflater;
    private List<RodReading> data;
    private int mWorkId;

    Context c;

    public RodReadingAdapter(Context context, int WorkId) {
        c = context;
        mWorkId = WorkId;
        if(inflater == null){inflater = LayoutInflater.from(context);}
    }

    public void loadData(List<RodReading> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;

        View view  = inflater.inflate((viewType==DATA_VIEW) ? R.layout.rod_reading_layout : R.layout.rod_reading_button_layout, parent, false);
        viewHolder = new ViewHolder(view, viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder vHolder, int position) {
        final RodReading item = data.get(position);
        if(data.size()==position+1) {
            vHolder.vAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i=-1; i<=3; i++) {
                        data.add(data.size() - 1, new RodReading("Ground/Earth Rod readings " + (position + i), "0"));
                    }
                    notifyDataSetChanged();
                }
            });

            vHolder.vSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.ConfirmMsg(c, c.getString(R.string.are_you_sure));
                    final Button action = Common.confirmation.get().findViewById(R.id.action);
                    final Button cancel = Common.confirmation.get().findViewById(R.id.cancel);
                    //this is the worse need a work a round.
                    notifyDataSetChanged();
                    action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                final JSONObject jsonObject = new JSONObject();
                                for(int i=0; i<=3; i++) {
                                    data.get(i).setValue((data.get(i).getValue().trim() == "") ? "0" : data.get(i).getValue());
                                }
                                for (int i = 0; i < data.size(); i++) {
                                    if(data.get(i).getValue().trim()!="") {
                                        jsonObject.put(data.get(i).getKey(), data.get(i).getValue());
                                    }
                                }

                                List<JobItem> obj = (List<JobItem>) Common.readObjectFromFile(c, Common.getUserJobsFileName(c));
                                for(JobItem jobItem : obj) {
                                    if (jobItem.getId() == mWorkId) {
                                        jobItem.getRodReading().clear();
                                        jobItem.getRodReading().addAll(data);
                                        try {
                                            Common.SaveObjectAsFile(c, obj, Common.getUserJobsFileName(c));
                                        } catch (IOException ex) {
                                            Log.e(Common.LOG_TAG, ex.getMessage());
                                        }
                                    }
                                }

                                Common.alert.dismiss();
                                Util.AddJsonDataScheduleJob(c, jsonObject.toString(), mWorkId,"add_rod_reading");
                            }
                            catch(JSONException ex){
                                Log.e(Common.LOG_TAG, "JsonEx " + ex.getMessage());
                            }
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.alert.dismiss();
                        }
                    });
                }
            });
        }else
        {

            vHolder.vCode.setText((position>1) ? "Rod Reading " + (position - 1) : "Reading");
            vHolder.vName.setText(item.getKey());
            vHolder.vAmount.setText(item.getValue());
            vHolder.vAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        data.get(position).setValue(vHolder.vAmount.getText().toString());
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.size()==position+1? BTN_VIEW : DATA_VIEW;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView vName,
                 vCode;
        EditText vAmount;
        Button   vAdd,
                 vSave;

        private ViewHolder(View v, int viewType){
            super(v);
            if(viewType == DATA_VIEW) {
                vAmount = v.findViewById(R.id.amount);
                vName = v.findViewById(R.id.name);
                vCode = v.findViewById(R.id.code);
            }else{
                vAdd = v.findViewById(R.id.add);
                vSave = v.findViewById(R.id.save);
            }
        }
    }
}
