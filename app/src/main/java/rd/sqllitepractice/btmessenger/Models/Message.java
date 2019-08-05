package rd.sqllitepractice.btmessenger.Models;

import android.bluetooth.BluetoothDevice;


public class Message {

    String message;
    String timeStamp;
    boolean Is_sender;

    public Message(String message,String timeStamp, boolean is_sender) {
        this.message = message;
        this.timeStamp = timeStamp;
        Is_sender = is_sender;
    }

    public Message() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isIs_sender() {
        return Is_sender;
    }

    public void setIs_sender(boolean is_sender) {
        Is_sender = is_sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", Is_sender=" + Is_sender +
                '}';
    }
}
