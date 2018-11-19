package com.stingaltd.stingaltd.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.stingaltd.stingaltd.Models.Expenses;
import com.stingaltd.stingaltd.R;

import java.util.List;


public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private static final int DATA_VIEW  = 1;
    private static final int EMPTY_VIEW = 2;
    private LayoutInflater inflater;
    private List<Expenses> data;

    public ExpenseAdapter(Context context) {
        if(inflater == null){inflater = LayoutInflater.from(context);}
    }

    public void loadData(List<Expenses> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;

        view = inflater.inflate((viewType==DATA_VIEW) ? R.layout.expense_item_layout : R.layout.no_record_layout , parent, false);
        viewHolder = new ViewHolder(view, viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder vHolder, int position) {
        if (getItemViewType(position)==DATA_VIEW) {
            final Expenses item = data.get(position);
            vHolder.vName.setText(item.getName());
            vHolder.vAmount.setText("0.00");
        }
        else{
            vHolder.vErrorCode.setText(R.string.app_name);
            vHolder.vErrorDescription.setText(R.string.no_record);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.size()==0? EMPTY_VIEW : DATA_VIEW;
    }

    @Override
    public int getItemCount() {
        return data.size()==0? 1 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView vName,
                vErrorCode,
                vErrorDescription;
        EditText vAmount;

        private ViewHolder(View v, int viewType){
            super(v);
            if(viewType==DATA_VIEW) {
                vAmount = v.findViewById(R.id.amount);
                vName = v.findViewById(R.id.name);
            }else{
                vErrorCode = v.findViewById(R.id.err_code);
                vErrorDescription = v.findViewById(R.id.err_description);
            }
        }
    }
}
