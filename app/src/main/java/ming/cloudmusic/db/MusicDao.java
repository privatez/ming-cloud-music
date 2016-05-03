package ming.cloudmusic.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.text.TextUtils;

import org.xutils.DbManager;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.LogUtils;

public class MusicDao {

    /**
     * 数据库名字
     */
    private static final String DB_NAME = "music.db";

    /**
     * 数据库地址
     */
    private static final String DB_PATH = "/sdcard/cloudmusic";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 2;

    private DbManager.DaoConfig mDaoConfig;

    private static MusicDao mMusicDao;

    private MusicDao() {
        mDaoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME)
                .setDbDir(new File(DB_PATH))
                .setDbVersion(DB_VERSION)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
    }

    public static MusicDao getDefaultDao() {
        if (mMusicDao == null) {
            synchronized (MusicDao.class) {
                if (mMusicDao == null) {
                    mMusicDao = new MusicDao();
                }
            }
        }

        return mMusicDao;
    }

    /**
     * 扫描当前手机存储的音乐文件
     *
     * @param cr
     */
    public void findMobleMusic(ContentResolver cr) {
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {Audio.Media._ID, Audio.Media.TITLE,
                Audio.Media.DISPLAY_NAME, Audio.Media.DATA, Audio.Media.ARTIST,
                Audio.Media.ALBUM, Audio.Media.DURATION,};

        Cursor c = cr.query(uri, projection, null, null, null);

        DbManager db = x.getDb(mDaoConfig);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                long id = (c.getLong(c.getColumnIndex(Audio.Media._ID)));
                String title = (c.getString(c.getColumnIndex(Audio.Media.TITLE)));
                String name = (c.getString(c
                        .getColumnIndex(Audio.Media.DISPLAY_NAME)));
                String path = (c.getString(c.getColumnIndex(Audio.Media.DATA)));
                String artlist = (c.getString(c.getColumnIndex(Audio.Media.ARTIST)));
                String album = (c.getString(c.getColumnIndex(Audio.Media.ALBUM)));
                int duration = (c.getInt(c.getColumnIndex(Audio.Media.DURATION)));

                DbMusic dbMusic = new DbMusic();
                dbMusic.setId(id);
                dbMusic.setTitle(title);
                dbMusic.setName(name);
                dbMusic.setPath(path);
                if (artlist.contains("unknown")) {
                    dbMusic.setArtlist("未知");
                } else {
                    dbMusic.setArtlist(artlist);
                }
                dbMusic.setAlbum(album);
                dbMusic.setDuration(duration);
                //TODO
                dbMusic.setPlaySequence(c.getPosition());
                dbMusic.setHistroySequence(DbMusic.DEFAULT_HISTORY_SEQUENCE);
                dbMusic.setLocalMusic(true);

                int num = path.lastIndexOf("/");
                dbMusic.setFileNmae(path.substring(0, num));

                if (!dbHasThisMusic(path)) {
                    db.save(dbMusic);
                }
            }
        } catch (Throwable e) {
            LogUtils.log(e.getMessage());
        } finally {
            closeDbManagerWithUnUpdate(db);
            c.close();
        }

    }

    /**
     * 通过歌曲路径判断数据库中是否有该歌曲
     *
     * @param path
     * @return
     */
    private boolean dbHasThisMusic(String path) {
        DbManager db = x.getDb(mDaoConfig);
        try {
            DbMusic music = db.selector(DbMusic.class).where(DbMusic.COLUMN_PATH, "=", path).findFirst();
            if (music == null) {
                return false;
            } else {
                //LogUtils.log("比对到的音乐：" + music.toString());
            }
        } catch (DbException e) {
            LogUtils.log(e.getMessage());
        }
        return true;
    }


    /**
     * 获取已存储在软件数据库中的音乐列表
     *
     * @return
     */
    public List<DbMusic> getInAppDbMusics() {
        List<DbMusic> musics = new ArrayList<>();
        DbManager db = x.getDb(mDaoConfig);
        try {
            musics.addAll(checkDbMusicsResultNull(db.findAll(DbMusic.class)));
        } catch (java.io.IOException e) {
            LogUtils.log(e.getMessage());
        } finally {
            closeDbManagerWithUnUpdate(db);
        }

        //LogUtils.log("数据库中的音乐：" + musics.toString());

        return musics;
    }

    public void updateDbMusics(List<DbMusic> musics) {
        if (musics != null || musics.isEmpty()) {
            return;
        }

        DbManager db = x.getDb(mDaoConfig);

        for (DbMusic music : musics) {
            try {
                db.update(music);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        closeDbManagerWithUpdate(db);
    }


    /**
     * 获取正在播放的音乐列表
     *
     * @return ArrayList<Music>
     */
    public List<DbMusic> getPlayingMusics() {

        List<DbMusic> musics = new ArrayList();

        DbManager db = x.getDb(mDaoConfig);
        try {
            musics.addAll(checkDbMusicsResultNull(db.selector(DbMusic.class).where(DbMusic.COLUMN_PLAY_SEQUENCE, ">",
                    DbMusic.DEFAULT_PLAY_SEQUENCE).findAll()));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            closeDbManagerWithUnUpdate(db);
        }

        //LogUtils.log("播放中的音乐：" + musics.toString());

        return musics;
    }

    /**
     * 添加音乐到播放数据库
     *
     * @param musics
     * @return musics.size() - Flag  成功的数量
     */

    public int insertPlayingMusics(List<DbMusic> musics) {
        int Flag = 0;

        if (musics != null || musics.isEmpty()) {
            return Flag;
        }

        DbMusic music;

        DbManager db = x.getDb(mDaoConfig);

        for (int i = 0; i < musics.size(); i++) {
            music = musics.get(i);
            try {
                db.save(music);
            } catch (DbException e) {
                Flag++;
            }
        }

        closeDbManagerWithUpdate(db);

        return musics.size() - Flag;
    }


    /**
     * 获取全部的历史播放歌曲
     *
     * @return
     */
    public List<DbMusic> getHistoryMusics() {
        List<DbMusic> musics = new ArrayList();

        DbManager db = x.getDb(mDaoConfig);
        try {
            musics.addAll(checkDbMusicsResultNull(db.selector(DbMusic.class).where(DbMusic.COLUMN_HISTORY_SEQUENCE, ">",
                    DbMusic.DEFAULT_HISTORY_SEQUENCE).orderBy(DbMusic.COLUMN_HISTORY_SEQUENCE, true).findAll()));
        } catch (java.io.IOException e) {
            LogUtils.log("getHistoryMusics Exception：" + e.getMessage());
        } finally {
            closeDbManagerWithUnUpdate(db);
        }

        //LogUtils.log("获取历史音乐：" + musics.toString());

        return musics;
    }

    /**
     * 获取全部的历史播放歌曲总数
     *
     * @return
     */
    public long getHistoryMusicsCount() {
        long count = 0;

        DbManager db = x.getDb(mDaoConfig);
        try {
            count = (db.selector(DbMusic.class).where(DbMusic.COLUMN_HISTORY_SEQUENCE, ">",
                    DbMusic.DEFAULT_HISTORY_SEQUENCE).orderBy(DbMusic.COLUMN_HISTORY_SEQUENCE, true).count());
        } catch (java.io.IOException e) {
            LogUtils.log("getHistoryMusicsCount Exception：" + e.getMessage());
        } finally {
            closeDbManagerWithUnUpdate(db);
        }

        //LogUtils.log("获取历史音乐：" + musics.toString());

        return count;
    }


    public void insertHistoryMusics(DbMusic music, int playingPosition) {
        if (music == null || playingPosition < 0) {
            return;
        }
        music.setHistroySequence(playingPosition);
        music.setPlayedTime(System.currentTimeMillis());
        DbManager db = x.getDb(mDaoConfig);
        try {
            db.update(music);
        } catch (java.io.IOException e) {
            LogUtils.log("insertHistoryMusics Exception：" + e.getMessage());
        } finally {
            closeDbManagerWithUpdate(db);
        }

        //EventUtil.getDefault().postEventMsg(DataEvent.HISTORYMUSICS_CHANGGE);

    }

    public List<DbMusic> searchLocalMusic(String msg) {
        List<DbMusic> musics = new ArrayList<>();
        if (TextUtils.isEmpty(msg)) {
            return musics;
        }
        DbManager db = x.getDb(mDaoConfig);
        try {
            musics.addAll(checkDbMusicsResultNull(db.selector(DbMusic.class).where(DbMusic.COLUMN_NAME,
                    "like", "%" + msg + "%").findAll()));
        } catch (java.io.IOException e) {
            LogUtils.log("searchLocalMusic Exception：" + e.getMessage());
        } finally {
            closeDbManagerWithUnUpdate(db);
        }
        LogUtils.log(musics.toString());
        return musics;
    }

    public List<DbModel> getMusicInfoGroupByColumn(String groupByColumnName, String... selectColumns) {
        List<DbModel> dbModels = new ArrayList<>();
        DbManager db = x.getDb(mDaoConfig);
        try {
            dbModels.addAll(checkModelResultNull(db.selector(DbMusic.class).groupBy(groupByColumnName).select(selectColumns).findAll()));
        } catch (java.io.IOException e) {
            LogUtils.log("getLocalMusicGroupBy " + groupByColumnName + " Exception：" + e.getMessage());
        } finally {
            closeDbManagerWithUnUpdate(db);
        }

        //LogUtils.log("getLocalMusicGroupBy " + groupBy + ".....result: " + musics.toString());

        return dbModels;
    }

    public List<DbMusic> getLocalMusicByColumnName(String columnName, String key) {
        List<DbMusic> dbMusics = new ArrayList<>();
        DbManager db = x.getDb(mDaoConfig);
        try {
            dbMusics.addAll(checkDbMusicsResultNull(db.selector(DbMusic.class).where(columnName, "=", key).findAll()));
        } catch (java.io.IOException e) {
            LogUtils.log("getLocalMusicBy: " + columnName + "....key: " + key + " Exception：" + e.getMessage());
        } finally {
            closeDbManagerWithUnUpdate(db);
        }
        //LogUtils.log("getLocalMusicBy: " + columnName + "....key: " + key "....result: "+ musics.toString());

        return dbMusics;
    }

    private List<DbMusic> checkDbMusicsResultNull(List<DbMusic> result) {
        List<DbMusic> list = new ArrayList<>();
        if (result != null) {
            list.addAll(result);
        }
        return list;
    }

    private List<DbModel> checkModelResultNull(List<DbModel> result) {
        List<DbModel> list = new ArrayList<>();
        if (result != null) {
            list.addAll(result);
        }
        return list;
    }

    private void closeDbManagerWithUnUpdate(DbManager db) {
        closeDbManager(db, false);
    }

    private void closeDbManagerWithUpdate(DbManager db) {
        closeDbManager(db, true);
    }

    private void closeDbManager(DbManager db, boolean isDbUpdate) {
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isDbUpdate) {
            //TODO 发送更新
        }
    }


}
