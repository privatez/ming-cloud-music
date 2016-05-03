package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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

    private TextView tvPlaytitleArt;
    private TextView tvPlaytitleTitle;
    private TextView tvPlaytime;
    private SeekBar seekbarPlay;
    private TextView tvAlltime;
    private ImageView ivPlayorpasue;
    private ImageView ivMode;

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

        tvPlaytitleArt = (TextView) findViewById(R.id.tv_playtitle_art);
        tvPlaytitleTitle = (TextView) findViewById(R.id.tv_playtitle_title);
        tvPlaytime = (TextView) findViewById(R.id.tv_playtime);
        seekbarPlay = (SeekBar) findViewById(R.id.seekbar_play);
        tvAlltime = (TextView) findViewById(R.id.tv_alltime);
        ivPlayorpasue = (ImageView) findViewById(R.id.iv_playorpasue);
        ivMode = (ImageView) findViewById(R.id.iv_mode);

    }

    @Override
    public void initData() {

    }

    private void setListener() {
        ivMode.setOnClickListener(this);
        ivPlayorpasue.setOnClickListener(this);

        findViewById(R.id.iv_next).setOnClickListener(this);
        findViewById(R.id.iv_prev).setOnClickListener(this);
        findViewById(R.id.iv_menu).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

        seekbarPlay.setOnSeekBarChangeListener(this);
    }

    private void refreshView(Map data) {
        tvPlaytitleTitle.setText(data.get(Event.Extra.PLAYING_TITLE).toString());
        tvPlaytitleArt.setText(data.get(Event.Extra.PLAYING_ART).toString());
        tvPlaytime.setText(DateSDF.getDefaultSDF(data.get(Event.Extra.PLAYING_POINT)).toString());
        tvAlltime.setText(DateSDF.getDefaultSDF(data.get(Event.Extra.PLAYING_DURATION)).toString());
        duration = (int) data.get(Event.Extra.PLAYING_DURATION);
        setPlayModeIcon((int) data.get(Event.Extra.PLAY_MODE));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceEvent event) {

        String msg = event.getMsg();
        Map data = event.getExtras();

        switch (msg) {
            case ServiceEvent.SERVICE_PLAY:
                ivPlayorpasue.setImageResource(R.drawable.play_btn_pause);
                break;
            case ServiceEvent.SERVICE_PAUSE:
                ivPlayorpasue.setImageResource(R.drawable.play_btn_play);
                break;
            case ServiceEvent.SERVICE_BAR_CHANGE:
                int currentPosition = (int) data.get(Event.Extra.BAR_CHANGE);
                if (duration > 0) {
                    tvPlaytime.setText(DateSDF.getDefaultSDF(currentPosition).toString());
                    seekbarPlay.setProgress(currentPosition * 100 / duration);
                }
                break;
            case ServiceEvent.SERVICE_PLAY_MODE:
                setPlayModeIcon((int) data.get(Event.Extra.PLAY_MODE));
                break;
            case ServiceEvent.SERVICE_POST_PLAYINGMUSIC:
                if (!ivPlayorpasue.hasOnClickListeners()) {
                    setListener();
                }
                refreshView(event.getExtras());
                break;
        }
    }

    private void setPlayModeIcon(int mode) {
        switch (mode) {
            case Constant.PlayMode.SINGLE:
                ivMode.setImageResource(R.drawable.play_icn_one);
                break;
            case Constant.PlayMode.RAMDOM:
                ivMode.setImageResource(R.drawable.play_icn_shuffle);
                break;
            case Constant.PlayMode.ALL:
                ivMode.setImageResource(R.drawable.play_icn_loop);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_playorpasue:
                postEventMsg(KeyEvent.PLAY_OR_PAUSE);
                break;
            case R.id.iv_next:
                postEventMsg(KeyEvent.NEXT);
                break;
            case R.id.iv_prev:
                postEventMsg(KeyEvent.PREVIOUS);
                break;
            case R.id.iv_mode:
                postEventMsg(KeyEvent.PLAY_MODE);
                break;
            case R.id.iv_menu:
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
