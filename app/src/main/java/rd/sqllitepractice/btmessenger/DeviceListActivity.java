package rd.sqllitepractice.btmessenger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

import rd.sqllitepractice.btmessenger.Adapter.DeviceRecyclerAdapter;
import rd.sqllitepractice.btmessenger.BTfuctions.BTChatService;
import rd.sqllitepractice.btmessenger.Models.BTDevice;
import rd.sqllitepractice.btmessenger.Utility.BTChatServiceSingleton;
import rd.sqllitepractice.btmessenger.Utility.Constants;

public class DeviceListActivity extends AppCompatActivity
        implements DeviceRecyclerAdapter.OnDeviceListener
{
    private static final String TAG = "DeviceListActivity";
    private final String NAME = "BT_Messenger";
    private final String UUID_STRING  = "fa87c0d0-afac-11de-8a39-0800200c9a66";


    //vars
    private ArrayList<BTDevice> allAvailableDevices = new ArrayList<>();
    private Set<BluetoothDevice> pairedDevices;
    private DeviceRecyclerAdapter mDeviceRecyclerAdapter;
    private BTDevice mBtDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BTChatService mBtChatService;

    //UI components
    private RecyclerView mRecyclerView;
    private TextView mSearchMoreDevices;
    private ImageView ivBackArrow;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mRecyclerView = findViewById(R.id.recyclerView);
        ivBackArrow = findViewById(R.id.backArrow);
        mSearchMoreDevices = findViewById(R.id.searchMoreDevices);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = getApplicationContext();
        initRecyclerView();
        getPairedDevices();

        mSearchMoreDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.startDiscovery())
                {
                    scanBTDevices();
                }
            }
        });

        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finish();
                }
                catch (NullPointerException e)
                {
                    Log.e(TAG, "onClick: NullPointerException"+e.getMessage() );
                }
            }
        });

    }


    //Scanning bluetooth devices

    private void scanBTDevices()
    {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d(TAG, "onReceive: Device found name::::::::::::::"+device.getName()+" address :"+device.getAddress());
                    if (!pairedDevices.contains(device))
                    {
                        if (!allAvailableDevices.contains(new BTDevice(device,false))){

                            mBtDevice = new BTDevice(device,false);
                            allAvailableDevices.add(mBtDevice);

                        }
                    }
                    mDeviceRecyclerAdapter.notifyDataSetChanged();
                }

            }
        };

        registerReceiver(receiver, filter);
    }


    //get already paired devices
    public void getPairedDevices(){

        pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                BluetoothClass bluetoothClass = device.getBluetoothClass();
                int deviceClass = bluetoothClass.getDeviceClass();
                if (deviceClass == BluetoothClass.Device.PHONE_SMART) {
                    Log.d(TAG, "getPairedDevices: Found : " + device.getName() + " with address " + device.getAddress());
                    mBtDevice = new BTDevice(device, true);
                    allAvailableDevices.add(mBtDevice);
                }
            }
        }
    }


    private void initRecyclerView()
    {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mDeviceRecyclerAdapter = new DeviceRecyclerAdapter(allAvailableDevices, this);
        mRecyclerView.setAdapter(mDeviceRecyclerAdapter);

    }


    @Override
    public void onDeviceClick(int position) {

        Log.d(TAG, "onDeviceClick: Clicked on device: "+allAvailableDevices.get(position).getBluetoothDevice().getName());
        //Establish connection with the desired device
        BluetoothDevice selected = allAvailableDevices.get(position).getBluetoothDevice();
        Log.d(TAG, "onDeviceClick: selected device bond state :" +selected.getBondState()+" "+BluetoothDevice.BOND_BONDED );
//        if(selected.getBondState() != BluetoothDevice.BOND_BONDED)
//        {
//                pairWithDevice(position);
//        }
//        else {
//                startMessaging(selected);
//        }

            startMessaging(selected);

    }

//    private void pairWithDevice(int position) {
//
//        BluetoothDevice selected = allAvailableDevices.get(position).getBluetoothDevice();
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        Log.d(TAG, "onDeviceClick: Pairing withe device ");
//        Toast.makeText(mContext, "Pairing with : "+selected.getName(), Toast.LENGTH_SHORT).show();
//        final BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(mContext, "Pairing with : "+selected.getName(), Toast.LENGTH_SHORT).show();
//
//                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction()) )
//                {
//
//                    if (selected.getBondState() == BluetoothDevice.BOND_BONDED)
//                    {
//                        allAvailableDevices.get(position).setPaired(true);
//                        mDeviceRecyclerAdapter.notifyDataSetChanged();
//                        Toast.makeText(mContext, "Paired with : "+selected.getName(), Toast.LENGTH_SHORT).show();
//                        startMessaging(selected);
//                    }
//
//                }
//                else
//                {
//                    Toast.makeText(mContext, "Pairing with : "+selected.getName()+" failed. /nPlease retry.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//
//        registerReceiver(receiver, filter);
//
//
//    }

    private void startMessaging(BluetoothDevice device) {

        mBtChatService = BTChatServiceSingleton.getInstance(mContext, handler);
            mBtChatService.startConnecting(device);
            Toast.makeText(mContext, "Connecting...", Toast.LENGTH_SHORT).show();

    }


    private final Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case Constants
                        .MESSAGE_TOAST:
                {
                    Toast.makeText(mContext, msg.getData()
                            .getString(Constants.TOAST_MESSAGE) ,Toast.LENGTH_SHORT ).show();
                }
                case Constants.MESSAGE_CONNECTED_DEVICE : {
                    navigateToChatScreen(msg.getData().getString(Constants.DEVICE_NAME));
                }
                case Constants.MESSAGE_CONNECTION_LOST:
                {
                    Toast.makeText(mContext, msg.getData()
                            .getString(Constants.TOAST_MESSAGE) ,Toast.LENGTH_SHORT ).show();
                }

            }
        }
    };

    private void navigateToChatScreen(String deviceName) {

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.DEVICE_NAME, deviceName);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
