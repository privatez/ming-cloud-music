package ming.cloudmusic.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore.Audio;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.LogUtils;

public class MusicDao {

    private SQLiteDatabase db;
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
                //.setDbDir(new File(DB_PATH))
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
                dbMusic.setArtlist(artlist);
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
            close(db);
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
            musics.addAll(db.findAll(DbMusic.class));

        } catch (java.io.IOException e) {
            LogUtils.log(e.getMessage());
        } finally {
            close(db);
        }

        //LogUtils.log("数据库中的音乐：" + musics.toString());

        return musics;
    }

    public void updateDbMusics(List<DbMusic> musics) {

        DbManager db = x.getDb(mDaoConfig);

        for (DbMusic music : musics) {
            try {
                db.update(music);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        close(db);
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
            musics.addAll(db.selector(DbMusic.class).where(DbMusic.COLUMN_PLAY_SEQUENCE, ">",
                    DbMusic.DEFAULT_PLAY_SEQUENCE).findAll());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            close(db);
        }

        //LogUtils.log("播放中的音乐：" + musics.toString());

        return musics;
    }

    /**
     * 添加音乐到播放数据库
     *
     * @param musics
     * @return musics.size() - Flag
     */

    public int insertPlayingMusics(List<DbMusic> musics) {

        int Flag = 0;

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

        close(db);

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
            musics.addAll(db.selector(DbMusic.class).where(DbMusic.COLUMN_HISTORY_SEQUENCE, ">",
                    DbMusic.DEFAULT_HISTORY_SEQUENCE).orderBy(DbMusic.COLUMN_HISTORY_SEQUENCE, true).findAll());
        } catch (java.io.IOException e) {
            LogUtils.log("获取历史音乐Excetion：" + e.getMessage());
        } finally {
            close(db);
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
            LogUtils.log("获取全部的历史播放歌曲总数Excetion：" + e.getMessage());
        } finally {
            close(db);
        }

        //LogUtils.log("获取历史音乐：" + musics.toString());

        return count;
    }


    public void insertHistoryMusics(DbMusic music, int playingPosition) {
        music.setHistroySequence(playingPosition);
        music.setPlayedTime(System.currentTimeMillis());
        DbManager db = x.getDb(mDaoConfig);
        try {
            db.update(music);
        } catch (java.io.IOException e) {
            LogUtils.log("插入历史音乐Excetion：" + e.getMessage());
        } finally {
            close(db);
        }

        //EventUtil.getDefault().postEventMsg(DataEvent.HISTORYMUSICS_CHANGGE);

    }

    /**
     * ʹ�ø����������ʾ
     *
     * @return ArrayList�͵�Map���͵ļ���
     */
    public ArrayList<Map<String, String>> groupByArtlist() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/cloudmusic.db", null);
        String sql = "select * from(select artlist,count(artlist) "
                + "as number from localmusic_info group by artlist)as temp "
                + "order by number desc";
        Cursor c = db.rawQuery(sql, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            map = new HashMap<String, String>();
            String artlist = c.getString(c.getColumnIndex("artlist"));
            String number = c.getString(c.getColumnIndex("number"));
            map.put(artlist, number);
            list.add(map);
        }
        close(c);
        return list;
    }

    /**
     * ʹ���ļ��������ʾ
     *
     * @return ArrayList�͵�Map���͵ļ���
     */
    public ArrayList<Map<String, String>> groupByFilePath() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/cloudmusic.db", null);
        String sql = "select * from(select data,count(data)"
                + "as number from localmusic_groupbyfile group by data)"
                + "as temp order by number desc";
        Cursor c = db.rawQuery(sql, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            map = new HashMap<String, String>();
            String data = c.getString(c.getColumnIndex("data"));
            String number = c.getString(c.getColumnIndex("number"));
            map.put(data, number);
            list.add(map);
        }
        close(c);
        return list;
    }

    /**
     * ʹ��ר���������ʾ
     *
     * @return ArrayList�͵�Map���͵ļ���
     */
    public ArrayList<Map<String, String>> groupByAlubm() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        DbMusic music = null;
        db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/cloudmusic.db", null);
        String sql = "select * from(select album,artlist,count(album)"
                + "as number from localmusic_info group by album)"
                + "as temp order by number desc";
        Cursor c = db.rawQuery(sql, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            map = new HashMap<String, String>();
            music = new DbMusic();
            String album = c.getString(c.getColumnIndex("album"));
            String artlist = c.getString(c.getColumnIndex("artlist"));
            String number = c.getString(c.getColumnIndex("number"));
            music.setAlbum(album);
            music.setArtlist(artlist);
            map.put(album, number + "/." + artlist);
            list.add(map);
        }
        close(c);
        return list;
    }

    /**
     * @param key   ����=?
     * @param value ?��ֵ
     * @return
     */

    public ArrayList<DbMusic> getMusicByTag(String key, String value) {
        ArrayList<DbMusic> musics = new ArrayList<DbMusic>();
        DbMusic music = null;
        db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/cloudmusic.db", null);

        String table = "localmusic_info";
        String[] columns = {"*"};
        String selection = key + "=?";
        String[] selectionArgs = {value};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            music = new DbMusic();
            music.setId(c.getLong(c.getColumnIndex("_id")));
            music.setTitle(c.getString(c.getColumnIndex("_title")));
            music.setName(c.getString(c.getColumnIndex("display_name")));
            music.setPath(c.getString(c.getColumnIndex("data")));
            music.setArtlist(c.getString(c.getColumnIndex("artlist")));
            music.setAlbum(c.getString(c.getColumnIndex("album")));
            music.setDuration(c.getInt(c.getColumnIndex("duration")));

            musics.add(music);
        }
        close(c);
        return musics;
    }

    public ArrayList<DbMusic> getMusicByFile(String value) {
        ArrayList<DbMusic> musics = new ArrayList<DbMusic>();
        DbMusic music = null;
        db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/cloudmusic.db", null);

        String table = "localmusic_groupbyfile";
        String[] columns = {"*"};
        String selection = "data=?";
        String[] selectionArgs = {value};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            music = new DbMusic();
            music.setId(c.getLong(c.getColumnIndex("_id")));
            music.setTitle(c.getString(c.getColumnIndex("_title")));
            music.setName(c.getString(c.getColumnIndex("display_name")));
            music.setPath(c.getString(c.getColumnIndex("data")));
            music.setArtlist(c.getString(c.getColumnIndex("artlist")));
            music.setAlbum(c.getString(c.getColumnIndex("album")));
            music.setDuration(c.getInt(c.getColumnIndex("duration")));

            musics.add(music);
        }
        close(c);
        return musics;
    }

    /**
     * �������ظ���
     *
     * @param value
     * @return
     */

    public ArrayList<DbMusic> findMusicByUser(String value) {
        ArrayList<DbMusic> musics = new ArrayList<DbMusic>();
        DbMusic music = null;
        db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/cloudmusic.db", null);

        String table = "localmusic_groupbyfile";
        String[] columns = {"*"};
        String selection = "display_name like ? or artlist like ? or album like ?";
        String[] selectionArgs = {value, value, value};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            music = new DbMusic();
            music.setId(c.getLong(c.getColumnIndex("_id")));
            music.setTitle(c.getString(c.getColumnIndex("_title")));
            music.setName(c.getString(c.getColumnIndex("display_name")));
            music.setPath(c.getString(c.getColumnIndex("data")));
            music.setArtlist(c.getString(c.getColumnIndex("artlist")));
            music.setAlbum(c.getString(c.getColumnIndex("album")));
            music.setDuration(c.getInt(c.getColumnIndex("duration")));
            musics.add(music);
        }
        close(c);
        return musics;
    }

    /**
     * ����Cursor �ص��÷����ر���ݿ������Լ��ر�Cursor
     */
    public void close(Cursor c) {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (c != null && c.isAfterLast()) {
            c.close();
        }
    }


    public void close(DbManager db) {
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
