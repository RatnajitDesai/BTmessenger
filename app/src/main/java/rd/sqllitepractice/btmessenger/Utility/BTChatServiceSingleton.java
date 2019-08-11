package rd.sqllitepractice.btmessenger.Utility;

import android.content.Context;
import android.os.Handler;

import rd.sqllitepractice.btmessenger.BTfuctions.BTChatService;

public class BTChatServiceSingleton {

    private static BTChatService obj;

    private BTChatServiceSingleton() {
    }

    public static BTChatService getInstance(Context context, Handler handler) {
        if (obj == null)
        {
            obj = new BTChatService(context, handler);
        }
        return obj;

    }
}
