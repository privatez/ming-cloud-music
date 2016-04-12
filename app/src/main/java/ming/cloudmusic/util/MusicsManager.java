package ming.cloudmusic.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.db.MusicDao;
import ming.cloudmusic.model.DbMusic;

/**
 * Created by lihaiye on 16/4/12.
 */
public class MusicsManager {

    private static final int MAX_RANDOM = 5;

    private ArrayList<DbMusic> mLocalMusics;
    private ArrayList<DbMusic> mPlayingMusics;

    private List<Integer> mPlayedMusicPositions;

    private static MusicsManager sMusicsManager;

    private MusicsManager() {
        mPlayedMusicPositions = new ArrayList<>();
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
        MusicDao dao = MusicDao.getDefaultDao();

        dao.findMobleMusic(context.getApplicationContext().getContentResolver());

        mLocalMusics = dao.getDbMusics();
        mPlayingMusics = dao.getPlayingMusics();
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

        LogUtils.log((playingMusicsSize <= MAX_RANDOM ? playingMusicsSize - 1 : MAX_RANDOM) + ":MAX_RANDOM");
        if (playingMusicsSize >= (playingMusicsSize <= MAX_RANDOM ? playingMusicsSize - 1 : MAX_RANDOM)) {
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

    public ArrayList<DbMusic> getmPlayingMusics() {

        return mPlayingMusics;
    }

    public void setmPlayingMusics(ArrayList<DbMusic> mPlayingMusics) {
        this.mPlayingMusics = mPlayingMusics;

    }


    public ArrayList<DbMusic> getmLocalMusics() {

        return mLocalMusics;
    }

    public void setmLocalMusics(ArrayList<DbMusic> mLocalMusics) {
        this.mLocalMusics = mLocalMusics;
    }

}
