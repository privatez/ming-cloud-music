package ming.cloudmusic.activity.BaseActivity;

import android.app.Activity;
import android.os.Bundle;

import java.util.HashMap;

import ming.cloudmusic.event.EventUtils;

/**
 * Created by Lhy on 2015/12/22.
 */
public class DefalutBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void postEventMsg(String msg) {
        EventUtils.getDefault().postEventMsg(msg);
    }

    protected void postEventMsgHasExtra(String msg,HashMap extras) {
        EventUtils.getDefault().postEventMsgHasExtra(msg,extras);
    }

}
