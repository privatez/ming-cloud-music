package ming.cloudmusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import ming.cloudmusic.R;
import ming.cloudmusic.activity.BaseActivity.DefalutBaseActivity;
import ming.cloudmusic.event.model.MusicEvent;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtils;

public class MusicPlayActivity extends DefalutBaseActivity implements OnClickListener,
        Constant, OnSeekBarChangeListener {

    private TextView tvPlayinfo;
    private TextView tvPlayBack;
    private TextView tvPlaytitleArt;
    private TextView tvPlaytitleTitle;
    private TextView tvPlayPlayorpasue;
    private TextView tvPlayPrev;
    private TextView tvPlayNext;
    private TextView tvPlayMode;
    private TextView tvPlayMenu;
    private SeekBar seekBar1;
    private TextView tvAlltime;
    private TextView tvPlaytime;

    private int duration;
    private int musicFlag;
    private int modeFlag;

    private boolean mIsPlaying;

    private HashMap mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplay);

        mIsPlaying = false;

        mExtras = new HashMap();

        initView();

        EventBus.getDefault().register(this);

        EventUtils.getDefault().postEventMsg(Event.EVENT_MSG_GETMUSICFLAG);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {

        tvPlayinfo = (TextView) findViewById(R.id.tv_playinfo);
        tvPlayBack = (TextView) findViewById(R.id.tv_play_back);
        tvPlaytitleArt = (TextView) findViewById(R.id.tv_playtitle_art);
        tvPlaytitleTitle = (TextView) findViewById(R.id.tv_playtitle_title);
        tvPlayPlayorpasue = (TextView) findViewById(R.id.tv_play_playorpasue);
        tvPlayPrev = (TextView) findViewById(R.id.tv_play_prev);
        tvPlayNext = (TextView) findViewById(R.id.tv_play_next);
        tvPlayMode = (TextView) findViewById(R.id.tv_play_mode);
        tvPlayMenu = (TextView) findViewById(R.id.tv_play_menu);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        tvAlltime = (TextView) findViewById(R.id.tv_alltime);
        tvPlaytime = (TextView) findViewById(R.id.tv_playtime);

        tvPlayNext.setOnClickListener(this);
        tvPlayPrev.setOnClickListener(this);
        tvPlayPlayorpasue.setOnClickListener(this);
        tvPlayMode.setOnClickListener(this);
        tvPlayMenu.setOnClickListener(this);
        tvPlayBack.setOnClickListener(this);

        seekBar1.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()) {
            case R.id.tv_play_playorpasue:
                if(mIsPlaying){
                    postEventMsg(Event.KeyEvent.KEY_PLAY);
                } else {
                    postEventMsg(Event.KeyEvent.KEY_PAUSE);
                }
                break;
            case R.id.tv_play_next:
                postEventMsg(Event.KeyEvent.KEY_NEXT);
                break;
            case R.id.tv_play_prev:
                postEventMsg(Event.KeyEvent.KEY_PREVIOUS);
                break;
            case R.id.tv_play_mode:
                postEventMsg(Event.KeyEvent.KEY_PLAY_MODE);
                break;
            case R.id.tv_play_back:
                finish();
                break;
            case R.id.tv_play_menu:

                break;
        }
    }

    @Subscribe
    private void onEventMainThread(MusicEvent event) {
        String msg = event.getMsg();
        switch (msg) {
            case Event.ServiceEvent.SERVICE_PLAY:
                bnPlayOrPause.setBackgroundResource(R.drawable.play_btn_pause);
                musicFlag = intent.getIntExtra(INTENT_ACTION_IS_PLAY_DATA, -1);
                tvtitleText = intent
                        .getStringExtra(INTENT_ACTION_IS_PLAY_TITLEDATA);
                tvartText = intent
                        .getStringExtra(INTENT_ACTION_IS_PLAY_ARTDATA);
                duration = intent.getIntExtra(
                        INTENT_ACTION_IS_PLAY_DURATIONDATA, 0);
                if (musicFlag != -1) {
                    tvtitle.setText(tvtitleText);
                    tvart.setText(tvartText);
                    tvDuration.setText(DateSDF.getSDF(duration) + "");
                }
                break;
        }
    }

    private class MyMReceiver extends BroadcastReceiver {
        String tvtitleText = "";
        String tvartText = "";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (INTENT_ACTION_PLAY_BN.equals(action)) {

            } else if (INTENT_ACTION_PAUSE_BN.equals(action)) {
                bnPlayOrPause.setBackgroundResource(R.drawable.play_btn_play);
            } else if (INTENT_ACTION_SEEKBAR.equals(action)) {
                int currentPosition = intent.getIntExtra(
                        INTENT_ACTION_SEEKBAR_DATA, 1);
                if (duration > 0) {
                    tvCurrentPosition.setText(DateSDF.getSDF(currentPosition)
                            .toString());
                    sbProgress.setProgress(currentPosition * 100 / duration);
                }
            } else if (INTENT_ACTION_PLAY_MODE_BN.equals(action)) {
                modeFlag = intent.getIntExtra(INTENT_ACTION_PLAY_MODE_DATA, 1);
                switch (modeFlag) {
                    case 2:
                        bnPlayMode.setBackgroundResource(R.drawable.play_icn_loop);
                        break;
                    case 1:
                        bnPlayMode
                                .setBackgroundResource(R.drawable.play_icn_shuffle);
                        break;
                    case 0:
                        bnPlayMode.setBackgroundResource(R.drawable.play_icn_one);
                        break;
                }
            } else if (INTENT_ACTION_SENDMUSICFLAG.equals(action)) {
                setListener();
                musicFlag = intent.getIntExtra(INTENT_ACTION_IS_PLAY_DATA, -1);
                tvtitleText = intent
                        .getStringExtra(INTENT_ACTION_IS_PLAY_TITLEDATA);
                tvartText = intent
                        .getStringExtra(INTENT_ACTION_IS_PLAY_ARTDATA);
                duration = intent.getIntExtra(
                        INTENT_ACTION_IS_PLAY_DURATIONDATA, 0);
                if (musicFlag != -1) {
                    tvtitle.setText(tvtitleText);
                    tvart.setText(tvartText);
                    tvDuration.setText(DateSDF.getSDF(duration) + "");
                }
                int num = intent.getIntExtra(INTENT_ACTION_SENDMUSICFLAG_BN, 0);
                if (num == 0) {
                    bnPlayOrPause
                            .setBackgroundResource(R.drawable.play_btn_pause);
                } else {
                    bnPlayOrPause
                            .setBackgroundResource(R.drawable.play_btn_play);
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        mExtras.clear();
        mExtras.put(Event.Extra.EXTRA_BAR_CHANGE, seekBar.getProgress());
        postEventMsgHasExtra(Event.KeyEvent.KEY_BAR_CHANGE,mExtras);

    }

}
