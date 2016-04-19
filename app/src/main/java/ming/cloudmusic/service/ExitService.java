package ming.cloudmusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.util.LogUtils;

/**
 * Created by Lhy on 2016/4/14.
 */
public class ExitService extends Service {

    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_CHECK_POSITION = "position";

    private static final long INTERVAL = 1000;

    private CountDownTimer mCountDownTimer;

    private Map mExtra;

    private int mCheckPosition;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtils.log("ExitService onCreate");
        mExtra = new HashMap();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timingPlay(intent.getLongExtra(EXTRA_TIME, 0));
        mCheckPosition = intent.getIntExtra(EXTRA_CHECK_POSITION, 0);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void timingPlay(long millisInFuture) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (millisInFuture == 0) {
            postData(millisInFuture);
            return;
        }

        mCountDownTimer = new CountDownTimer(millisInFuture, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                postData(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                System.exit(0);
            }
        }.start();
    }

    private void postData(long millisUntilFinished) {
        mExtra.put(Event.Extra.TIMINGPLAY_TIME, millisUntilFinished);
        mExtra.put(Event.Extra.TIMINGPLAY_CHECK_POSITION, mCheckPosition);
        EventUtil.getDefault().postKeyEventHasExtra(KeyEvent.POST_MILLISUNTILFINISHED, mExtra);
    }
}
