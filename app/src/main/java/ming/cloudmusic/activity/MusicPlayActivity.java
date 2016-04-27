package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import ming.cloudmusic.R;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.CustomUtils;
import ming.cloudmusic.util.DateSDF;

public class MusicPlayActivity extends DrawableBaseActivity implements OnClickListener, OnSeekBarChangeListener {

    private TextView tvPlayinfo;
    private TextView tvPlayBack;
    private TextView tvPlaytitleArt;
    private TextView tvPlaytitleTitle;
    private TextView tvPlayorpasue;
    private TextView tvPlayPrev;
    private TextView tvPlayNext;
    private TextView tvPlayMode;
    private TextView tvPlayMenu;
    private SeekBar seekBar;
    private TextView tvAlltime;
    private TextView tvPlaytime;

    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplay);

        initView();

        EventBus.getDefault().register(this);

        postEventMsg(KeyEvent.GET_PLAYINGMUSIC);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initView() {

        tvPlayinfo = (TextView) findViewById(R.id.tv_playinfo);
        tvPlayBack = (TextView) findViewById(R.id.tv_play_back);
        tvPlaytitleArt = (TextView) findViewById(R.id.tv_playtitle_art);
        tvPlaytitleTitle = (TextView) findViewById(R.id.tv_playtitle_title);
        tvPlayorpasue = (TextView) findViewById(R.id.tv_play_playorpasue);
        tvPlayPrev = (TextView) findViewById(R.id.tv_play_prev);
        tvPlayNext = (TextView) findViewById(R.id.tv_play_next);
        tvPlayMode = (TextView) findViewById(R.id.tv_play_mode);
        tvPlayMenu = (TextView) findViewById(R.id.tv_play_menu);
        seekBar = (SeekBar) findViewById(R.id.seekbar_play);
        tvAlltime = (TextView) findViewById(R.id.tv_alltime);
        tvPlaytime = (TextView) findViewById(R.id.tv_playtime);

    }

    @Override
    public void initData() {

    }

    private void setListener() {
        tvPlayNext.setOnClickListener(this);
        tvPlayPrev.setOnClickListener(this);
        tvPlayorpasue.setOnClickListener(this);
        tvPlayMode.setOnClickListener(this);
        tvPlayMenu.setOnClickListener(this);
        tvPlayBack.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);
    }

    private void refreshView(Map data) {
        tvPlaytitleTitle.setText(data.get(Event.Extra.PLAYING_TITLE).toString());
        tvPlaytitleArt.setText(data.get(Event.Extra.PLAYING_ART).toString());
        tvPlaytime.setText(DateSDF.getSDF(data.get(Event.Extra.PLAYING_POINT)).toString());
        tvAlltime.setText(DateSDF.getSDF(data.get(Event.Extra.PLAYING_DURATION)).toString());
        duration = (int) data.get(Event.Extra.PLAYING_DURATION);
        setPlayModeIcon((int) data.get(Event.Extra.PLAY_MODE));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceEvent event) {

        String msg = event.getMsg();
        Map data = event.getExtras();

        switch (msg) {
            case ServiceEvent.SERVICE_PLAY:
                tvPlayorpasue.setBackgroundResource(R.drawable.play_btn_pause);
                break;
            case ServiceEvent.SERVICE_PAUSE:
                tvPlayorpasue.setBackgroundResource(R.drawable.play_btn_play);
                break;
            case ServiceEvent.SERVICE_BAR_CHANGE:
                int currentPosition = (int) data.get(Event.Extra.BAR_CHANGE);
                if (duration > 0) {
                    tvPlaytime.setText(DateSDF.getSDF(currentPosition).toString());
                    seekBar.setProgress(currentPosition * 100 / duration);
                }
                break;
            case ServiceEvent.SERVICE_PLAY_MODE:
                setPlayModeIcon((int) data.get(Event.Extra.PLAY_MODE));
                break;
            case ServiceEvent.SERVICE_POST_PLAYINGMUSIC:
                if (!tvPlayNext.hasOnClickListeners()) {
                    setListener();
                }
                refreshView(event.getExtras());
                break;
        }
    }

    private void setPlayModeIcon(int mode) {
        switch (mode) {
            case Constant.PlayMode.SINGLE:
                tvPlayMode.setBackgroundResource(R.drawable.play_icn_one);
                break;
            case Constant.PlayMode.RAMDOM:
                tvPlayMode.setBackgroundResource(R.drawable.play_icn_shuffle);
                break;
            case Constant.PlayMode.ALL:
                tvPlayMode.setBackgroundResource(R.drawable.play_icn_loop);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_play_playorpasue:
                postEventMsg(KeyEvent.PLAY_OR_PAUSE);
                break;
            case R.id.tv_play_next:
                postEventMsg(KeyEvent.NEXT);
                break;
            case R.id.tv_play_prev:
                postEventMsg(KeyEvent.PREVIOUS);
                break;
            case R.id.tv_play_mode:
                postEventMsg(KeyEvent.PLAY_MODE);
                break;
            case R.id.tv_play_back:
                finish();
                break;
            case R.id.tv_play_menu:
                CustomUtils.createPlayingMusicListDialog(mContext);
                break;
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

        mExtras.put(Event.Extra.BAR_CHANGE, seekBar.getProgress());
        postEventMsgHasExtra(KeyEvent.BAR_CHANGE, mExtras);

    }

}
