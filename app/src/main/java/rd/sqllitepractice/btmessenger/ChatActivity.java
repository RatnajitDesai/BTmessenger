package rd.sqllitepractice.btmessenger;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import rd.sqllitepractice.btmessenger.Adapter.MessagesRecyclerAdapter;
import rd.sqllitepractice.btmessenger.BTfuctions.BTChatService;
import rd.sqllitepractice.btmessenger.Utility.BTChatServiceSingleton;
import rd.sqllitepractice.btmessenger.Utility.Constants;
import rd.sqllitepractice.btmessenger.Utility.Utility;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private static final int REQUEST_SEND_IMAGE = 101;


    //vars
    private BTChatService mBtChatService;
    private Context mContext;
    private MessagesRecyclerAdapter mMessagesRecyclerAdapter;
    private ArrayList<rd.sqllitepractice.btmessenger.Models.Message> message_list = new ArrayList<>();

//widgets

    private ImageView mClose, mSend;
    private EditText mEnterMessage;
    private  TextView mRecipientName;
    private  RecyclerView mRecyclerView;
    private rd.sqllitepractice.btmessenger.Models.Message message = null;
    private StringBuffer mOutStringBuffer;
    private final Handler handler = new Handler() {

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
                case Constants.MESSAGE_WRITE: {
                    // construct a string from the buffer
                    String writeMsg = (String) msg.obj;
                    message = new rd.sqllitepractice.btmessenger.Models.Message(writeMsg, Utility.getCurrentTimeStamp(), true, false);
                    Log.d(TAG, "handleMessage: message write :" + writeMsg);
                    message_list.add(message);
                    mMessagesRecyclerAdapter.notifyDataSetChanged();
                    break;

                }
                case Constants.MESSAGE_READ:
                    // construct a string from the valid bytes in the buffer
                    String readMsg = (String) msg.obj;
                    message = new rd.sqllitepractice.btmessenger.Models.Message(readMsg, Utility.getCurrentTimeStamp(), false, false);
                    Log.d(TAG, "handleMessage: message read :" + message.toString());
                    message_list.add(message);
                    mMessagesRecyclerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private Toolbar toolbar;
    private  BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.hasExtra("Image_Uri")) {
                if (intent.getStringExtra(Constants.MESSAGE_TYPE).equals(Constants.READ_MESSAGE)) {

                    Bitmap bitmap = intent.getParcelableExtra("Image_Uri");
                    try {
                        message = new rd.sqllitepractice.btmessenger.Models.Message(bitmap,
                                Utility.getCurrentTimeStamp(), false, true);
                    } catch (Exception e) {
                        Log.e(TAG, "onReceive: Exception - " + e.getMessage());
                    }
                } else if (intent.getStringExtra(Constants.MESSAGE_TYPE).equals(Constants.WRITE_MESSAGE)) {

                    Bitmap bitmap = intent.getParcelableExtra("Image_Uri");
                    try {
                        message = new rd.sqllitepractice.btmessenger.Models.Message(bitmap,
                                Utility.getCurrentTimeStamp(), true, true);
                    } catch (Exception e) {
                        Log.e(TAG, "onReceive:IOException - " + e.getMessage());
                    }
                }
            } else {

                String msg = intent.getStringExtra("Message");
                Log.d(TAG, "onReceive: Received message." + msg);

                String msg_type = intent.getStringExtra(Constants.MESSAGE_TYPE);
                Log.d(TAG, "onReceive: msg type : " + msg_type);

                if (msg_type.equals(Constants.READ_MESSAGE)) {
                    message = new rd.sqllitepractice.btmessenger.Models.Message(
                            msg, Utility.getCurrentTimeStamp(), false, false);
                } else if (msg_type.equals(Constants.WRITE_MESSAGE)) {
                    message = new rd.sqllitepractice.btmessenger.Models.Message(
                            msg, Utility.getCurrentTimeStamp(), true, false);
                }
            }

            message_list.add(message);
            mMessagesRecyclerAdapter.notifyDataSetChanged();
        }
    };

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
        mOutStringBuffer = new StringBuffer();
        mContext = getApplicationContext();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.MESSAGE_RECEIVER));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }


    private void initializeRecyclerView() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mMessagesRecyclerAdapter = new MessagesRecyclerAdapter(message_list);
        mRecyclerView.setAdapter(mMessagesRecyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_images:
                sendImageToDevice();
                break;
            case R.id.about:
                //show profile details
                break;
        }
        return true;
    }

    private void init() {

        String device_name = getIntent().getStringExtra(Constants.DEVICE_NAME);
        mRecipientName.setText(device_name);
        startMessaging();

    }

    private void startMessaging() {

        mBtChatService = BTChatServiceSingleton.getInstance(mContext, handler);
        mBtChatService.connectedThread();

    }

    private void sendMessage(String msg) {
        if (msg.length() > 0 )
        {
            Log.d(TAG, "sendMessage: message "+msg);
            byte[] msgBytes = msg.getBytes();
            mBtChatService.write(msgBytes, false);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mEnterMessage.setText(mOutStringBuffer);
        }
    }

    private void sendImageToDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_SEND_IMAGE);
    }

    private void sendImage(Bitmap img) {
        if (img != null) {
            Log.d(TAG, "sendMessage: message " + img);
            String s = Utility.BitMapToString(img);
            mBtChatService.write(s.getBytes(), true);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mEnterMessage.setText(mOutStringBuffer);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_SEND_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        sendImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }
    }
}
