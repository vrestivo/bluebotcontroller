package com.example.devbox.bluebotcontroller;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by devbox on 2/9/17.
 */

public class MainFragment extends Fragment {

    private Button mButtonBtOn;
    private Button mButtonoBtOff;
    private Button mButtonDiscovery;


    private static final int REQUEST_ENABLE_BT = 1;
    private static final int ACTION_STATE_CHAGED = 2;
    private static final int ACTION_FOUND = 3;
    private static final int ACTION_DISCOVERY = 4;



    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO setHasOptionsMenu(true); when ready

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (mBluetoothAdapter == null) {
            //TODO see if need to call getAdapter();
            MainActivity mainActivity = (MainActivity) getActivity();
            Toast.makeText(mainActivity, getString(R.string.bt_admin_not_available), Toast.LENGTH_SHORT).show();
            //exit the app if bluetooth is not available
            mainActivity.finish();
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mButtonBtOn = (Button) rootView.findViewById(R.id.bt_on);
        mButtonoBtOff = (Button) rootView.findViewById(R.id.bt_off);
        mButtonDiscovery = (Button) rootView.findViewById(R.id.bt_discover);

        mButtonBtOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btOn(v);
            }
        });

        mButtonoBtOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btOff(v);
            }
        });

        mButtonDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDiscover(v);
            }
        });


        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(btEnableIntent, REQUEST_ENABLE_BT);
            } else {
                //TODO initialization
                Toast.makeText(getContext(), "TODO: initialization block", Toast.LENGTH_SHORT).show();
            }
        }


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO finish implementation
        if(data!=null) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT: {
                    Toast.makeText(getContext(), String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
                    break;
                }
                case ACTION_DISCOVERY: {
                    String deviceString = null;
                    if (data.hasExtra(DiscoveryActivity.DEVICE_STRING)) {
                        deviceString = data.getStringExtra(DiscoveryActivity.DEVICE_STRING);
                    }
                    Toast.makeText(getContext(), "device Selected: " + deviceString, Toast.LENGTH_SHORT).show();
                    //TODO initiate pairing
                    break;
                }

            }
        }
    }


    /** *** BLUETOOTH OPERATIONS ***  **/


    public void btOn(View v) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent btOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOnIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getContext(), getString(R.string.bt_admin_on), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.bt_admin_already_on), Toast.LENGTH_SHORT);
        }
    }

    public void btOff(View v) {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(getContext(), getString(R.string.bt_admin_off), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.bt_admin_already_off), Toast.LENGTH_SHORT).show();
        }
    }

    public void btDiscover(View v){
        //TODO start Bt discovery activity
        Intent discoveryIntent = new Intent(getContext(), DiscoveryActivity.class);
        startActivityForResult(discoveryIntent, ACTION_DISCOVERY);
    }


}
