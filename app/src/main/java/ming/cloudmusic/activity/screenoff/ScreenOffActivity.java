package ming.cloudmusic.activity.screenoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import ming.cloudmusic.R;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.CustomUtils;

/**
 * Created by lihaiye on 16/4/26.
 */
public class ScreenOffActivity extends SwipeBackActivity implements OnClickListener {

    private static final int REFRE_VIEW = 1;

    private SwipeBackLayout mSwipeBackLayout;

    private TextView tvLocalTime;
    private TextView tvLocalData;
    private TextView tvMusicTitle;
    private TextView tvMusicArt;
    private ImageView ivPrev;
    private ImageView ivPlayorpause;
    private ImageView ivNext;

    private Timer mTimer;
    private Handler mHandler;
    private InterceptKeyEventReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenoff);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        CustomUtils.setTranslucent(this);

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setSwipeMode(SwipeBackLayout.FULL_SCREEN_LEFT);

        initView();
        initReceiver();

        EventBus.getDefault().register(this);

        postEventMsg(KeyEvent.GET_PLAYINGMUSIC);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == REFRE_VIEW) {
                    refreViewByDate();
                }
            }
        };

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(REFRE_VIEW);
            }
        }, 0, 1000);

    }

    private void initView() {
        tvLocalTime = (TextView) findViewById(R.id.tv_local_time);
        tvLocalData = (TextView) findViewById(R.id.tv_local_data);
        tvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
        tvMusicArt = (TextView) findViewById(R.id.tv_music_art);
        ivPrev = (ImageView) findViewById(R.id.iv_prev);
        ivPlayorpause = (ImageView) findViewById(R.id.iv_playorpause);
        ivNext = (ImageView) findViewById(R.id.iv_next);

        ivPrev.setOnClickListener(this);
        ivPlayorpause.setOnClickListener(this);
        ivNext.setOnClickListener(this);
    }

    private void initReceiver() {
        mReceiver = new InterceptKeyEventReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(2147483647);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mReceiver, filter);
    }

    private void refreshView(Map data) {
        tvMusicTitle.setText(data.get(Event.Extra.PLAYING_TITLE).toString());
        tvMusicArt.setText(data.get(Event.Extra.PLAYING_ART).toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceEvent event) {
        String msg = event.getMsg();
        switch (msg) {
            case ServiceEvent.SERVICE_PLAY:
                ivPlayorpause.setImageResource(R.drawable.lock_btn_pause);
                break;
            case ServiceEvent.SERVICE_PAUSE:
                ivPlayorpause.setImageResource(R.drawable.lock_btn_play);
                break;
            case ServiceEvent.SERVICE_POST_PLAYINGMUSIC:
                refreshView(event.getExtras());
                break;
        }
    }

    private void refreViewByDate() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        tvLocalTime.setText(getAppendString(hourOfDay) + ":" + getAppendString(minute));
        tvLocalData.setText(month + "月" + getAppendString(dayOfMonth) + "日 " + "星期" + Constant.WEEK_STRING[weekDay]);
    }

    private String getAppendString(int num) {
        StringBuffer str = new StringBuffer();
        if (num < 10) {
            str.append("0");
        }
        str.append(num);
        return str.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mTimer.cancel();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_playorpause:
                postEventMsg(KeyEvent.PLAY_OR_PAUSE);
                break;
            case R.id.iv_next:
                postEventMsg(KeyEvent.NEXT);
                break;
            case R.id.iv_prev:
                postEventMsg(KeyEvent.PREVIOUS);
                break;
        }
    }

    private void postEventMsg(String eventMsg) {
        EventUtil.getDefault().postKeyEvent(eventMsg);
    }

    public class InterceptKeyEventReceiver extends BroadcastReceiver {

        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    abortBroadcast();
                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    abortBroadcast();
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    abortBroadcast();
                }
            }
        }
    }
}
