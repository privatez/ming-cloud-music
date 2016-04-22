package ming.cloudmusic.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.simonvt.menudrawer.MenuDrawer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import ming.cloudmusic.R;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.fragment.MyMusicFragment;
import ming.cloudmusic.util.FragmentTaskManager;
import ming.cloudmusic.view.MenuDrawerHelper;

public class CloudMusicMainActivity extends FragmentActivity implements View.OnClickListener, OnTouchListener {

    private RelativeLayout rlPlaybar;
    private ImageView ivPlaybarNext;
    private ImageView ivPlaybarPlayorpause;
    private TextView tvPlaybarTitle;
    private TextView tvPlaybarArt;

    private MenuDrawerHelper mDrawerHelper;

    private float startX;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerHelper = new MenuDrawerHelper(this, new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(View v, int delta, int x, int y) {
                return false;
            }
        });

        initView();
        initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void initView() {
        rlPlaybar = (RelativeLayout) findViewById(R.id.rl_playbar);
        ivPlaybarNext = (ImageView) findViewById(R.id.iv_playbar_next);
        ivPlaybarPlayorpause = (ImageView) findViewById(R.id.iv_playbar_playorpause);
        tvPlaybarTitle = (TextView) findViewById(R.id.tv_playbar_title);
        tvPlaybarArt = (TextView) findViewById(R.id.tv_playbar_art);

        ivPlaybarNext.setOnClickListener(this);
        ivPlaybarPlayorpause.setOnClickListener(this);
        rlPlaybar.setOnTouchListener(this);
    }


    public void initData() {
        mContext = this;

        EventBus.getDefault().register(this);
        FragmentTaskManager.getInstance().bind(getSupportFragmentManager(), R.id.fl_content);

        addFragmentToContent();
        postEventMsg(KeyEvent.GET_PLAYINGMUSIC);
    }

    private void refreshView(HashMap data) {
        rlPlaybar.setVisibility(View.VISIBLE);
        tvPlaybarTitle.setText(data.get(Event.Extra.PLAYING_TITLE).toString());
        tvPlaybarArt.setText(data.get(Event.Extra.PLAYING_ART).toString());
    }

    private void addFragmentToContent() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_content, new MyMusicFragment()).addToBackStack(MyMusicFragment.class.getSimpleName()).commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(KeyEvent event) {
        switch (event.getMsg()) {
            case KeyEvent.TOGGLE_MENU:
                mDrawerHelper.toggleMenu();
                break;
            case KeyEvent.POST_MILLISUNTILFINISHED:
                mDrawerHelper.setTimeText((Long) event.getExtras().get(Event.Extra.TIMINGPLAY_TIME),
                        (int) event.getExtras().get(Event.Extra.TIMINGPLAY_CHECK_POSITION));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceEvent event) {
        String msg = event.getMsg();
        Map data = event.getExtras();

        switch (msg) {
            case ServiceEvent.SERVICE_PLAY:
                ivPlaybarPlayorpause.setImageResource(R.drawable.playbar_btn_pause);
                break;
            case ServiceEvent.SERVICE_PAUSE:
                ivPlaybarPlayorpause.setImageResource(R.drawable.playbar_btn_play);
                break;
            case ServiceEvent.SERVICE_BAR_CHANGE:
               /* int currentPosition = (int) data.get(Event.Extra.BAR_CHANGE);
                if (duration > 0) {
                    tvPlaytime.setText(DateSDF.getSDF(currentPosition).toString());
                    seekBar.setProgress(currentPosition * 100 / duration);
                }*/
                break;
            case ServiceEvent.SERVICE_POST_PLAYINGMUSIC:
                refreshView(event.getExtras());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_playbar_next:
                postEventMsg(KeyEvent.NEXT);
                break;
            case R.id.iv_playbar_playorpause:
                postEventMsg(KeyEvent.PLAY_OR_PAUSE);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        //获得当前事件由几个手指触发
        int howmuch = e.getPointerCount();
        if (howmuch == 1) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                startX = e.getX();
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                if (e.getX() - startX > 100) {
                    //滑动 下一首
                    postEventMsg(KeyEvent.NEXT);
                } else if (e.getX() - startX < -100) {
                    //滑动 上一首
                    postEventMsg(KeyEvent.PREVIOUS);
                } else if (e.getX() == startX) {
                    //长按 进入播放界面
                    mContext.startActivity(new Intent(mContext, MusicPlayActivity.class));
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (mDrawerHelper.isMenuOpened())
                mDrawerHelper.closeMenu();
            else
                finish();
        }
    }

    private void postEventMsg(String eventMsg) {
        EventUtil.getDefault().postKeyEvent(eventMsg);
    }

    private void postEventMsgHasExtra(String eventMsg, Map extras) {
        EventUtil.getDefault().postKeyEventHasExtra(eventMsg, extras);
    }

}
