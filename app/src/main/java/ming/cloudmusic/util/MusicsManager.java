package ming.cloudmusic.util;

import android.content.Context;

import org.xutils.db.table.DbModel;

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

    private static final long DEFAULT_PLAYINGID = -1;

    public static final String KEY_LOCALMUSICS_COUNT = "localmusics";
    public static final String KEY_HISTORYMUSICS_COUNT = "historymusics";

    private static final int MAX_RANDOM = 5;

    private List<DbMusic> mLocalMusics;
    private List<DbMusic> mPlayingMusics;

    private List<Integer> mPlayedMusicPositions;

    private static MusicsManager sMusicsManager;

    private MusicDao dao;

    private Map mExtras;

    private SharedPrefsUtil mSharedPrefsUtil;

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

    public void init(final Context context) {
        new Runnable() {
            @Override
            public void run() {
                dao.findMobleMusic(context.getApplicationContext().getContentResolver());
            }
        }.run();

        mSharedPrefsUtil = new SharedPrefsUtil(context.getApplicationContext(), Constant.SharedPrefrence.SHARED_NAME);

        mLocalMusics = dao.getInAppDbMusics();
        mPlayingMusics = dao.getPlayingMusics();
    }

    /**
     * 播放全部音乐
     *
     * @param dbMusics
     */
    public void playAllMusic(List<DbMusic> dbMusics) {
        updateDbMusics(dbMusics);
        postEventMsg(KeyEvent.PLAY_ALL);
    }

    /**
     * 播放单独一首歌
     *
     * @param dbMusics
     * @param position
     */
    public void playMusicByPosition(List<DbMusic> dbMusics, int position) {
        long id = mPlayingMusics.get(getPlayingPosition()).getId();
        updateDbMusics(dbMusics);
        if (id != (dbMusics.get(position).getId())) {
            mExtras.clear();
            mExtras.put(Event.Extra.PLAY_BY_POSITION, position);
            postEventMsgHasExtra(KeyEvent.PLAY_BY_POSITION, mExtras);
        }
    }

    public void removePlayingMusicById(long id) {
        for (int i = 0; i < mLocalMusics.size(); i++) {
            if (mLocalMusics.get(i).getId() == id) {
                mPlayingMusics.remove(mLocalMusics.get(i));
                mLocalMusics.get(i).setPlaySequence(DbMusic.DEFAULT_PLAY_SEQUENCE);
            }
        }

        if (getPlayingMusicsSize() == 0) {
           //TODO
        }

        dao.updateDbMusics(mLocalMusics);
    }

    public void clearPlayingMusics() {
        mPlayingMusics.clear();

        for (DbMusic music : mLocalMusics) {
            if (music.getPlaySequence() > DbMusic.DEFAULT_PLAY_SEQUENCE) {
                music.setPlaySequence(DbMusic.DEFAULT_PLAY_SEQUENCE);
            }
        }

        dao.updateDbMusics(mLocalMusics);
    }

    /**
     * 更新数据库
     *
     * @param dbMusics
     */
    private void updateDbMusics(List<DbMusic> dbMusics) {
        if (mPlayingMusics.containsAll(dbMusics)) {
            return;
        }
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

    public Map<String, String> getMusicsCount() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY_LOCALMUSICS_COUNT, String.valueOf(getLocalMusics().size()));
        map.put(KEY_HISTORYMUSICS_COUNT, String.valueOf(dao.getHistoryMusicsCount()));

        return map;
    }

    public int getPlayingMusicsSize() {
        return mPlayingMusics.size();
    }

    public int getPositionByRandom(int oldPosition) {

        int newPosition;
        int playingMusicsSize = getPlayingMusicsSize();

        if (playingMusicsSize == 0) {
            return 0;
        }

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

    public List<DbMusic> searchLocalMusic(String msg) {

        return dao.searchLocalMusic(msg);
    }

    public boolean isMusicPlaying(long id) {
        long playingId = getPlayingMusicId();
        if (playingId == DEFAULT_PLAYINGID) {
            return false;
        }

        if (playingId == id) {
            return true;
        }
        return false;
    }

    public long getPlayingMusicId() {
        return mSharedPrefsUtil.getLongSP(Constant.SharedPrefrence.PLAYING_ID, DEFAULT_PLAYINGID);
    }

    public int getPlayingPosition() {
        return mSharedPrefsUtil.getIntSP(Constant.SharedPrefrence.PLAYING_POSITION, 0);
    }

    public List<DbModel> getMusicInfoGroupByArt() {

        return getMusicInfoGroupByColumn(DbMusic.COLUMN_ARTLIST, DbMusic.COLUMN_ARTLIST);
    }

    public List<DbModel> getMusicInfoGroupByAlbum() {

        return getMusicInfoGroupByColumn(DbMusic.COLUMN_ALBUM, new String[]{DbMusic.COLUMN_ALBUM, DbMusic.COLUMN_ARTLIST});
    }

    public List<DbModel> getMusicInfoGroupByFileName() {

        return getMusicInfoGroupByColumn(DbMusic.COLUMN_FILENAME, new String[]{DbMusic.COLUMN_FILENAME});
    }

    private List<DbModel> getMusicInfoGroupByColumn(String groupByColumnName, String... selectColumns) {
        String[] temp = new String[selectColumns.length + 1];
        temp[0] = DbMusic.COLUMN_COUNT;
        for (int i = 0; i < selectColumns.length; i++) {
            temp[i + 1] = selectColumns[i];
        }
        return dao.getMusicInfoGroupByColumn(groupByColumnName, temp);
    }

    public List<DbMusic> getLocalMusicByArt(String key) {
        return dao.getLocalMusicByColumnName(DbMusic.COLUMN_ARTLIST, key);
    }

    public List<DbMusic> getLocalMusicByAlbum(String key) {
        return dao.getLocalMusicByColumnName(DbMusic.COLUMN_ALBUM, key);
    }

    public List<DbMusic> getLocalMusicByFilename(String key) {
        return dao.getLocalMusicByColumnName(DbMusic.COLUMN_FILENAME, key);
    }

    private List<DbMusic> getLocalMusicByColumnName(String columnName, String key) {
        return dao.getLocalMusicByColumnName(columnName, key);
    }

    public List<DbMusic> getPlayingMusics() {

        return mPlayingMusics;
    }

    public void setPlayingMusics(List<DbMusic> mPlayingMusics) {
        this.mPlayingMusics = mPlayingMusics;

    }

    public List<DbMusic> getLocalMusics() {

        return mLocalMusics;
    }

    public void setLocalMusics(List<DbMusic> mLocalMusics) {
        this.mLocalMusics = mLocalMusics;
    }

    private void postEventMsg(String eventMsg) {
        EventUtil.getDefault().postKeyEvent(eventMsg);
    }

    private void postEventMsgHasExtra(String eventMsg, Map extars) {
        EventUtil.getDefault().postKeyEventHasExtra(eventMsg, extars);
    }

}
