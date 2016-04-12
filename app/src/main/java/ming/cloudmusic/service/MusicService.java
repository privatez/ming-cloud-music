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
import java.util.ArrayList;
import java.util.HashMap;

import ming.cloudmusic.db.MusicDao;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.event.model.ServiceEvent;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.MusicsManager;

public class MusicService extends Service {

    private MediaPlayer mPlayer;

    private DbMusic mPlayingMusic;
    private ArrayList<DbMusic> historyMusics;

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
    private int mPlayingPoint;

    private boolean isRunning;

    private MusicsManager mMusicsManager;
    private HashMap mExtras;

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
            historyMusics = new ArrayList<>();

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
                        mExtras.clear();
                        mExtras.put(Event.Extra.EXTRA_BAR_CHANGE, mPlayer.getCurrentPosition());
                        postEventMsgHasExtra(ServiceEvent.SERVICE_BAR_CHANGE);
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
            case KeyEvent.KEY_PLAY_OR_PAUSE:
                if (mPlayer.isPlaying())
                    pause();
                else
                    play();
                break;
            case KeyEvent.KEY_PREVIOUS:
                pause();
                right();
                break;
            case KeyEvent.KEY_NEXT:
                pause();
                left();
                break;
            case KeyEvent.KEY_PLAY_MODE:
                changePlayMode();
                break;
            case KeyEvent.KEY_BAR_CHANGE:
                int num = (int) event.getExtras().get(Event.Extra.EXTRA_BAR_CHANGE);
                mPlayingPoint = num * mPlayer.getDuration() / 100;
                play();
                break;
            case KeyEvent.KEY_GET_PLAYINGMUSIC:
                sendMusicInfo();
                break;
            /*else if (INTENT_ACTION_CLICKPLAY.equals(action)) {
                long songId = intent.getLongExtra(INTENT_ACTION_CLICKPLAY_DATA,
						0);
				mPlayingPosition = app.getOnPlayMusicById(songId);
				play();
			}*/
        }
    }

    public void changePlayMode() {
        mPlayingMode++;
        if (mPlayingMode > 2) {
            mPlayingMode = 0;
        }

        mExtras.put(Event.Extra.EXTRA_PLAY_MODE, mPlayingMode);
        postEventMsgHasExtra(ServiceEvent.SERVICE_PLAY_MODE);

    }

    public void sendMusicInfo() {

        if (mPlayingMusic != null) {

            mExtras.put(Event.Extra.EXTRA_PLAYING_POSITION, mPlayingPosition);
            mExtras.put(Event.Extra.EXTRA_PLAYING_TITLE, mPlayingMusic.getTitle());
            mExtras.put(Event.Extra.EXTRA_PLAYING_ART, mPlayingMusic.getArtlist());
            mExtras.put(Event.Extra.EXTRA_PLAYING_DURATION, mPlayingMusic.getDuration());

            if (mPlayer.isPlaying()) {
                mExtras.put(Event.Extra.EXTRA_PLAYING_POINT, mPlayer.getCurrentPosition());
                postEventMsg(ServiceEvent.SERVICE_PLAY);
                //LogUtils.log("SERVICE_PLAY");
            } else {
                mExtras.put(Event.Extra.EXTRA_PLAYING_POINT, 0);
                postEventMsg(ServiceEvent.SERVICE_PAUSE);
                //LogUtils.log("SERVICE_PAUSE");
            }

        } else {
            return;
        }
        postEventMsgHasExtra(ServiceEvent.SERVICE_POST_PLAYINGMUSIC);
    }

    public void right() {
        if (mPlayingMode == 1) {
            random();
        } else {
            mPlayingPosition++;
            if (mPlayingPosition == musicsSize) {
                mPlayingPosition = 0;
            }
            mPlayingPoint = 0;
            play();
        }
    }

    public void left() {
        if (mPlayingMode == 1) {
            random();
        } else {
            mPlayingPosition--;
            if (mPlayingPosition < 0) {
                mPlayingPosition = musicsSize - 1;
            }
            mPlayingPoint = 0;
            play();
        }
    }

    public void next() {
        historyMusics.clear();
        //historyMusics.add(mPlayingMusic);
        mPlayingMusic.setIsHistroy(DbMusic.ISHISTORY);
        mPlayingMusic.setPlayedTime(System.currentTimeMillis());
        MusicDao.getDefaultDao().insertHistoryMusics(mPlayingMusic);
        switch (mPlayingMode) {
            case 0:
                single();
                break;
            case 1:
                random();
                break;
            case 2:
                all();
                break;
        }
    }

    private void all() {
        mPlayingPosition++;
        mPlayingPoint = 0;
        play();
    }

    private void random() {
        int num = mMusicsManager.getPositionByRandom(mPlayingPosition);
        mPlayingPosition = num;
        mPlayingPoint = 0;
        play();
    }

    private void single() {
        mPlayingPoint = 0;
        play();
    }

    private void pause() {
        mPlayer.stop();
        mPlayingPoint = mPlayer.getCurrentPosition();
        postEventMsg(ServiceEvent.SERVICE_PAUSE);
    }

    private void play() {
        mPlayingMusic = mMusicsManager.getOnPlayMusicByPosition(mPlayingPosition);
        if (mPlayingMusic == null) {
            return;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mPlayingMusic.getPath());
            mPlayer.prepare();
            mPlayer.seekTo(mPlayingPoint);
            mPlayer.start();
            mPlayingPoint = 0;
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

    private void postEventMsg(String msg) {
        EventUtil.getDefault().postEventMsg(msg, EventUtil.SER);
    }

    private void postEventMsgHasExtra(String msg) {
        EventUtil.getDefault().postEventMsgHasExtra(msg, mExtras, EventUtil.SER);
    }

}
