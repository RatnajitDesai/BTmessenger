package rd.sqllitepractice.btmessenger.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utility {
    private static final String TAG = "Utility";
    public static String getCurrentTimeStamp(){
        try{

            Calendar calendar = GregorianCalendar.getInstance() ;
            String hours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            String minutes = "";
            if (calendar.get(Calendar.MINUTE) < 10)
            {
                minutes = "0"+String.valueOf(calendar.get(Calendar.MINUTE));

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

}
