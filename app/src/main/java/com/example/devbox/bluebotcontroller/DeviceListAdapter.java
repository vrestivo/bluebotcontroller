package com.example.devbox.bluebotcontroller;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * Created by devbox on 2/10/17.
 */

public class DeviceListAdapter extends ArrayAdapter<String> {
    public DeviceListAdapter(Context context, int resource) {
        super(context, resource);
    }
}
