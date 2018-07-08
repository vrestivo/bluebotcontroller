package com.example.devbox.bluebotcontroller.view.discovery;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.devbox.bluebotcontroller.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceListAdapter extends RecyclerView.Adapter<BluetoothDeviceListAdapter.BluetoothDeviceViewHolder> {
    private List<BluetoothDevice> mDeviceList;
    private OnDeviceSelected mDeviceClickListener;


    public BluetoothDeviceListAdapter(OnDeviceSelected listener) {
        mDeviceList = new ArrayList<>();
        mDeviceClickListener = listener;
    }

    public void updateDeviceDataSet(Set<BluetoothDevice> newDevices){
        if(newDevices!=null && newDevices.size()>0){
            mDeviceList.clear();
            mDeviceList.addAll(newDevices);
        }
        notifyDataSetChanged();
    }

    public interface OnDeviceSelected{
        void onDeviceClick(BluetoothDevice device);
    }

    @NonNull
    @Override
    public BluetoothDeviceListAdapter.BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemRootView = LayoutInflater.from(context).inflate(R.layout.device_list_item, parent, false);
        return new BluetoothDeviceViewHolder(itemRootView);
        }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceListAdapter.BluetoothDeviceViewHolder holder, int position) {
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
            mmDeviceName = (TextView) itemView.findViewById(R.id.device_list_item_device_info_tv);
            mmDeviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDeviceList!=null) mDeviceClickListener.onDeviceClick(mDeviceList.get(mmId));
                }
            });
        }
    }

}
