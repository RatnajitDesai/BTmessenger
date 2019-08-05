package rd.sqllitepractice.btmessenger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import rd.sqllitepractice.btmessenger.Adapter.MessagesRecyclerAdapter;
import rd.sqllitepractice.btmessenger.BTfuctions.BTChatService;
import rd.sqllitepractice.btmessenger.Utility.BTChatServiceSingleton;
import rd.sqllitepractice.btmessenger.Utility.Constants;
import rd.sqllitepractice.btmessenger.Utility.Utility;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";


    //vars
    private BluetoothAdapter mBluetoothAdapter;
    private final String NAME = "BT_Messenger";
    private final String UUID_STRING  = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    private int mState;
    private int mNewState;
    private BTChatService mBtChatService;
    private static Context mContext;
    private MessagesRecyclerAdapter mMessagesRecyclerAdapter;
    private ArrayList<rd.sqllitepractice.btmessenger.Models.Message> message_list = new ArrayList<>();
    private BluetoothSocket mBluetoothSocket;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    //widgets

    private ImageView mClose, mSend;
    private EditText mEnterMessage;
    private  TextView mRecipientName;
    private  RecyclerView mRecyclerView;
    private rd.sqllitepractice.btmessenger.Models.Message message = null;
    private StringBuffer mOutStringBuffer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatscreen);
        mClose = findViewById(R.id.close);
        mSend = findViewById(R.id.sendBtn);
        mEnterMessage = findViewById(R.id.etChatInput);
        mRecipientName = findViewById(R.id.tvRecipient);
        mContext = getApplicationContext();
        mRecyclerView = findViewById(R.id.chat_msg_container);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mOutStringBuffer = new StringBuffer("");
        mContext = getApplicationContext();

        init();
        initializeRecyclerView();

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close the Connection
                mBtChatService.closeSocket("connectedSocket");
                Toast.makeText(mContext, "Connection closed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage(mEnterMessage.getText().toString());
            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver , new IntentFilter(Constants.MESSAGE_RECEIVER));

    }

    private  BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String msg = intent.getStringExtra("Message");
            Log.d(TAG, "onReceive: Received message."+msg);

            String msg_type = intent.getStringExtra(Constants.MESSAGE_TYPE);
            Log.d(TAG, "onReceive: msg type : "+ msg_type);

            if (msg_type.equals(Constants.READ_MESSAGE))
            {
                message = new rd.sqllitepractice.btmessenger.Models.Message(
                        msg,Utility.getCurrentTimeStamp(), false );
            }
            else if (msg_type.equals(Constants.WRITE_MESSAGE))
            {
                message = new rd.sqllitepractice.btmessenger.Models.Message(
                        msg,Utility.getCurrentTimeStamp(), true );
            }
            message_list.add(message);
            mMessagesRecyclerAdapter.notifyDataSetChanged();
        }
    };


    public static Context returnContext(){
        return mContext;
    }
    private void initializeRecyclerView() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mMessagesRecyclerAdapter = new MessagesRecyclerAdapter(message_list);
        mRecyclerView.setAdapter(mMessagesRecyclerAdapter);
    }


    private void sendMessage(String msg) {
        if (msg.length() > 0 )
        {
            Log.d(TAG, "sendMessage: message "+msg);
            byte[] msgBytes = msg.getBytes();
            mBtChatService.write(msgBytes);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mEnterMessage.setText(mOutStringBuffer);
        }
    }

    private void init() {

        String device_name = getIntent().getStringExtra(Constants.DEVICE_NAME);
        mRecipientName.setText(device_name);
        startMessaging();

    }

    private void startMessaging() {

        mBtChatService = BTChatServiceSingleton.getInstance(handler);
        mBtChatService.connectedThread();

    }


    private final Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Constants
                        .MESSAGE_TOAST: {
                    Toast.makeText(mContext, msg.getData()
                            .getString(Constants.TOAST_MESSAGE), Toast.LENGTH_SHORT).show();
                }
                case Constants.MESSAGE_CONNECTED_DEVICE: {
                    Toast.makeText(mContext, "Connected with " + msg.getData()
                            .getString(Constants.DEVICE_NAME), Toast.LENGTH_SHORT).show();
                }
                case Constants.MESSAGE_CONNECTION_LOST: {
                    Toast.makeText(mContext, msg.getData()
                            .getString(Constants.TOAST_MESSAGE), Toast.LENGTH_SHORT).show();
                }
                case Constants.MESSAGE_WRITE:
                {
                    // construct a string from the buffer
                    String writeMsg =(String)msg.obj;
                    message = new rd.sqllitepractice.btmessenger.Models.Message(writeMsg,Utility.getCurrentTimeStamp(), true);
                    Log.d(TAG, "handleMessage: message write :"+writeMsg);
                    message_list.add(message);
                    mMessagesRecyclerAdapter.notifyDataSetChanged();
                    break;

                }
                case Constants.MESSAGE_READ:
                    // construct a string from the valid bytes in the buffer
                    String readMsg = (String) msg.obj;
                    message = new rd.sqllitepractice.btmessenger.Models.Message(readMsg,Utility.getCurrentTimeStamp(), false);
                    Log.d(TAG, "handleMessage: message read :"+message.toString());
                    message_list.add(message);
                    mMessagesRecyclerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };





}
