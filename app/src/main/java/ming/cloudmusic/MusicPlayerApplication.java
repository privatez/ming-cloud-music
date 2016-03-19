package ming.cloudmusic;

import android.app.Application;

import org.xutils.x;

import java.util.ArrayList;

import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.db.DbMusicDao;

public class MusicPlayerApplication extends Application {

    private ArrayList<DbMusic> mLocalMusics;
    private ArrayList<DbMusic> mPlayingMusics;

    public int getOnPlaySize() {
        return mPlayingMusics.size();
    }

    public int getNumByRandom(int musicFlag) {
        int num;
        do {
            num = (int) (Math.random() * mPlayingMusics.size());
        } while (num == musicFlag);
        return num;
    }

    public int getPlayingMusicById(long id) {
        for (int i = 0; i < mPlayingMusics.size(); i++) {
            if (id == mPlayingMusics.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    public DbMusic getOnPlayMusicByFlag(int num) {
        if (mPlayingMusics.size() == 0) {
            return null;
        }
        return mPlayingMusics.get(num);
    }

    public ArrayList<DbMusic> getmPlayingMusics() {

        return mPlayingMusics;
    }

    public void setmPlayingMusics(ArrayList<DbMusic> mPlayingMusics) {
        this.mPlayingMusics = mPlayingMusics;
        new InsertThread().start();
    }

    private class InsertThread extends Thread {
        @Override
        public void run() {
            super.run();
            /*MusicBiz.insertPlayingMusics(mPlayingMusics);*/
        }
    }

    public ArrayList<DbMusic> getmLocalMusics() {

        return mLocalMusics;
    }

    public void setmLocalMusics(ArrayList<DbMusic> mLocalMusics) {
        this.mLocalMusics = mLocalMusics;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //http://cl.avicl.ws/index.php
        //初始化xUtil
        x.Ext.init(this);
        x.Ext.setDebug(true);
        DbMusicDao dao = DbMusicDao.getDefaultDao();

        dao.findMobleMusic(getContentResolver());

        mLocalMusics = dao.getDbMusics();
        mPlayingMusics = dao.getPlayingMusics();
    }

}
