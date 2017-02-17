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

    //buttons
    private Button mButtonBtOn;
    private Button mButtonoBtOff;
    private Button mButtonDiscovery;
    private Button mButtonForward;
    private Button mButtonReverse;
    private Button mButtonLeft;
    private Button mButtonRight;
    private Button mButtonSend;

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
    private boolean mOn;


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getActivity(), "on Handler", Toast.LENGTH_SHORT).show();
            switch (msg.what) {
                case Constants.MESSAGE_CON_STATE_CHANGE:
                    //TODO finish
                    //FIXME pass the device address if connected
                    int stateCode = msg.arg1;
                    if (stateCode == ST_CONNECTED && msg.getData().containsKey(DEV_INFO_STR)) {
                        String devinfo = msg.getData().getString(DEV_INFO_STR);
                        updateStatusIndicator(stateCode, devinfo);
                    } else {
                        updateStatusIndicator(stateCode, null);
                    }
                    Toast.makeText(getContext(), pickState(stateCode), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_WRITE:
                    //TODO implement
                    Toast.makeText(getContext(), "just Wrote something", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    if (msg.getData().containsKey(Constants.TOAST_STR)) {
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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mButtonBtOn = (Button) rootView.findViewById(R.id.bt_on);

        mOn = mBluetoothAdapter.isEnabled();

        if (mOn) {
            mButtonBtOn.setText(getString(R.string.button_bt_off));
        } else {
            mButtonBtOn.setText(getString(R.string.button_bt_on));

        }

        mButtonDiscovery = (Button) rootView.findViewById(R.id.bt_discover);
        mButtonSend = (Button) rootView.findViewById(R.id.bt_send);

        //directional controls
        mButtonForward = (Button) rootView.findViewById(R.id.button_fwd);
        mButtonReverse = (Button) rootView.findViewById(R.id.button_reverse);
        mButtonLeft = (Button) rootView.findViewById(R.id.button_left);
        mButtonRight = (Button) rootView.findViewById(R.id.button_right);

        //edit text view
        mEditText = (EditText) rootView.findViewById(R.id.exit_text);

        //status indicator
        mConStatus = (TextView) rootView.findViewById(R.id.con_status);


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

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement send message
                mBuffer = mEditText.getText().toString();

                //TODO delete
                //Toast.makeText(getContext(), mBuffer, Toast.LENGTH_SHORT).show();
                if (mBtService != null) {
                    mBtService.sendToRemoteBt(mBuffer);
                    //TODO delete when done
                    Log.v(LOG_TAG, "data passed to thread");
                } else {
                    Toast.makeText(getContext(), "Service not started.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //TODO delete below
        //Directional button listeners setup
//        mButtonForward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO delete
//                //Toast.makeText(getContext(), "forward pressed", Toast.LENGTH_SHORT).show();
//                if(mBtService!=null){
//                    mBtService.sendToRemoteBt(Constants.BT_FWD);
//                    //TODO delete when done
//                    Log.v(LOG_TAG, "fwd passed to thread");
//                }else{
//                    Toast.makeText(getContext(), "Service not started.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        mButtonForward.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                //TODO delete logging
//                Log.v(LOG_TAG, "fwd long click");
//                return false;
//            }
//        });


        /**
         * last working on touch listener
         */
//        mButtonForward.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                //TODO delete logging
//                Log.v(LOG_TAG, "fwd on touch listener");
//                if(mBtService!=null){
//                    mBtService.sendToRemoteBt(Constants.BT_FWD);
//                    //TODO delete when done
//                    Log.v(LOG_TAG, "fwd passed to thread");
//                }else{
//                    //Toast.makeText(getContext(), "Service not started.", Toast.LENGTH_SHORT).show();
//                    Log.v(LOG_TAG, "service not started");
//
//                }
//
//                return false;
//            }
//        });


        /**
         * FORWARD button repeat action on touch listener
         * implemented according to the following guidance
         * http://stackoverflow.com/questions/10511423/android-repeat-action-on-pressing-and-holding-a-button
         *
         */
        mButtonForward.setOnTouchListener(new View.OnTouchListener() {

            private Handler mmRepeatHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (mmRepeatHandler != null) {
                            return true;
                        }
                        mmRepeatHandler = new Handler();
                        mmRepeatHandler.postDelayed(mmRunRepeatedAction, Constants.RUN_DELAY);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (mmRepeatHandler == null) {
                            return true;
                        }
                        mmRepeatHandler.removeCallbacks(mmRunRepeatedAction);
                        mmRepeatHandler = null;
                        break;
                    }

                }
                return false;
            }

            Runnable mmRunRepeatedAction = new Runnable() {
                @Override
                public void run() {
                    sendMessage(Constants.BT_FWD);
                    mmRepeatHandler.postDelayed(this, Constants.RUN_DELAY);
                }
            };

        });


        /**
         * REVERSE button repeat action on touch listener
         * implemented according to the following guidance
         * http://stackoverflow.com/questions/10511423/android-repeat-action-on-pressing-and-holding-a-button
         *
         */
        mButtonReverse.setOnTouchListener(new View.OnTouchListener() {

            private Handler mmRepeatHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (mmRepeatHandler != null) {
                            return true;
                        }
                        mmRepeatHandler = new Handler();
                        mmRepeatHandler.postDelayed(mmRunRepeatedAction, Constants.RUN_DELAY);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (mmRepeatHandler == null) {
                            return true;
                        }
                        mmRepeatHandler.removeCallbacks(mmRunRepeatedAction);
                        mmRepeatHandler = null;
                        break;
                    }

                }
                return false;
            }

            Runnable mmRunRepeatedAction = new Runnable() {
                @Override
                public void run() {
                    sendMessage(Constants.BT_REV);
                    mmRepeatHandler.postDelayed(this, Constants.RUN_DELAY);
                }
            };

        });

        /**
         * LEFT button repeat action on touch listener
         * implemented according to the following guidance
         * http://stackoverflow.com/questions/10511423/android-repeat-action-on-pressing-and-holding-a-button
         *
         */
        mButtonLeft.setOnTouchListener(new View.OnTouchListener() {

            private Handler mmRepeatHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (mmRepeatHandler != null) {
                            return true;
                        }
                        mmRepeatHandler = new Handler();
                        mmRepeatHandler.postDelayed(mmRunRepeatedAction, Constants.RUN_DELAY);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (mmRepeatHandler == null) {
                            return true;
                        }
                        mmRepeatHandler.removeCallbacks(mmRunRepeatedAction);
                        mmRepeatHandler = null;
                        break;
                    }

                }
                return false;
            }

            Runnable mmRunRepeatedAction = new Runnable() {
                @Override
                public void run() {
                    sendMessage(Constants.BT_LFT);
                    mmRepeatHandler.postDelayed(this, Constants.RUN_DELAY);
                }
            };

        });


        /**
         * RIGHT button repeat action on touch listener
         * implemented according to the following guidance
         * http://stackoverflow.com/questions/10511423/android-repeat-action-on-pressing-and-holding-a-button
         *
         */
        mButtonRight.setOnTouchListener(new View.OnTouchListener() {

            private Handler mmRepeatHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (mmRepeatHandler != null) {
                            return true;
                        }
                        mmRepeatHandler = new Handler();
                        mmRepeatHandler.postDelayed(mmRunRepeatedAction, Constants.RUN_DELAY);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (mmRepeatHandler == null) {
                            return true;
                        }
                        mmRepeatHandler.removeCallbacks(mmRunRepeatedAction);
                        mmRepeatHandler = null;
                        break;
                    }

                }
                return false;
            }

            Runnable mmRunRepeatedAction = new Runnable() {
                @Override
                public void run() {
                    sendMessage(Constants.BT_RGT);
                    mmRepeatHandler.postDelayed(this, Constants.RUN_DELAY);
                }
            };

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
    public void onResume() {
        super.onResume();
        mOn = mBluetoothAdapter.isEnabled();

        if (mOn) {
            mButtonBtOn.setText(getString(R.string.button_bt_off));
            enableButtons(mOn);
        } else {
            mButtonBtOn.setText(getString(R.string.button_bt_on));
            enableButtons(mOn);
        }
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
                    //TODO initiate pairing
                    if (data.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
                        mBtService = new BTConnectionService(getContext(), mHandler);
                        Log.v(LOG_TAG, "_staring service for" + deviceString);
                        mBtService.connect(((BluetoothDevice) data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)), true);
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
            Log.v(LOG_TAG, "cant sent message");
        }
    }

    public void enableButtons(boolean flag) {
        mButtonSend.setEnabled(flag);
        mButtonForward.setEnabled(flag);
        mButtonReverse.setEnabled(flag);
        mButtonLeft.setEnabled(flag);
        mButtonRight.setEnabled(flag);
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
                    String statusMessage = mBluetoothAdapter.getName() + " " + devinfo;
                    mConStatus.setText(devinfo);
                } else {
                    mConStatus.setText(STR_CONNECTED);
                }
                break;
            }

        }
    }

}
