package ming.cloudmusic.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
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

import ming.cloudmusic.R;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.fragment.HistoryFragment;
import ming.cloudmusic.fragment.MyMusicFragment;
import ming.cloudmusic.view.MenuDrawerHelper;

public class CloudMusicMainActivity extends DefalutBaseActivity implements View.OnClickListener, OnTouchListener {

    private RelativeLayout rlPlaybar;
    private ImageView ivPlaybarNext;
    private ImageView ivPlaybarPlayorpause;
    private TextView tvPlaybarTitle;
    private TextView tvPlaybarArt;

    private Fragment mContent;

    private MyMusicFragment mMyMusicFragment;
    private HistoryFragment mHistoryFragment;

    private MenuDrawerHelper mDrawerHelper;

    private float startX;

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

    @Override
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

    @Override
    public void initData() {
        mContent = new Fragment();
        mMyMusicFragment = new MyMusicFragment();
        switchContent(mContent, mMyMusicFragment);

        mHistoryFragment = new HistoryFragment();

        EventBus.getDefault().register(this);

        postEventMsg(KeyEvent.KEY_GET_PLAYINGMUSIC);
    }

    private void refreshView(HashMap data) {
        rlPlaybar.setVisibility(View.VISIBLE);
        tvPlaybarTitle.setText(data.get(Event.Extra.EXTRA_PLAYING_TITLE).toString());
        tvPlaybarArt.setText(data.get(Event.Extra.EXTRA_PLAYING_ART).toString());
    }

    private void switchContent(Fragment oldFragment, Fragment newFragment) {
        if (mContent != newFragment) {
            mContent = newFragment;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (!newFragment.isAdded())
                transaction.hide(oldFragment).add(R.id.ll_content, newFragment).commit();
            else
                transaction.hide(oldFragment).show(newFragment).commit();
        }
    }

    private void onBackFragment(Fragment showedFragment, Fragment showingFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (showedFragment.isAdded() && showingFragment.isAdded())
            transaction.hide(showedFragment).show(showingFragment).remove(showedFragment).commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(KeyEvent event) {
        switch (event.getMsg()) {
            case KeyEvent.KEY_TOGGLE_MENU:
                mDrawerHelper.toggleMenu();
                break;
            case KeyEvent.KEY_ACTION_HISTORYMUSIC:
                switchContent(mMyMusicFragment, mHistoryFragment);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceEvent event) {
        String msg = event.getMsg();
        HashMap data = event.getExtras();

        switch (msg) {
            case ServiceEvent.SERVICE_PLAY:
                ivPlaybarPlayorpause.setImageResource(R.drawable.playbar_btn_pause);
                break;
            case ServiceEvent.SERVICE_PAUSE:
                ivPlaybarPlayorpause.setImageResource(R.drawable.playbar_btn_play);
                break;
            case ServiceEvent.SERVICE_BAR_CHANGE:
               /* int currentPosition = (int) data.get(Event.Extra.EXTRA_BAR_CHANGE);
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
                postEventMsg(KeyEvent.KEY_NEXT);
                break;
            case R.id.iv_playbar_playorpause:
                postEventMsg(KeyEvent.KEY_PLAY_OR_PAUSE);
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
                    postEventMsg(KeyEvent.KEY_NEXT);
                } else if (e.getX() - startX < -100) {
                    //滑动 上一首
                    postEventMsg(KeyEvent.KEY_PREVIOUS);
                } else if (e.getX() == startX) {
                    //长按 进入播放界面
                    mContent.startActivity(new Intent(mContext, MusicPlayActivity.class));
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerHelper.isMenuOpened())
            mDrawerHelper.closeMenu();
        else
            super.onBackPressed();
    }

}
