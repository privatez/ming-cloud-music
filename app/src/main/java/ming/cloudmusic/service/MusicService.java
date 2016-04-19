package ming.cloudmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ming.cloudmusic.db.MusicDao;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.MusicsManager;
import ming.cloudmusic.util.SharedPrefsUtil;

public class MusicService extends Service {

    private MediaPlayer mPlayer;

    private DbMusic mPlayingMusic;

    /**
     * 播放模式
     */
    private int mPlayingMode;
    /**
     * 正在播放的歌曲
     */
    private int mPlayingPosition;

    /**
     * 正在播放的列表歌曲数
     */
    private int musicsSize;

    /**
     * 播放进度
     */
    private int mPlayingProgress;

    private boolean isRunning;

    private SharedPrefsUtil mSharedPrefsUtil;
    private MusicsManager mMusicsManager;
    private Map mExtras;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mExtras = new HashMap<>();
        mMusicsManager = MusicsManager.getInstance();
        EventBus.getDefault().register(this);
        mSharedPrefsUtil = new SharedPrefsUtil(getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);
        mPlayingPosition = mSharedPrefsUtil.getIntSP(Constant.SharedPrefrence.PLAYING_POSITION, 0);
        mPlayingMode = mSharedPrefsUtil.getIntSP(Constant.SharedPrefrence.PLAYINT_MODE, 0);
        new InnerAsyncTask().execute();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        isRunning = false;
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private class InnerAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            mPlayingMusic = mMusicsManager.getOnPlayMusicByPosition(mPlayingPosition);
            musicsSize = mMusicsManager.getPlayingMusicsSize();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mPlayer = new MediaPlayer();
            isRunning = true;

            new UpdateSeekBarThread().start();

            mPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();
                }
            });
        }
    }

    private class UpdateSeekBarThread extends Thread {
        @Override
        public void run() {
            try {
                while (isRunning) {
                    if (mPlayer.isPlaying()) {
                        mExtras.put(Event.Extra.BAR_CHANGE, mPlayer.getCurrentPosition());
                        postEventMsgHasExtra(ServiceEvent.SERVICE_BAR_CHANGE, mExtras);
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onEventMainThread(KeyEvent event) {
        String msg = event.getMsg();
        switch (msg) {
            case KeyEvent.PLAY_OR_PAUSE:
                if (mPlayer.isPlaying())
                    pause();
                else
                    play();
                break;
            case KeyEvent.PREVIOUS:
                pause();
                left();
                break;
            case KeyEvent.NEXT:
                pause();
                right();
                break;
            case KeyEvent.PLAY_MODE:
                changePlayMode();
                break;
            case KeyEvent.BAR_CHANGE:
                int num = (int) event.getExtras().get(Event.Extra.BAR_CHANGE);
                mPlayingProgress = num * mPlayer.getDuration() / 100;
                play();
                break;
            case KeyEvent.GET_PLAYINGMUSIC:
                sendMusicInfo();
                break;
            case KeyEvent.PLAY_ALL:
                pause();
                mPlayingPosition = 0;
                play();
                break;
            case KeyEvent.PLAY_BY_POSITION:
                pause();
                mPlayingProgress = 0;
                mPlayingPosition = (int) event.getExtras().get(Event.Extra.PLAY_BY_POSITION);
                play();
                break;
        }
    }

    public void changePlayMode() {
        mPlayingMode++;
        if (mPlayingMode > Constant.PlayMode.ALL) {
            mPlayingMode = Constant.PlayMode.SINGLE;
        }

        mExtras.put(Event.Extra.PLAY_MODE, mPlayingMode);
        postEventMsgHasExtra(ServiceEvent.SERVICE_PLAY_MODE, mExtras);
        mSharedPrefsUtil.setIntSP(Constant.SharedPrefrence.PLAYINT_MODE, mPlayingMode);
    }

    public void sendMusicInfo() {

        if (mPlayingMusic != null) {

            mExtras.put(Event.Extra.PLAYING_POSITION, mPlayingPosition);
            mExtras.put(Event.Extra.PLAYING_TITLE, mPlayingMusic.getTitle());
            mExtras.put(Event.Extra.PLAYING_ART, mPlayingMusic.getArtlist());
            mExtras.put(Event.Extra.PLAYING_DURATION, mPlayingMusic.getDuration());
            mExtras.put(Event.Extra.PLAY_MODE, mPlayingMode);

            if (mPlayer.isPlaying()) {
                mExtras.put(Event.Extra.PLAYING_POINT, mPlayer.getCurrentPosition());
                postEventMsg(ServiceEvent.SERVICE_PLAY);
                //LogUtils.log("SERVICE_PLAY");
            } else {
                mExtras.put(Event.Extra.PLAYING_POINT, 0);
                postEventMsg(ServiceEvent.SERVICE_PAUSE);
                //LogUtils.log("SERVICE_PAUSE");
            }

        } else {
            return;
        }
        postEventMsgHasExtra(ServiceEvent.SERVICE_POST_PLAYINGMUSIC, mExtras);
    }

    public void right() {
        if (mPlayingMode == Constant.PlayMode.RAMDOM) {
            random();
        } else {
            mPlayingPosition++;
            if (mPlayingPosition == musicsSize) {
                mPlayingPosition = 0;
            }
            mPlayingProgress = 0;
            play();
        }
    }

    public void left() {
        if (mPlayingMode == Constant.PlayMode.RAMDOM) {
            random();
        } else {
            mPlayingPosition--;
            if (mPlayingPosition < 0) {
                mPlayingPosition = musicsSize - 1;
            }
            mPlayingProgress = 0;
            play();
        }
    }

    public void next() {
        MusicDao.getDefaultDao().insertHistoryMusics(mPlayingMusic, mPlayingPosition);
        switch (mPlayingMode) {
            case Constant.PlayMode.SINGLE:
                single();
                break;
            case Constant.PlayMode.RAMDOM:
                random();
                break;
            case Constant.PlayMode.ALL:
                all();
                break;
        }
    }

    private void all() {
        mPlayingPosition++;
        mPlayingProgress = 0;
        play();
    }

    private void random() {
        int num = mMusicsManager.getPositionByRandom(mPlayingPosition);
        mPlayingPosition = num;
        mPlayingProgress = 0;
        play();
    }

    private void single() {
        mPlayingProgress = 0;
        play();
    }

    private void pause() {
        if(mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayingProgress = mPlayer.getCurrentPosition();
        postEventMsg(ServiceEvent.SERVICE_PAUSE);
    }

    private void play() {
        mPlayingMusic = mMusicsManager.getOnPlayMusicByPosition(mPlayingPosition);
        mSharedPrefsUtil.setIntSP(Constant.SharedPrefrence.PLAYING_POSITION, mPlayingPosition);
        mSharedPrefsUtil.setLongSP(Constant.SharedPrefrence.PLAYING_ID,mPlayingMusic.getId());
        if (mPlayingMusic == null) {
            return;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mPlayingMusic.getPath());
            mPlayer.prepare();
            mPlayer.seekTo(mPlayingProgress);
            mPlayer.start();
            mPlayingProgress = 0;
            sendMusicInfo();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postEventMsg(String eventMsg) {
        EventUtil.getDefault().postSerEvent(eventMsg);
    }

    private void postEventMsgHasExtra(String eventMsg, Map extars) {
        EventUtil.getDefault().postSerEventHasExtra(eventMsg, extars);
    }

}
