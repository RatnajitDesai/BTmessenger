package rd.sqllitepractice.btmessenger.Models;

import android.graphics.Bitmap;
import android.net.Uri;

public class Message {

    private String message;
    private String timeStamp;
    private boolean Is_sender;
    private boolean image;
    private Uri uri;
    private Bitmap bitmap;

    public Message(String message, String timeStamp, boolean is_sender, boolean image) {
        this.message = message;
        this.timeStamp = timeStamp;
        Is_sender = is_sender;
        this.image = image;
    }

    public Message(Uri uri, String timeStamp, boolean is_sender, boolean image) {
        this.uri = uri;
        this.timeStamp = timeStamp;
        Is_sender = is_sender;
        this.image = image;
    }

    public Message(Bitmap bitmap, String timeStamp, boolean is_sender, boolean image) {
        this.bitmap = bitmap;
        this.timeStamp = timeStamp;
        Is_sender = is_sender;
        this.image = image;
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

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", Is_sender=" + Is_sender +
                ", image=" + image +
                ", uri=" + uri +
                ", bitmap=" + bitmap +
                '}';
    }
}
