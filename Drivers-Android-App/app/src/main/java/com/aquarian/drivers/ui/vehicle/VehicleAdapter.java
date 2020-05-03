package com.aquarian.drivers.ui.vehicle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aquarian.drivers.R;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleHolder> {
    private final List<ListItem> vehicleList;

    public VehicleAdapter(ArrayList vehicle) {
        vehicleList = vehicle;
    }

    @Override
    public VehicleHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View vehicleView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehiclelist, parent, false);

        return new VehicleHolder(vehicleView);

    }

    @Override
    public void onBindViewHolder(VehicleHolder holder, int position) {
        holder.vHeader.setText(vehicleList.get(position).getHeader());
        holder.vValue.setText(vehicleList.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return vehicleList != null ? vehicleList.size() : 0;
    }

}

class VehicleHolder extends RecyclerView.ViewHolder{

    public TextView vHeader;
    public TextView vValue;

    public VehicleHolder(View itemView) {
        super(itemView);
        vHeader = (TextView) itemView.findViewById(R.id.vehicleHeader);
        vValue = (TextView) itemView.findViewById(R.id.vehicleItem);
    }
}

