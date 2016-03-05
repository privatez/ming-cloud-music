package ming.cloudmusic.service;

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

import ming.cloudmusic.MusicPlayerApplication;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtils;
import ming.cloudmusic.event.model.MusicEvent;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.Constant;

public class MusicService extends android.app.Service implements Constant {

    private MediaPlayer mPlayer;

    private DbMusic music;
    private ArrayList<DbMusic> historyMusics;

    /**
     * 当前播放模式
     */
    private int mPlayingMode;
    /**
     * 标记
     */
    private int mPlayingMusic;

    private int musicsSize;

    private int musicPosition;
    private boolean isRunning;

    private MusicPlayerApplication app;
    private HashMap mExtras;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mExtras = new HashMap<>();
        app = (MusicPlayerApplication) getApplication();
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
            music = app.getOnPlayMusicByFlag(mPlayingMusic);
            musicsSize = app.getOnPlaySize();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            historyMusics = new ArrayList<>();

            mPlayer = new MediaPlayer();
            isRunning = true;

            // 开启更新进度条子线程
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
                        postEventMsgHasExtra(Event.Service.SERVICE_BAR_CHANGE);
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    private void onEventMainThread(MusicEvent event) {
        String msg = event.getMsg();
        switch (msg) {
            case Event.KeyEvent.KEY_PLAY:
                play();
                break;
            case Event.KeyEvent.KEY_PAUSE:
                pause();
                break;
            case Event.KeyEvent.KEY_PREVIOUS:
                pause();
                right();
                break;
            case Event.KeyEvent.KEY_NEXT:
                pause();
                left();
                break;
            case Event.KeyEvent.KEY_PLAY_MODE:
                changePlayMode();
                break;
            case Event.KeyEvent.KEY_BAR_CHANGE:
                int num = (int) event.getExtras().get(Event.Extra.EXTRA_BAR_CHANGE);
                musicPosition = num * mPlayer.getDuration() / 100;
                play();
                break;
            case Event.KeyEvent.KEY_GET_PLAYINGMUSIC:
                sendMusicInfo();
                break;
            /*else if (INTENT_ACTION_CLICKPLAY.equals(action)) {
				long songId = intent.getLongExtra(INTENT_ACTION_CLICKPLAY_DATA,
						0);
				mPlayingMusic = app.getOnPlayMusicById(songId);
				play();
			}*/
        }
    }

    public void changePlayMode() {
        mPlayingMode++;
        if (mPlayingMode > 2) {
            mPlayingMode = 0;
        }

        mExtras.clear();
        mExtras.put(Event.Extra.EXTRA_PLAY_MODE, mPlayingMode);
        postEventMsgHasExtra(Event.Service.SERVICE_PLAY_MODE);

    }

    public void sendMusicInfo() {

        mExtras.clear();

        if (music != null) {

            mExtras.put(Event.Extra.EXTRA_PLAYING_POSITION, mPlayingMusic);
            mExtras.put(Event.Extra.EXTRA_PLAYING_TITLE, music.getTitle());
            mExtras.put(Event.Extra.EXTRA_PLAYING_ART, music.getArtlist());
            mExtras.put(Event.Extra.EXTRA_PLAYING_DURATION, music.getDuration());

            if (mPlayer.isPlaying()) {
                postEventMsg(Event.Service.SERVICE_PLAY);
            } else {
                postEventMsg(Event.Service.SERVICE_PAUSE);
            }

        } else {
            return;
        }
        postEventMsgHasExtra(Event.Service.SERVICE_POST_PLAYINGMUSIC);
    }

    public void right() {
        if (mPlayingMode == 1) {
            random();
        } else {
            mPlayingMusic++;
            if (mPlayingMusic == musicsSize) {
                mPlayingMusic = 0;
            }
            musicPosition = 0;
            play();
        }
    }

    public void left() {
        if (mPlayingMode == 1) {
            random();
        } else {
            mPlayingMusic--;
            if (mPlayingMusic < 0) {
                mPlayingMusic = musicsSize - 1;
            }
            musicPosition = 0;
            play();
        }
    }

    public void next() {
        historyMusics.clear();
        historyMusics.add(music);
		/*MusicBiz.insertHistoryMusics(historyMusics);*/
        switch (mPlayingMode) {
            case 2:
                all();
                break;
            case 1:
                random();
                break;
            case 0:
                single();
                break;
        }
    }

    private void all() {
        mPlayingMusic++;
        musicPosition = 0;
        play();
    }

    private void random() {
        int num = app.getNumByRandom(mPlayingMusic);
        mPlayingMusic = num;
        musicPosition = 0;
        play();
    }

    private void single() {
        musicPosition = 0;
        play();
    }

    private void pause() {
        mPlayer.stop();
        musicPosition = mPlayer.getCurrentPosition();
        postEventMsg(Event.Service.SERVICE_PAUSE);
    }

    private void play() {
        music = app.getOnPlayMusicByFlag(mPlayingMusic);
        if (music == null) {
            return;
        }
        try {
            mPlayer.reset();// 重置播放器
            mPlayer.setDataSource(music.getPath());//
            mPlayer.prepare();
            mPlayer.seekTo(musicPosition);// 指定从哪里开始
            mPlayer.start();
            musicPosition = 0;
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

    protected void postEventMsg(String msg) {
        EventUtils.getDefault().postEventMsg(msg);
    }

    protected void postEventMsgHasExtra(String msg) {
        EventUtils.getDefault().postEventMsgHasExtra(msg, mExtras);
    }

}
