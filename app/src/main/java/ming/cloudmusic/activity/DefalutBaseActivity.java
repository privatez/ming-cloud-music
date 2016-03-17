package ming.cloudmusic.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;

import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.SharedPrefsUtil;

/**
 * Created by Lhy on 2015/12/22.
 */
public class DefalutBaseActivity extends Activity {

    protected HashMap mExtras;

    protected Context mContext;

    protected SharedPrefsUtil mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtras = new HashMap();
        mContext = this;
        mSharedPrefs = new SharedPrefsUtil(getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);
    }

    protected void postEventMsg(String msg) {
        EventUtil.getDefault().postEventMsg(msg, EventUtil.KEY);
    }

    protected void postEventMsgHasExtra(String msg, HashMap mExtras) {
        EventUtil.getDefault().postEventMsgHasExtra(msg, mExtras, EventUtil.KEY);
    }

}
