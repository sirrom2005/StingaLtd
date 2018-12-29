package com.stingaltd.stingaltd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stingaltd.stingaltd.JobItemActivity;
import com.stingaltd.stingaltd.Models.JobItem;
import com.stingaltd.stingaltd.R;

import java.util.List;

import static com.stingaltd.stingaltd.Common.Common.JOB_ITEM;


public class JobItemAdapter extends RecyclerView.Adapter<JobItemAdapter.ViewHolder> {
    private static final int DATA_VIEW  = 1;
    private static final int EMPTY_VIEW = 2;
    private Context mContext;
    private LayoutInflater inflater;
    private List<JobItem> data;

    public JobItemAdapter(Context context) {
        mContext = context;
        if(inflater == null){inflater = LayoutInflater.from(mContext);}
    }

    public void loadData(List<JobItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;

        view = inflater.inflate((viewType==DATA_VIEW) ? R.layout.task_layout : R.layout.no_record_layout, parent, false);
        viewHolder = new ViewHolder(view, viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder vHolder, int position) {
        if(getItemViewType(position) == DATA_VIEW) {
            final JobItem item = data.get(position);
            vHolder.vJobType.setText(item.getJob_type());
            vHolder.vJobId.setText(String.format("%s%s", mContext.getString(R.string.work_item), item.getJob_id()));
            vHolder.vJobItem.setText(item.getTitle());
            vHolder.vAssignDate.setText(item.getStartDate());
            vHolder.vCustomer.setText(Html.fromHtml(item.getCustomer()));

            vHolder.vHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, JobItemActivity.class);
                    intent.putExtra(JOB_ITEM, item);
                    mContext.startActivity(intent);
                }
            });
        }else{
            vHolder.vErrorCode.setText(R.string.app_name);
            vHolder.vErrorDescription.setText(R.string.no_record);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data==null? EMPTY_VIEW : DATA_VIEW;
    }

    @Override
    public int getItemCount() {
        return data==null? 1 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView    vJobType,
                    vJobId,
                    vJobItem,
                    vAssignDate,
                    vCustomer,
                    vErrorCode,
                    vErrorDescription;
        RelativeLayout vHolder;

        private ViewHolder(View v, int viewType){
            super(v);
            if(viewType==DATA_VIEW) {
                vJobType = v.findViewById(R.id.job_type);
                vJobId = v.findViewById(R.id.job_id);
                vJobItem = v.findViewById(R.id.job_item);
                vAssignDate = v.findViewById(R.id.assign_date);
                vCustomer = v.findViewById(R.id.customer);
                vHolder = v.findViewById(R.id.holder);
            }else{
                vErrorCode = v.findViewById(R.id.err_code);
                vErrorDescription = v.findViewById(R.id.err_description);
            }
        }
    }
}
