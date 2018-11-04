package com.stingaltd.stingaltd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stingaltd.stingaltd.JobItemActivity;
import com.stingaltd.stingaltd.Models.JobItem;
import com.stingaltd.stingaltd.R;

import java.util.List;

import static com.stingaltd.stingaltd.Common.Common.JOB_ITEM;


public class JobItemAdapter extends RecyclerView.Adapter<JobItemAdapter.ViewHolder> {
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

        view = inflater.inflate(R.layout.task_layout, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder vHolder, int position) {
        final JobItem item = data.get(position);
        vHolder.vJobType.setText(item.getJob_type());
        vHolder.vJobId.setText(String.format("%s%s", mContext.getString(R.string.work_item), item.getJob_id()));
        vHolder.vJobItem.setText(item.getTitle());
        vHolder.vAssignDate.setText(item.getAssign_date());
        vHolder.vCustomer.setText(Html.fromHtml(item.getCustomer()));

        vHolder.vHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, JobItemActivity.class);
                intent.putExtra(JOB_ITEM, item);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView    vJobType,
                    vJobId,
                    vJobItem,
                    vAssignDate,
                    vCustomer;
        CardView    vHolder;

        private ViewHolder(View v){
            super(v);
            vJobType        = v.findViewById(R.id.job_type);
            vJobId          = v.findViewById(R.id.job_id);
            vJobItem        = v.findViewById(R.id.job_item);
            vAssignDate     = v.findViewById(R.id.assign_date);
            vCustomer       = v.findViewById(R.id.customer);
            vHolder         = v.findViewById(R.id.holder);
        }
    }
}
