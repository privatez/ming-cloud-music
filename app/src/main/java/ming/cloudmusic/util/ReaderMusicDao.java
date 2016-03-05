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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ming.cloudmusic.model.LocalMusic;

public class ReaderMusicDao implements Constant {

	private SQLiteDatabase db;

	/**
	 * 当前程序默认的文件存储位置-必须是绝对位置
	 */
	private static String path;

	/*static {
		try {
			Properties properties = new Properties();
			properties.load(ReaderMusicDao.class
					.getResourceAsStream("/assets/config.properties"));
			path = properties.getProperty("filepath");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
			.setDbName("music.db")
			.setDbDir(new File("/sdcard/cloudmusic"))
			.setDbVersion(2)
			.setDbOpenListener(new DbManager.DbOpenListener() {
				@Override
				public void onDbOpened(DbManager db) {
					// 开启WAL, 对写入加速提升巨大
					db.getDatabase().enableWriteAheadLogging();
				}
			});

	private void onTestDbClick(ContentResolver cr) {

		// 一对多: (本示例的代码)
		// 自己在多的一方(child)保存另一方的(parentId), 查找的时候用parentId查parent或child.
		// 一对一:
		// 在任何一边保存另一边的Id并加上唯一属性: @Column(name = "parentId", property = "UNIQUE")
		// 多对多:
		// 再建一个关联表, 保存两边的id. 查询分两步: 先查关联表得到id, 再查对应表的属性.

		/*	Parent test = db.selector(Parent.class).where("id", "in", new int[]{1, 3, 6}).findFirst();

			db.save(parent);

			db.saveBindingId(child);//保存对象关联数据库生成的id

			List<DbModel> dbModels = db.selector(Parent.class)
					.groupBy("name")
					.select("name", "count(name) as count").findAll();
			temp += "group by result:" + dbModels.get(0).getDataMap() + "\n";*/

	}

	/**
	 * 一键获得本地保存的音乐
	 * 
	 * @param cr
	 */
	public void findLocalMusic(ContentResolver cr) {
		Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
		String[] projection = { Audio.Media._ID, Audio.Media.TITLE,
				Audio.Media.DISPLAY_NAME, Audio.Media.DATA, Audio.Media.ARTIST,
				Audio.Media.ALBUM, Audio.Media.DURATION, };
		Cursor c = cr.query(uri, projection, null, null, null);
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

				LocalMusic music = new LocalMusic();
				music.setId(id);
				music.setTitle(title);
				music.setName(name);
				music.setPath(path);
				music.setArtlist(artlist);
				music.setAlbum(album);
				music.setDuration(duration);

				LogUtils.log(music.toString());
				LogUtil.e(music.toString());

				int num = path.lastIndexOf("/");
				String subpath = path.substring(0, num);
				music.setShortPath(subpath);

				db.save(music);

			}
		} catch (Throwable e) {
			LogUtil.e(e.getMessage());
		}
		close(c);
	}

	public ArrayList<LocalMusic> getMusics() {
		ArrayList<LocalMusic> musics = new ArrayList<>();
		DbManager db = x.getDb(daoConfig);
		try {
			musics.addAll(db.findAll(LocalMusic.class));
		} catch (DbException e) {
			e.printStackTrace();
		}

		LogUtils.log(musics.toString());

		return musics;
	}

	/**
	 * 获取本地音乐
	 * 
	 */
	public ArrayList<LocalMusic> getLocalMusics() {
		LocalMusic music = null;
		ArrayList<LocalMusic> musics = new ArrayList<LocalMusic>();
		musics.clear();
		db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

		String sql = "select * from localmusic_info";
		Cursor c = db.rawQuery(sql, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			music = new LocalMusic();
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
	 * 获取当前播放歌曲列表信息
	 * 
	 * @return ArrayList<Music>
	 */
	public ArrayList<LocalMusic> getOnPlayMusics() {
		LocalMusic music = null;
		ArrayList<LocalMusic> musics = new ArrayList<LocalMusic>();
		musics.clear();
		db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

		String sql = "select * from onplaymusic_info";
		Cursor c = db.rawQuery(sql, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			music = new LocalMusic();
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
	 * 给当前播放列表添加歌曲
	 * 
	 * @param musics
	 * @return 成功添加的数量
	 */

	public int insertOnPlayMusics(ArrayList<LocalMusic> musics) {
		int Flag = 0;
		LocalMusic music = null;
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
			// 把数据存在onplaymusic_info表中
			try {
				Object[] bindArgs = { id, title, name, path, artlist, album,
						duration };

				String insertSql = "insert into onplaymusic_info values(?,?,?,?,?,?,?)";
				db.execSQL(insertSql, bindArgs);
			} catch (SQLException e) {
				Flag++;
				Log.e("cloudmusic", e.getMessage());
			}
		}

		close();
		return musics.size() - Flag;
	}

	/**
	 * 获得历史播放列表
	 * 
	 * @return
	 */

	public ArrayList<LocalMusic> getHistoryMusics() {
		LocalMusic music = null;
		ArrayList<LocalMusic> musics = new ArrayList<LocalMusic>();
		musics.clear();
		db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

		String sql = "select * from history_music_info";
		Cursor c = db.rawQuery(sql, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			music = new LocalMusic();
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
	 * 添加播放完的歌曲到历史播放列表
	 * 
	 * @param musics
	 * @return 成功的数量
	 */
	public int insertHistoryMusics(ArrayList<LocalMusic> musics) {
		int okFlag = 0;
		LocalMusic music = null;
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
			// 把数据存在onplaymusic_info表中
			try {
				Object[] bindArgs = { id, title, name, path, artlist, album,
						duration };
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
	 * 一键清空历史播放列表
	 * 
	 * @return 删除成功的条数,-1表示删除失败
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
	 * 使用歌手名分类显示
	 * 
	 * @return ArrayList型的Map泛型的集合
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
	 * 使用文件名分类显示
	 * 
	 * @return ArrayList型的Map泛型的集合
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
	 * 使用专辑名分类显示
	 * 
	 * @return ArrayList型的Map泛型的集合
	 */
	public ArrayList<Map<String, String>> groupByAlubm() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		LocalMusic music = null;
		db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);
		String sql = "select * from(select album,artlist,count(album)"
				+ "as number from localmusic_info group by album)"
				+ "as temp order by number desc";
		Cursor c = db.rawQuery(sql, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			map = new HashMap<String, String>();
			music = new LocalMusic();
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
	 * 
	 * @param key
	 *            列名=?
	 * @param value
	 *            ?的值
	 * @return
	 */

	public ArrayList<LocalMusic> getMusicByTag(String key, String value) {
		ArrayList<LocalMusic> musics = new ArrayList<LocalMusic>();
		LocalMusic music = null;
		db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

		String table = "localmusic_info";
		String[] columns = { "*" };
		String selection = key + "=?";
		String[] selectionArgs = { value };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor c = db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			music = new LocalMusic();
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

	public ArrayList<LocalMusic> getMusicByFile(String value) {
		ArrayList<LocalMusic> musics = new ArrayList<LocalMusic>();
		LocalMusic music = null;
		db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

		String table = "localmusic_groupbyfile";
		String[] columns = { "*" };
		String selection = "data=?";
		String[] selectionArgs = { value };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor c = db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			music = new LocalMusic();
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
	 * 搜索本地歌曲
	 * @param value
	 * @return
	 */

	public ArrayList<LocalMusic> findMusicByUser(String value) {
		ArrayList<LocalMusic> musics = new ArrayList<LocalMusic>();
		LocalMusic music = null;
		db = SQLiteDatabase.openOrCreateDatabase(path + "/cloudmusic.db", null);

		String table = "localmusic_groupbyfile";
		String[] columns = { "*" };
		String selection = "display_name like ? or artlist like ? or album like ?";
		String[] selectionArgs = { value,value,value };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor c = db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			music = new LocalMusic();
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
	 * 当有Cursor 回调该方法关闭数据库连接以及关闭Cursor
	 */
	public void close(Cursor c) {
		if (db != null && db.isOpen()) {
			db.close();
		}
		if(c!=null&&c.isAfterLast()){
			c.close();
		}
	}
	
	/**
	 * 单独关闭数据库连接
	 */
	public void close(){
		if (db != null && db.isOpen()) {
			db.close();
		}
	}


}
