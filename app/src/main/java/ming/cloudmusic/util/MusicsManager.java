package ming.cloudmusic.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ming.cloudmusic.db.MusicDao;
import ming.cloudmusic.event.Event;
import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.event.model.KeyEvent;
import ming.cloudmusic.model.DbMusic;

/**
 * Created by lihaiye on 16/4/12.
 */
public class MusicsManager {

    private static final int MAX_RANDOM = 5;

    private List<DbMusic> mLocalMusics;
    private List<DbMusic> mPlayingMusics;

    private List<Integer> mPlayedMusicPositions;

    private static MusicsManager sMusicsManager;

    private MusicDao dao;

    private Map mExtras;

    private MusicsManager() {
        mPlayedMusicPositions = new ArrayList<>();
        mExtras = new HashMap<>();
        dao = MusicDao.getDefaultDao();
    }

    public static MusicsManager getInstance() {
        if (sMusicsManager == null) {
            synchronized (MusicsManager.class) {
                if (sMusicsManager == null) {
                    sMusicsManager = new MusicsManager();
                }
            }
        }
        return sMusicsManager;
    }

    public void init(Context context) {
        dao.findMobleMusic(context.getApplicationContext().getContentResolver());

        mLocalMusics = dao.getInAppDbMusics();
        mPlayingMusics = dao.getPlayingMusics();
    }

    public void playAllMusic(List<DbMusic> dbMusics) {
        updateDbMusics(dbMusics);
        postEventMsg(KeyEvent.PLAY_ALL);
    }

    public void playMusicByPosition(List<DbMusic> dbMusics, int position) {
        updateDbMusics(dbMusics);
        mExtras.clear();
        mExtras.put(Event.Extra.PLAY_BY_POSITION, position);
        postEventMsgHasExtra(KeyEvent.PLAY_BY_POSITION, mExtras);
    }

    private void updateDbMusics(List<DbMusic> dbMusics) {
        mPlayingMusics.clear();
        mPlayingMusics.addAll(dbMusics);

        for (int i = 0; i < dbMusics.size(); i++) {
            dbMusics.get(i).setPlaySequence(i);
        }

        mLocalMusics.removeAll(dbMusics);
        for (int i = 0; i < mLocalMusics.size(); i++) {
            mLocalMusics.get(i).setPlaySequence(DbMusic.DEFAULT_PLAY_SEQUENCE);
        }
        mLocalMusics.addAll(dbMusics);

        dao.updateDbMusics(mLocalMusics);
    }

    public void clearHistroyMusics() {
        for (int i = 0; i < mLocalMusics.size(); i++) {
            if (mLocalMusics.get(i).getHistroySequence() > DbMusic.DEFAULT_HISTORY_SEQUENCE) {
                mLocalMusics.get(i).setHistroySequence(DbMusic.DEFAULT_HISTORY_SEQUENCE);
            }
        }

        dao.updateDbMusics(mLocalMusics);
    }

    public int getPlayingMusicsSize() {
        return mPlayingMusics.size();
    }

    public int getPositionByRandom(int oldPosition) {

        int newPosition;
        int playingMusicsSize = getPlayingMusicsSize();

        do {
            newPosition = (int) (Math.random() * playingMusicsSize);
        } while (newPosition == oldPosition || mPlayedMusicPositions.contains(newPosition));
       /* LogUtils.log("playingMusicsSize:" + playingMusicsSize);
        LogUtils.log("newPosition:" + newPosition);
        LogUtils.log((playingMusicsSize <= MAX_RANDOM ? playingMusicsSize - 1 : MAX_RANDOM) + ":MAX_RANDOM");*/
        if (mPlayedMusicPositions.size() >= (playingMusicsSize <= MAX_RANDOM ? playingMusicsSize - 1 : MAX_RANDOM)) {
            mPlayedMusicPositions.remove(0);
        }
        mPlayedMusicPositions.add(newPosition);

        return newPosition;
    }

    public int getPlayingMusicById(long id) {
        for (int i = 0; i < getPlayingMusicsSize(); i++) {
            if (id == mPlayingMusics.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    public DbMusic getOnPlayMusicByPosition(int position) {
        if (getPlayingMusicsSize() == 0) {
            return null;
        }
        return mPlayingMusics.get(position);
    }

    public List<DbMusic> getmPlayingMusics() {

        return mPlayingMusics;
    }

    public void setmPlayingMusics(List<DbMusic> mPlayingMusics) {
        this.mPlayingMusics = mPlayingMusics;

    }


    public List<DbMusic> getmLocalMusics() {

        return mLocalMusics;
    }

    public void setmLocalMusics(List<DbMusic> mLocalMusics) {
        this.mLocalMusics = mLocalMusics;
    }

    private void postEventMsg(String msg) {
        EventUtil.getDefault().postEventMsg(msg, EventUtil.KEY);
    }

    private void postEventMsgHasExtra(String msg, Map extars) {
        EventUtil.getDefault().postEventMsgHasExtra(msg, extars, EventUtil.KEY);
    }

}
