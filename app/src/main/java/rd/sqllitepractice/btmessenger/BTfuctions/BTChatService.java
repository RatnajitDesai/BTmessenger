package rd.sqllitepractice.btmessenger.BTfuctions;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import rd.sqllitepractice.btmessenger.ChatActivity;
import rd.sqllitepractice.btmessenger.Utility.Constants;
import rd.sqllitepractice.btmessenger.Utility.Utility;

public class BTChatService{

    private static final String TAG = "BTChatService";


    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChatSecure";

    // Unique UUID for this application
    private static final UUID MY_UUID =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    //vars
    private Context mContext;
    private Handler mHandler;
    private int mState;
    private BluetoothAdapter mBluetoothAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothSocket mBluetoothSocket;


    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    private ConnectedThread mConnectedThread;

    public BTChatService( Handler mHandler) {
        this.mHandler = mHandler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }


    public void startAccepting(){

        mAcceptThread =  new AcceptThread();
        mAcceptThread.start();
        mState = STATE_LISTEN;

    }

    public void closeSocket(String socketType){
        if (socketType.equals("serverSocket"))
        {
            mAcceptThread.cancel();
        }
        else if (socketType.equals("socket"))
        {
            mConnectThread.cancel();
        }
        else if (socketType.equals("connectedSocket")) {
            mConnectedThread.cancel();
        }
    }


    public void sendMessage(String msg, String resID){
        Log.d(TAG, "sendMessage: Message "+msg);
        Intent intent = new Intent(Constants.MESSAGE_RECEIVER);
        intent.putExtra("Message", msg);
        intent.putExtra(Constants.MESSAGE_TYPE, resID);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        Log.d(TAG, "sendMessage: INTENT BROADCAST WITH MESSAGE SUCCESSFUL.");
    }

    public int getState(){
        return mState;
    }

    public void startConnecting(BluetoothDevice device) {

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    public void connect() {


    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.

                    connectionEstablished(socket, socket.getRemoteDevice());
                    if (mState == STATE_CONNECTED)
                    {
                        mBluetoothSocket = socket;
                     //   connectedThread();
                    }

                    try
                    {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    public synchronized void connectedThread() {

        Log.d(TAG, "connectedThread: Socket connection established :"+mBluetoothSocket.isConnected());
        mContext = ChatActivity.returnContext();
        Toast.makeText(mContext,
                "Connected to device :"+mBluetoothSocket.getRemoteDevice().getName(),
                Toast.LENGTH_SHORT).show();
        mConnectedThread = new ConnectedThread(mBluetoothSocket);
        mConnectedThread.start();

    }

    private void connectionLost(BluetoothSocket socket) {
        mState = STATE_NONE;
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_CONNECTION_LOST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST_MESSAGE, "Connection closed");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    private void connectionEstablished(BluetoothSocket socket, BluetoothDevice device) {
        mBluetoothSocket = socket;
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_CONNECTED_DEVICE);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME ,device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        mState = STATE_CONNECTED;
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectionEstablished(mmSocket, mmDevice);
            switch (mState){
                case STATE_CONNECTED:
                {
                    mBluetoothSocket = mmSocket;
                  //  connectedThread();
                }
            }

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }




        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;
            private byte[] mmBuffer; // mmBuffer store for the stream


            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams; using temp objects because
                // member streams are final.
                try {
                    tmpIn = socket.getInputStream();

                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating input stream", e);
                }
                try {
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating output stream", e);
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                mmBuffer = new byte[1024];
                int numBytes; // bytes returned from read()


                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    try {
                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);
                        String Readmsg = new String(mmBuffer, 0, numBytes);


                        Log.d(TAG, "READ: MESSAGE :::::::::"+Readmsg);
                        sendMessage(Readmsg, Constants.READ_MESSAGE);

                    } catch (IOException e) {
                        Log.d(TAG, "Input stream was disconnected", e);
                        connectionLost(mmSocket);
                        break;
                    }
                }
            }

            /**
             * Write to the connected OutStream.
             *
             * @param buffer The bytes to write
             */
            public void write(byte[] buffer) {
                try {
                    mmOutStream.write(buffer);
                    String WriteMsg = new String(buffer);

                    Log.d(TAG, "write: MESSAGE :::::::::"+WriteMsg);
                    // Share the sent message back to the UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, WriteMsg)
                            .sendToTarget();
                    sendMessage(WriteMsg, Constants.WRITE_MESSAGE);
                } catch (IOException e) {
                    Log.e(TAG, "Exception during write", e);
                }
            }

            // Call this method from the main activity to shut down the connection.
            public void cancel() {
                try {
                    Toast.makeText(mContext,
                            "Connection closed by : "+mmSocket.getRemoteDevice().getName(),
                            Toast.LENGTH_SHORT).show();
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the connect socket", e);
                }
            }
        }


    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
        Log.d(TAG, "write: MESSAGE EXTERNAL :"+out);
    }

}
