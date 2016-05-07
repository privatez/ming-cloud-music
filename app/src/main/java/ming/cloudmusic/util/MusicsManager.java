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
import ming.cloudmusic.event.model.DataEvent;
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

        mSharedPrefsUtil = new SharedPrefsUtil(context.getApplicationContext(),
                Constant.SharedPrefrence.SHARED_NAME_DATA);

        mLocalMusics = dao.getInAppDbMusics();
        mPlayingMusics = dao.getPlayingMusics();
    }

    /**
     * 播放全部音乐
     *
     * @param dbMusics
     */
    public void playAllMusic(List<DbMusic> dbMusics) {
        if (dbMusics == null || dbMusics.size() == 0) {
            return;
        }
        updateDbMusics(dbMusics);
        postKeyEventMsg(KeyEvent.PLAY_ALL);
    }

    /**
     * 播放单独一首歌
     *
     * @param dbMusics
     * @param position
     */
    public void playMusicByPosition(List<DbMusic> dbMusics, int position) {
        updateDbMusics(dbMusics);
        if (!isMusicPlaying(dbMusics.get(position).getId())) {
            mExtras.clear();
            mExtras.put(Event.Extra.PLAY_BY_POSITION, position);
            postKeyEventMsgHasExtra(KeyEvent.PLAY_BY_POSITION, mExtras);
        }
    }

    public void removePlayingMusicById(long id) {
        if (isMusicPlaying(id)) {
            postKeyEventMsg(KeyEvent.NEXT);
        }
        for (int i = 0; i < mLocalMusics.size(); i++) {
            if (mLocalMusics.get(i).getId() == id) {
                mPlayingMusics.remove(mLocalMusics.get(i));
                mLocalMusics.get(i).setPlaySequence(DbMusic.DEFAULT_PLAY_SEQUENCE);
            }
        }

        checkIsHasPlayingList();
        dao.updateDbMusics(mLocalMusics);
    }

    public void clearPlayingMusics() {
        mPlayingMusics.clear();

        for (int i = 0; i < mLocalMusics.size(); i++) {
            if (mLocalMusics.get(i).getPlaySequence() > DbMusic.DEFAULT_PLAY_SEQUENCE) {
                mLocalMusics.get(i).setPlaySequence(DbMusic.DEFAULT_PLAY_SEQUENCE);
            }
        }

        checkIsHasPlayingList();
        dao.updateDbMusics(mLocalMusics);
    }

    private boolean checkIsHasPlayingList() {
        if (getPlayingMusicsSize() == 0) {
            postDataEventMsg(DataEvent.PLAYINTMUSICS_ISCLEAR);
            mSharedPrefsUtil.clearAll();
            return false;
        }
        return true;
    }

    /**
     * 更新数据库
     *
     * @param newPlayMusics
     */
    private void updateDbMusics(List<DbMusic> newPlayMusics) {
        if (newPlayMusics == null || newPlayMusics.size() == 0) {
            return;
        }

        if (mPlayingMusics.containsAll(newPlayMusics)) {
            return;
        }

        mPlayingMusics.clear();
        mPlayingMusics.addAll(newPlayMusics);

        int localCount = mLocalMusics.size();
        int playingCount = getPlayingMusicsSize();

        for (int local = 0; local < localCount; local++) {
            mLocalMusics.get(local).setPlaySequence(DbMusic.DEFAULT_PLAY_SEQUENCE);
        }

        for (int newPlay = 0; newPlay < playingCount; newPlay++) {
            for (int local = 0; local < localCount; local++) {
                if (mPlayingMusics.get(newPlay).getId() == mLocalMusics.get(local).getId()) {
                    mPlayingMusics.get(newPlay).setPlaySequence(newPlay);
                    mLocalMusics.get(local).setPlaySequence(newPlay);
                }
            }
        }

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
        if (mPlayedMusicPositions.size() >= (playingMusicsSize <= MAX_RANDOM
                ? playingMusicsSize - 1 : MAX_RANDOM)) {
            mPlayedMusicPositions.remove(0);
        }
        mPlayedMusicPositions.add(newPosition);

        return newPosition;
    }

    public DbMusic getOnPlayMusicByPosition(int position) {
        return checkIsHasPlayingList() ? mPlayingMusics.get(position) : null;
    }

    public DbMusic getPlayingMusicById(long id) {
        DbMusic isPlayingMusic = null;
        for (DbMusic music : mPlayingMusics) {
            if (music.getId() == id) {
                isPlayingMusic = music;
            }
        }
        return isPlayingMusic;
    }

    public List<DbMusic> searchLocalMusic(String msg) {

        return dao.searchLocalMusic(msg);
    }

    public boolean isMusicPlaying(long id) {

        return mSharedPrefsUtil.getLongSP(
                Constant.SharedPrefrence.PLAYING_ID, DEFAULT_PLAYINGID) == id;
    }

    public int getPlayingPosition() {
        return mSharedPrefsUtil.getIntSP(Constant.SharedPrefrence.PLAYING_POSITION, 0);
    }

    public List<DbModel> getMusicInfoGroupByArt() {

        return getMusicInfoGroupByColumn(DbMusic.COLUMN_ARTLIST, DbMusic.COLUMN_ARTLIST);
    }

    public List<DbModel> getMusicInfoGroupByAlbum() {

        return getMusicInfoGroupByColumn(DbMusic.COLUMN_ALBUM, DbMusic.COLUMN_ALBUM, DbMusic.COLUMN_ARTLIST);
    }

    public List<DbModel> getMusicInfoGroupByFileName() {

        return getMusicInfoGroupByColumn(DbMusic.COLUMN_FILENAME, DbMusic.COLUMN_FILENAME);
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

    private void postKeyEventMsg(String eventMsg) {
        EventUtil.getDefault().postKeyEvent(eventMsg);
    }

    private void postKeyEventMsgHasExtra(String eventMsg, Map extars) {
        EventUtil.getDefault().postKeyEventHasExtra(eventMsg, extars);
    }

    private void postDataEventMsg(String eventMsg) {
        EventUtil.getDefault().postDataEvent(eventMsg);
    }

    private void postDataEventMsgHasExtra(String eventMsg, Map extars) {
        EventUtil.getDefault().postDataEventHasExtra(eventMsg, extars);
    }

}
