package com.aquarian.drivers.ui.receipts.receiptsList;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aquarian.drivers.R;
import com.aquarian.drivers.ui.receipts.Receipt;

import java.util.ArrayList;
import java.util.List;

public class ReceiptsListAdapter extends RecyclerView.Adapter<JobsHolder> {

    private final List<Receipt> receiptList;

    public ReceiptsListAdapter(ArrayList receipts) {
        receiptList = receipts;
    }

    @Override
    public JobsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View receiptView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jobslist, parent, false);

        return new JobsHolder(receiptView);

    }

    @Override
    public void onBindViewHolder(JobsHolder holder, int position) {
        holder.nReceiptId.setText("Receipt " + (position+1));
        holder.nReceiptType.setText("Reason: " + sanitize(receiptList.get(position).getReceiptType()));
        holder.nReceiptAmount.setText("Amount: £" + sanitize(receiptList.get(position).getReceiptAmount()));
        holder.index = "" + position;
    }

    @Override
    public int getItemCount() {
        return receiptList != null ? receiptList.size() : 0;
    }

    public String sanitize(String input){
        if (input.equals("null") || input==null){input="";}
        return input;
    }

}

class JobsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView nReceiptId;
    public TextView nReceiptType;
    public TextView nReceiptAmount;
    public String index;

    public JobsHolder(View itemView) {
        super(itemView);
        nReceiptId = (TextView) itemView.findViewById(R.id.jobId);
        nReceiptType = (TextView) itemView.findViewById(R.id.jobStatus);
        nReceiptAmount = (TextView) itemView.findViewById(R.id.jobDetails);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){

    }
}