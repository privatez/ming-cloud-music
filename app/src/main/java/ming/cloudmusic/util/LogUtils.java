package ming.cloudmusic.util;

import android.util.Log;

/**
 * Created by Lhy on 2015/12/22.
 */
public class LogUtils {

    public static final String TAG = "lhy";

    public static boolean isDebug = true;

    public static void log(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void log(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }
}
