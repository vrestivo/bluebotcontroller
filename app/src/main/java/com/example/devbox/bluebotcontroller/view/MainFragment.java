package com.example.devbox.bluebotcontroller.view;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devbox.bluebotcontroller.Constants;
import com.example.devbox.bluebotcontroller.R;
import com.example.devbox.bluebotcontroller.view.joystick.*;

import java.lang.ref.WeakReference;

import com.example.devbox.bluebotcontroller.model.BluetoothThread;

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
    private EditText mEnterCommandField;
    private String mBuffer;

    //status indicator
    private TextView mConnectionStatusIndicator;

    private final String LOG_TAG = "_MainFragment";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int ACTION_DISCOVERY = 4;

    //bluetooth stuff
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothThread mBluetoothThread;
    private BluetoothDevice mBluetoothDevice;
    private boolean mIsBluetoothOn;
    private int mConnectionState;
    private String mDevInfo;
    private String mMessageFormatString;
    private JoystickHandlerThread mJoysticHandlerThread;
    private static MyUiHandler mUiHAndler;


    //Using static Handler class with weak reference
    //to main activity class IOT avoid memory leaks
    static class MyUiHandler extends Handler {
        private final WeakReference<MainActivity> mMainActivity;

        public MyUiHandler(MainActivity mainActivity) {
            super();
            mMainActivity = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainFragment mainFragment = mMainActivity.get().getmMainFragment();
            mainFragment.handleMessage(msg);
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //retain state on config changes
        setRetainInstance(true);

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
        mUiHAndler = new MyUiHandler((MainActivity) getActivity());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMessageFormatString = getString(R.string.message_format_string);

        mButtonBtOn = (Button) rootView.findViewById(R.id.bt_on);
        mButtonDiscovery = (Button) rootView.findViewById(R.id.bt_discover);
        mButtonSend = (Button) rootView.findViewById(R.id.bt_send);
        mButtonDisconnect = (Button) rootView.findViewById(R.id.bt_disconnect);
        mJoystickView = (JoystickView) rootView.findViewById(R.id.joystick_view);
        mEnterCommandField = (EditText) rootView.findViewById(R.id.edit_text);
        mConnectionStatusIndicator = (TextView) rootView.findViewById(R.id.con_status);

        mIsBluetoothOn = mBluetoothAdapter.isEnabled();
        if (mIsBluetoothOn) {
            mButtonBtOn.setText(getString(R.string.button_bt_off));
        } else {
            mButtonBtOn.setText(getString(R.string.button_bt_on));
        }

        mJoystickView.setJoystickDragListener(new JoystickView.OnJoystickDragListener() {
            @Override
            public void onJoystickUpdate(float x, float y, float resultant, boolean keepSending) {
                String messageToSend = String.format(mMessageFormatString, x, y, resultant);
                if (mJoysticHandlerThread != null && mJoysticHandlerThread.isAlive()) {

                    if (keepSending && mJoysticHandlerThread.isSending()) {
                        mJoysticHandlerThread.setKeepSending(keepSending);
                        mJoysticHandlerThread.setDataToSend(messageToSend);
                    } else if (keepSending && !mJoysticHandlerThread.isSending()) {
                        mJoysticHandlerThread.setKeepSending(keepSending);
                        mJoysticHandlerThread.getHandler()
                                .obtainMessage(JoystickHandlerThread.SEND_MSG, messageToSend)
                                .sendToTarget();
                    } else {
                        mJoysticHandlerThread.setKeepSending(false);
                    }
                }
            }
        });

        mJoystickView.setStopSendingListener(new JoystickView.StopSendingJoysticDataListener() {
            @Override
            public void onStopSending() {
                if (mJoysticHandlerThread != null && mJoysticHandlerThread.isAlive()) {
                    mJoysticHandlerThread.setKeepSending(false);
                }
            }
        });

        mButtonBtOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsBluetoothOn = mBluetoothAdapter.isEnabled();
                if (mIsBluetoothOn) {
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
                btDiscover();
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
                mBuffer = mEnterCommandField.getText().toString();
                if (mBluetoothThread != null) {
                    mBluetoothThread.sendToRemoteBt(mBuffer);
                } else {
                    Toast.makeText(getContext(), "Service not started.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateStatusIndicator(mConnectionState, mDevInfo);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(btEnableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsBluetoothOn = mBluetoothAdapter.isEnabled();
        //mJoysticHandlerThread = new JoystickHandlerThread(JoystickHandlerThread.NAME);
        mJoysticHandlerThread.start();

        if (mBluetoothThread != null) {
            //mJoysticHandlerThread.setBluetoothThread(mBluetoothThread);
        }

        if (mIsBluetoothOn) {
            mButtonBtOn.setText(getString(R.string.button_bt_off));
            enableButtons(mIsBluetoothOn);
        } else {
            mButtonBtOn.setText(getString(R.string.button_bt_on));
            enableButtons(mIsBluetoothOn);
        }
    }

    @Override
    public void onStop() {
        if (mJoysticHandlerThread != null && mJoysticHandlerThread.isAlive()) {
            mJoysticHandlerThread.quit();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //terminate connection
        if (mBluetoothThread != null) {
            mBluetoothThread.disconnect();
            mConnectionState = ST_DISCONNECTED;
            mDevInfo = null;
        }
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "_in onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
/*


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                        mBluetoothDevice = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (mBluetoothThread == null) {
                            mBluetoothThread = new BluetoothThread(getContext(), mUiHAndler);
                            mJoysticHandlerThread.setBluetoothThread(mBluetoothThread);
                        }
                        Log.v(LOG_TAG, "_staring service for" + deviceString);
                        BluetoothDevice existingDevice = mBluetoothThread.getDevice();
                        if (existingDevice != null && mBluetoothDevice.getAddress().equals(existingDevice.getAddress())) {
                            Toast.makeText(getContext(), getString(R.string.bt_error_already_connected_same_dev), Toast.LENGTH_SHORT).show();
                        } else if (mBluetoothThread.isConnected()) {
                            Toast.makeText(getContext(), getString(R.string.bt_error_already_connected), Toast.LENGTH_SHORT).show();
                        } else {
                            mBluetoothThread.connect(((BluetoothDevice) data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)), true);
                            mJoysticHandlerThread.setBluetoothThread(mBluetoothThread);
                        }
                    }
                    break;
                }
            }
        }
    }

*/

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_CON_STATE_CHANGE:
                //TODO finish
                mConnectionState = msg.arg1;
                //TODO delete logging
                Log.v(LOG_TAG, "contains devinfo: " + msg.getData().containsKey(DEV_INFO_STR));
                if (mConnectionState == ST_CONNECTED && msg.getData().containsKey(DEV_INFO_STR)) {
                    mDevInfo = msg.getData().getString(DEV_INFO_STR);
                    updateStatusIndicator(mConnectionState, mDevInfo);
                } else {
                    mDevInfo = null;
                    updateStatusIndicator(mConnectionState, mDevInfo);
                }
                break;
            case Constants.MESSAGE_WRITE:
                //TODO implement
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



    /**
     * ** BLUETOOTH OPERATIONS ***
     **/
    public void btOn(View v) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent btOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOnIntent, REQUEST_ENABLE_BT);
            mButtonBtOn.setText(getString(R.string.button_bt_off));
            //mIsBluetoothOn = mBluetoothAdapter.isEnabled();
            Toast.makeText(getContext(), getString(R.string.bt_admin_on), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.bt_admin_already_on), Toast.LENGTH_SHORT);
        }
    }


    public void btOff(View v) {
        if (mBluetoothAdapter.isEnabled()) {
            //TODO delete logging
            Log.v(LOG_TAG, "inside btOff:");
            if (mBluetoothThread != null) {
                mBluetoothThread.disconnect();
                //TODO delete logging
                Log.v(LOG_TAG, "inside btOff: called mBluetoothThread.disconnect()");
            }
            mBluetoothAdapter.disable();
            mButtonBtOn.setText(getString(R.string.button_bt_on));
            //mIsBluetoothOn = mBluetoothAdapter.isEnabled();
            Toast.makeText(getContext(), getString(R.string.bt_admin_off), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.bt_admin_already_off), Toast.LENGTH_SHORT).show();
        }
    }


    public void btDiscover() {
        Intent discoveryIntent = new Intent(getContext(), DiscoveryActivity.class);
        startActivityForResult(discoveryIntent, ACTION_DISCOVERY);
    }


    public void disconnect() {
        if (mBluetoothThread != null) {
            mBluetoothThread.disconnect();
        }
    }


    public void enableButtons(boolean flag) {
        mButtonSend.setEnabled(flag);
        mButtonDisconnect.setEnabled(flag);
        mButtonDiscovery.setEnabled(flag);
    }


    /**
     * takes state code and returns correct state String
     */
    public String pickState(int code) {
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
                    mConnectionStatusIndicator.setText(mDevInfo);
                } else {
                    mConnectionStatusIndicator.setText(STR_CONNECTED);
                    mDevInfo = null;
                }
                break;
            }
            //TODO delete
/*            case ST_DISCONNECTED_BY_USR:
                mConnectionStatusIndicator.setText(STR_DISCONNECTED_BY_USR);
                mDevInfo = null;
                break;*/
            case ST_DISCONNECTED:
                mConnectionStatusIndicator.setText(STR_DISCONNECTED);
                mDevInfo = null;
                break;
            case ST_ERROR:
                mConnectionStatusIndicator.setText(STR_ERROR);
                break;
            case ST_NONE:
                mDevInfo = null;
                mConnectionStatusIndicator.setText(getString(R.string.status_default));
                break;
        }

        Toast.makeText(getContext(), pickState(mConnectionState), Toast.LENGTH_SHORT).show();

    }

}
