package ming.cloudmusic.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.SharedPrefsUtil;
import ming.cloudmusic.view.OnViewCreateListener;

/**
 * Created by Lhy on 2015/12/22.
 */
public abstract class DefalutBaseActivity extends Activity implements OnViewCreateListener {

    protected Map mExtras;

    protected Context mContext;

    protected SharedPrefsUtil mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtras = new HashMap();
        mContext = this;
        mSharedPrefs = new SharedPrefsUtil(getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);

    }

    protected void postEventMsg(String eventMsg) {
        EventUtil.getDefault().postKeyEvent(eventMsg);
    }

    protected void postEventMsgHasExtra(String eventMsg, Map extras) {
        EventUtil.getDefault().postKeyEventHasExtra(eventMsg, extras);
    }

    protected int getResColor(int color) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(mContext, color);
        } else {
            return getResources().getColor(color);
        }
    }

}
