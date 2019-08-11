package rd.sqllitepractice.btmessenger;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import rd.sqllitepractice.btmessenger.BTfuctions.BTChatService;
import rd.sqllitepractice.btmessenger.Utility.BTChatServiceSingleton;
import rd.sqllitepractice.btmessenger.Utility.Constants;

public class BluetoothConnectionActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothConnectionAct";

    //widgets
    TextView mWaiting;
    ImageView mClose;

    //vars
    private BluetoothAdapter mBluetoothAdapter;
    private final String NAME = "BT_Messenger";
    private final String UUID_STRING  = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    private BTChatService mBtChatService;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_discoverable);

        mWaiting = findViewById(R.id.waiting);
        mClose = findViewById(R.id.close);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disconnect bluetooth chat
                mBtChatService.closeSocket("serverSocket");
                finish();
            }
        });

        mContext = getApplicationContext();

        init();

    }

    private void init() {

        Intent intent = getIntent();
        if (intent.hasExtra(getApplicationContext().getString(R.string.Accept_Thread)))
        {
            startAccepting();
        }

    }

    private void startAccepting() {

        mBtChatService = BTChatServiceSingleton.getInstance(mContext, handler);
        mBtChatService.startAccepting();

    }

    private final  Handler handler = new Handler(){

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
                case Constants.MESSAGE_CONNECTED_DEVICE :
                {
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


}
