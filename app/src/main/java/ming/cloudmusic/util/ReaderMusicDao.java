package ming.cloudmusic.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.util.Log;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.model.PlayingMusic;

public class ReaderMusicDao implements Constant {

    private SQLiteDatabase db;

    /**
     * 地址
     */
    private static String path;


    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("music.db")
            .setDbDir(new File("/sdcard/cloudmusic"))
            .setDbVersion(2)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    // ����WAL, ��д���������޴�
                    db.getDatabase().enableWriteAheadLogging();
                }
            });

    private void onTestDbClick(ContentResolver cr) {

			/*db.save(parent);

			db.saveBindingId(child);//������������ݿ���ɵ�id

			List<DbModel> dbModels = db.selector(Parent.class)
					.groupBy("name")
					.select("name", "count(name) as count").findAll();
			temp += "group by result:" + dbModels.get(0).getDataMap() + "\n";*/
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
        DbMusic dbMusic;

        ArrayList<PlayingMusic> playingMusics = new ArrayList<>();
        PlayingMusic playingMusic ;

        try {

            DbManager db = x.getDb(daoConfig);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                long id = (c.getLong(c.getColumnIndex(Audio.Media._ID)));
                String title = (c.getString(c.getColumnIndex(Audio.Media.TITLE)));
                String name = (c.getString(c
                        .getColumnIndex(Audio.Media.DISPLAY_NAME)));
                String path = (c.getString(c.getColumnIndex(Audio.Media.DATA)));
                String artlist = (c.getString(c.getColumnIndex(Audio.Media.ARTIST)));
                String album = (c.getString(c.getColumnIndex(Audio.Media.ALBUM)));
                int duration = (c.getInt(c.getColumnIndex(Audio.Media.DURATION)));

                dbMusic = new DbMusic();
                dbMusic.setId(id);
                dbMusic.setTitle(title);
                dbMusic.setName(name);
                dbMusic.setPath(path);
                dbMusic.setArtlist(artlist);
                dbMusic.setAlbum(album);
                dbMusic.setDuration(duration);

                playingMusic = new PlayingMusic();
                playingMusic.setId(id);
                playingMusic.setTitle(title);
                playingMusic.setName(name);
                playingMusic.setPath(path);
                playingMusic.setArtlist(artlist);
                playingMusic.setAlbum(album);
                playingMusic.setDuration(duration);

                playingMusics.add(playingMusic);

                LogUtils.log(dbMusic.toString());
                LogUtil.e(dbMusic.toString());

                int num = path.lastIndexOf("/");
                String subpath = path.substring(0, num);
                dbMusic.setShortPath(subpath);

                db.save(dbMusic);
                db.close();
            }
        } catch (Throwable e) {
            LogUtil.e(e.getMessage());
        } finally {
            c.close();
        }

        insertPlayingMusics(playingMusics);

    }


    /**
     * 获取已存储在软件数据库中的音乐列表
     *
     * @return
     */
    public ArrayList<DbMusic> getDbMusics() {
        ArrayList<DbMusic> musics = new ArrayList<>();
        DbManager db = x.getDb(daoConfig);
        try {
            musics.addAll(db.findAll(DbMusic.class));
            db.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        LogUtils.log("数据库中的音乐："+musics.toString());

        return musics;
    }

    /**
     * 获取正在播放的音乐列表
     *
     * @return ArrayList<Music>
     */
    public ArrayList<PlayingMusic> getPlayingMusics() {

        ArrayList<PlayingMusic> musics = new ArrayList();

        DbManager db = x.getDb(daoConfig);
        try {
            musics.addAll(db.findAll(PlayingMusic.class));
            db.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        LogUtils.log("播放中的音乐："+musics.toString());

        return musics;
    }

    /**
     * 添加音乐到播放数据库
     *
     * @param musics
     * @return �ɹ���ӵ�����
     */

    public int insertPlayingMusics(ArrayList<PlayingMusic> musics) {

        int Flag = 0;

        PlayingMusic music;

        DbManager db = x.getDb(daoConfig);

        for (int i = 0; i < musics.size(); i++) {
            music = musics.get(i);
            try {
                db.save(music);
            } catch (DbException e) {
                Flag++;
            }
        }

        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return musics.size() - Flag;
    }

    /**
     * �����ʷ�����б�
     *
     * @return
     */

    public ArrayList<DbMusic> getHistoryMusics() {
        DbMusic music = null;
        ArrayList<DbMusic> musics = new ArrayList<DbMusic>();
        musics.clear();
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

        String sql = "select * from history_music_info";
        Cursor c = db.rawQuery(sql, null);
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
     * ��Ӳ�����ĸ�����ʷ�����б�
     *
     * @param musics
     * @return �ɹ�������
     */
    public int insertHistoryMusics(ArrayList<DbMusic> musics) {
        int okFlag = 0;
        DbMusic music = null;
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

        for (int i = 0; i < musics.size(); i++) {
            music = musics.get(i);
            long id = (music.getId());
            String title = (music.getTitle());
            String name = (music.getName());
            String path = (music.getPath());
            String artlist = (music.getArtlist());
            String album = (music.getAlbum());
            int duration = (music.getDuration());
            // String
            // date=String.valueOf(SystemClock.currentThreadTimeMillis());
            // ����ݴ���onplaymusic_info����
            try {
                Object[] bindArgs = {id, title, name, path, artlist, album,
                        duration};
                // Log.e(tag, title);
                String insertSql = "insert into history_music_info values(?,?,?,?,?,?,?)";
                db.execSQL(insertSql, bindArgs);
            } catch (SQLException e) {
                okFlag++;
                Log.e("cloudmusic", e.getMessage());
            }
        }
        close();
        return musics.size() - okFlag;
    }

    /**
     * һ�������ʷ�����б�
     *
     * @return ɾ��ɹ�������,-1��ʾɾ��ʧ��
     */
    public int clearHistoryMusic() {
        int okFlag = -1;
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);
        String table = "history_music_info";
        try {
            okFlag = db.delete(table, null, null);
        } catch (SQLException e) {
            LogUtil.d(e.getMessage());
        }
        close();
        return okFlag;
    }

    /**
     * ʹ�ø����������ʾ
     *
     * @return ArrayList�͵�Map���͵ļ���
     */
    public ArrayList<Map<String, String>> groupByArtlist() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);
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
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);
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
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);
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
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

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
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

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
        db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

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

    /**
     * �����ر���ݿ�����
     */
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }


}
