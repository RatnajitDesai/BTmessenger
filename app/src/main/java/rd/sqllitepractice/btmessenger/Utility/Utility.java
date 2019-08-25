package rd.sqllitepractice.btmessenger.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Utility {
    private static final String TAG = "Utility";
    public static String getCurrentTimeStamp(){
        try{

            Calendar calendar = GregorianCalendar.getInstance() ;
            String hours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            String minutes;
            if (calendar.get(Calendar.MINUTE) < 10)
            {
                minutes = "0" + calendar.get(Calendar.MINUTE);

            }
            else {
                minutes = String.valueOf(calendar.get(Calendar.MINUTE));
            }

            return hours+":"+minutes+"";

        }
        catch (Exception e){

            return null;
        }
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
