package com.example.devbox.bluebotcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.devbox.bluebotcontroller.joystick.*;

import static com.example.devbox.bluebotcontroller.Constants.DEV_INFO_STR;
import static com.example.devbox.bluebotcontroller.Constants.STR_CONNECTED;
import static com.example.devbox.bluebotcontroller.Constants.STR_CONNECTING;
import static com.example.devbox.bluebotcontroller.Constants.STR_DISCONNECTED;
import static com.example.devbox.bluebotcontroller.Constants.STR_DISCONNECTED_BY_USR;
import static com.example.devbox.bluebotcontroller.Constants.STR_ERROR;
import static com.example.devbox.bluebotcontroller.Constants.STR_NONE;
import static com.example.devbox.bluebotcontroller.Constants.ST_CONNECTED;
import static com.example.devbox.bluebotcontroller.Constants.ST_CONNECTING;
import static com.example.devbox.bluebotcontroller.Constants.ST_DISCONNECTED;
import static com.example.devbox.bluebotcontroller.Constants.ST_DISCONNECTED_BY_USR;
import static com.example.devbox.bluebotcontroller.Constants.ST_ERROR;
import static com.example.devbox.bluebotcontroller.Constants.ST_NONE;

/**
 * This class provides app UI
 */

public class MainFragment extends Fragment {

    public static final String TAG = "MainFragment_TAG";

    //buttons
    private Button mButtonBtOn;
    private Button mButtonDiscovery;
    private Button mButtonSend;
    private Button mButtonDisconnect;
    private JoystickView mJoystickView;

    //text input field
    private EditText mEditText;
    private String mBuffer;

    //status indicator
    private TextView mConStatusTitle;
    private TextView mConStatus;

    private final String LOG_TAG = "_MainFragment";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int ACTION_STATE_CHAGED = 2;
    private static final int ACTION_FOUND = 3;
    private static final int ACTION_DISCOVERY = 4;

    //bluetooth stuff
    private BluetoothAdapter mBluetoothAdapter;
    private BTConnectionService mBtService;
    private BluetoothDevice mBtDevice;
    private boolean mOn;
    private int mConState;
    private String mDevInfo;
    private String mMessageFormatString;


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MESSAGE_CON_STATE_CHANGE:
                    //TODO finish
                    //FIXME pass the device address if connected
                    mConState = msg.arg1;
                    //TODO delete logging
                    Log.v(LOG_TAG, "contains devinfo: " + msg.getData().containsKey(DEV_INFO_STR));
                    if (mConState == ST_CONNECTED && msg.getData().containsKey(DEV_INFO_STR)) {
                        mDevInfo = msg.getData().getString(DEV_INFO_STR);
                        updateStatusIndicator(mConState, mDevInfo);
                    } else {
                        mDevInfo = null;
                        updateStatusIndicator(mConState, mDevInfo);
                    }
                    //check
                    if (getActivity() != null)
                        Toast.makeText(getContext(), pickState(mConState), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_WRITE:
                    //TODO implement
                    if (getActivity() != null)
                        Toast.makeText(getContext(), "just Wrote something", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    if (msg.getData().containsKey(Constants.TOAST_STR)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), msg.getData().getString(Constants.TOAST_STR), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_FROM_REMOTE_DEVICE: {
                    //TODO print message in the console view when ready


                    break;
                }
            }

        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //TODO delete logging
        Log.v(LOG_TAG, "_in_onCreate()");

        //retain state on config changes
        setRetainInstance(true);

        //TODO setHasOptionsMenu(true); when ready
        //TODO cancel connection when BT is turned off;

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

        //TODO delete logging
        Log.v(LOG_TAG, "_in_onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMessageFormatString = getString(R.string.message_format_string);

        mButtonBtOn = (Button) rootView.findViewById(R.id.bt_on);

        mOn = mBluetoothAdapter.isEnabled();

        if (mOn) {
            mButtonBtOn.setText(getString(R.string.button_bt_off));
        } else {
            mButtonBtOn.setText(getString(R.string.button_bt_on));

        }

        mButtonDiscovery = (Button) rootView.findViewById(R.id.bt_discover);
        mButtonSend = (Button) rootView.findViewById(R.id.bt_send);
        mButtonDisconnect = (Button) rootView.findViewById(R.id.bt_disconnect);
        mJoystickView = (JoystickView) rootView.findViewById(R.id.joystick_view);
        mJoystickView.setJoystickDragListener(new JoystickView.OnJoystickDragListener() {
            @Override
            public void onJoystickDrag(float x, float y, float resultant) {
                //format data and send over bluetooth
                sendMessage(String.format(mMessageFormatString, x,y,resultant));
            }
        });



        //edit text view
        mEditText = (EditText) rootView.findViewById(R.id.exit_text);

        //status indicator
        mConStatus = (TextView) rootView.findViewById(R.id.con_status);

        updateStatusIndicator(mConState, mDevInfo);

        mButtonBtOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOn = mBluetoothAdapter.isEnabled();
                if (mOn) {
                    btOff(v);
                    enableButtons(false);

                } else {
                    btOn(v);
                    enableButtons(true);

                }
            }
        });


        mButtonDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDiscover(v);
            }
        });

        mButtonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement send message
                mBuffer = mEditText.getText().toString();

                if (mBtService != null) {
                    mBtService.sendToRemoteBt(mBuffer);
                    //TODO delete when done
                    Log.v(LOG_TAG, "data passed to thread");
                } else {
                    Toast.makeText(getContext(), "Service not started.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //mDevInfo = savedInstanceState.getString(DEV_INFO_STR);

        }
    }

    @Override
    public void onStart() {
        //TODO delete logging
        Log.v(LOG_TAG, "_in_onStart()");
        super.onStart();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(btEnableIntent, REQUEST_ENABLE_BT);
            } else {
                //TODO initialization
            }
        }


    }

    @Override
    public void onResume() {
        //TODO delete logging
        super.onResume();
        Log.v(LOG_TAG, "_in_onResume()" + mConState + " " + mDevInfo);

        mOn = mBluetoothAdapter.isEnabled();
        //updateStatusIndicator(mConState, mDevInfo);

        if (mOn) {
            mButtonBtOn.setText(getString(R.string.button_bt_off));
            enableButtons(mOn);
        } else {
            mButtonBtOn.setText(getString(R.string.button_bt_on));
            enableButtons(mOn);
        }
    }

    @Override
    public void onStop() {
        //TODO delete logging
        Log.v(LOG_TAG, "_in_onStop()");
        super.onStop();

    }

    @Override
    public void onDestroy() {
        //TODO delete logging
        Log.v(LOG_TAG, "_in_onDestroy()");
        //terminate connection
        if (mBtService != null) {
            mBtService.disconnect();
            mConState = ST_DISCONNECTED_BY_USR;
            mDevInfo = null;
            //mConStatus.setText(STR_DISCONNECTED);
        }
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "_in onSaveInstanceState");
//        outState.putInt(STATE_STR, mConState);
//        outState.putString(DEV_INFO_STR, mDevInfo);
        super.onSaveInstanceState(outState);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO delete when done
        Log.v(LOG_TAG, "_in onActivityResult");

        //TODO finish implementation
        if (data != null) {
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
                    Toast.makeText(getContext(), "Action Discover: device Selected: " + deviceString, Toast.LENGTH_SHORT).show();
                    if (data.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
                        mBtDevice = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (mBtService == null) {
                            mBtService = new BTConnectionService(getContext(), mHandler);
                        }
                        Log.v(LOG_TAG, "_staring service for" + deviceString);
                        //TODO check if already connected to the same device
                        BluetoothDevice existingDevice = mBtService.getDevice();
                        if (existingDevice != null && mBtDevice.getAddress().equals(existingDevice.getAddress())) {
                            Toast.makeText(getContext(), getString(R.string.bt_error_already_connected_same_dev), Toast.LENGTH_SHORT).show();

                        }
                        else if (mBtService.isConnected()){
                            Toast.makeText(getContext(), getString(R.string.bt_error_already_connected), Toast.LENGTH_SHORT).show();

                        }
                        else {
                            mBtService.connect(((BluetoothDevice) data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)), true);
                        }
                    }

                    break;
                }
            }
        }
    }


    /**
     * ** BLUETOOTH OPERATIONS ***
     **/


    public void btOn(View v) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent btOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOnIntent, REQUEST_ENABLE_BT);
            mButtonBtOn.setText(getString(R.string.button_bt_off));
            //mOn = mBluetoothAdapter.isEnabled();
            Toast.makeText(getContext(), getString(R.string.bt_admin_on), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.bt_admin_already_on), Toast.LENGTH_SHORT);
        }
    }

    public void btOff(View v) {
        if (mBluetoothAdapter.isEnabled()) {
            //TODO delete logging
            Log.v(LOG_TAG, "inside btOff:");
            if (mBtService != null) {
                mBtService.disconnect();
                //TODO delete logging
                Log.v(LOG_TAG, "inside btOff: called mBtService.disconnect()");
            }
            mBluetoothAdapter.disable();
            mButtonBtOn.setText(getString(R.string.button_bt_on));
            //mOn = mBluetoothAdapter.isEnabled();
            Toast.makeText(getContext(), getString(R.string.bt_admin_off), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.bt_admin_already_off), Toast.LENGTH_SHORT).show();
        }
    }

    public void btDiscover(View v) {
        //TODO start Bt discovery activity
        Intent discoveryIntent = new Intent(getContext(), DiscoveryActivity.class);
        startActivityForResult(discoveryIntent, ACTION_DISCOVERY);
    }

    public void sendMessage(String message) {
        //TODO implement
        if (mBtService != null && message != null) {
            mBtService.sendToRemoteBt(message);
        } else {
            Log.v(LOG_TAG, "cant sent message: " + message + " " + String.valueOf(mBtService != null));
        }
    }

    public void disconnect() {
        if (mBtService != null) {
            mBtService.disconnect();
            mConState = ST_DISCONNECTED_BY_USR;
            updateStatusIndicator(mConState, null);
        }
    }

    public void enableButtons(boolean flag) {
        mButtonSend.setEnabled(flag);
        //TODO clean up
        //mButtonForward.setEnabled(flag);
        //mButtonReverse.setEnabled(flag);
        //mButtonLeft.setEnabled(flag);
        //mButtonRight.setEnabled(flag);
        mButtonDisconnect.setEnabled(flag);
        mButtonDiscovery.setEnabled(flag);
    }


    /**
     * takes state code and returns correct state String
     */
    private String pickState(int code) {
        switch (code) {
            case ST_ERROR:
                return STR_ERROR;
            case ST_NONE:
                return STR_NONE;
            case ST_CONNECTING:
                return STR_CONNECTING;
            case ST_CONNECTED:
                return STR_CONNECTED;
            case ST_DISCONNECTED:
                return STR_DISCONNECTED;
            case ST_DISCONNECTED_BY_USR:
                return STR_DISCONNECTED_BY_USR;
        }
        return STR_NONE;
    }


    private void updateStatusIndicator(int statusCode, @Nullable String devinfo) {
        switch (statusCode) {
            case ST_CONNECTED: {
                //TODO update status
                if (devinfo != null) {
                    mDevInfo = devinfo;

                    mConStatus.setText(mDevInfo);
                } else {
                    mConStatus.setText(STR_CONNECTED);
                    mDevInfo = null;
                }
                break;
            }
            case ST_DISCONNECTED_BY_USR:
                mConStatus.setText(STR_DISCONNECTED_BY_USR);
                mDevInfo = null;
                break;

            case ST_ERROR:
                mConStatus.setText(STR_ERROR);
                break;
            case ST_NONE:
                mDevInfo = null;
                mConStatus.setText(getString(R.string.status_default));
                break;
        }
    }

}
