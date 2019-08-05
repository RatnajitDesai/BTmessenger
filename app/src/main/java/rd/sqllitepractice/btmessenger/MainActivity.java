package rd.sqllitepractice.btmessenger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static boolean DEVICE_MADE_DISCOVERABLE = false;
    private static final int REQUEST_DEVICE_DISCOVERY = 1;

    //vars
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mBluetoothDevice;

    //widgets
    Button btnSearch, btnJoin;
    ImageView ivClose;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearch = (Button)findViewById(R.id.search_BT_btn);
        ivClose = (ImageView)findViewById(R.id.close);
        btnJoin = findViewById(R.id.join_BT_btn);
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        //enabling bluetooth
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeBT();
                if (mAdapter.isEnabled())
                {
                    Log.d(TAG, "onClick: Bluetooth enabled");
                    Log.d(TAG, "onClick: Searching for paired devices");
                    Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
                    intent.putExtra("BT_Enable","BT_Enable");
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enable bluetooth!",Toast.LENGTH_SHORT).show();

                }
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make bluetooth discoverable
                makeDeviceDiscoverable();
            }
        });

        //closing application

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


    }

    private void makeDeviceDiscoverable(){

        try {
            if (!(mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)){
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(discoverableIntent, REQUEST_DEVICE_DISCOVERY);
                Log.d(TAG, "makeDeviceDiscoverable: Device made discoverable : "+ DEVICE_MADE_DISCOVERABLE);
            }
            else{

                Log.d(TAG, "makeDeviceDiscoverable: Device already discoverable : "+ DEVICE_MADE_DISCOVERABLE);
                    DEVICE_MADE_DISCOVERABLE = true;
                    waitForSender();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "makeDeviceDiscoverable: Error occurred while making device discoverable"+e.getMessage() );
            return;
        }
    }

    private void waitForSender(){

        //Change activity to show BluetoothConnections
        if (DEVICE_MADE_DISCOVERABLE) {

            Intent intent = new Intent(getApplicationContext(), BluetoothConnectionActivity.class);
            intent.putExtra(getApplicationContext().getString(R.string.Accept_Thread)
                    , getApplicationContext().getString(R.string.Accept_Thread));
            startActivity(intent);
        }
    }

    private void initializeBT()
    {

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mAdapter == null)
        {
            Log.d(TAG, "init: Device doesn't support bluetooth");
            Toast.makeText(getApplicationContext(), "This device doesn't support bluetooth",Toast.LENGTH_SHORT).show();

        }
        else if (!mAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Log.d(TAG, "initializeBT: Bluetooth is already enabled.");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth enabled", Toast.LENGTH_SHORT).show();
        }
        if (resultCode == 300)
        {
            DEVICE_MADE_DISCOVERABLE = true;
            Log.d(TAG, "onActivityResult: Bluetooth discovery enabled: "+DEVICE_MADE_DISCOVERABLE );
            waitForSender();
            Toast.makeText(getApplicationContext(), "Bluetooth discovery enabled", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_CANCELED)
        {
            Toast.makeText(getApplicationContext(), "Error occurred while enabling bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
