package com.example.devbox.bluebotcontroller.view;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.devbox.bluebotcontroller.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothDeviceViewHolder> {

    interface OnDeviceSelected{
        void onDeviceClick(BluetoothDevice device);
    }

    private List<BluetoothDevice> mDeviceList;
    private OnDeviceSelected mDeviceClickListener;



    public BluetoothDeviceAdapter(OnDeviceSelected listener) {
        mDeviceList = new ArrayList<>();
        mDeviceClickListener = listener;
    }


    public void updateDeviceDataSet(Set<BluetoothDevice> newDevices){
        if(newDevices!=null && newDevices.size()>0){
            mDeviceList.clear();
            mDeviceList.addAll(newDevices);
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public BluetoothDeviceAdapter.BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView deviceItem = (TextView) LayoutInflater.from(context).inflate(R.layout.device_list_item, parent, false);

        BluetoothDeviceViewHolder viewHolder = new BluetoothDeviceViewHolder(deviceItem);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceAdapter.BluetoothDeviceViewHolder holder, int position) {
        holder.mmDeviceName.setText(mDeviceList.get(position).getAddress()+ " " + mDeviceList.get(position).getName());
        holder.mmId = position;
    }


    @Override
    public int getItemCount() {
        if(mDeviceList!=null) return mDeviceList.size();
        return 0;
    }


    public class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {

        public int mmId;
        public TextView mmDeviceName;

        public BluetoothDeviceViewHolder(View itemView) {
            super(itemView);
            mmDeviceName = (TextView) itemView;
            mmDeviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDeviceList!=null) mDeviceClickListener.onDeviceClick(mDeviceList.get(mmId));
                }
            });
        }
    }

}