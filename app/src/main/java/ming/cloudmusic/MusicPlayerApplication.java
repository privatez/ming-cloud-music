package ming.cloudmusic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import ming.cloudmusic.model.LocalMusic;

public class MusicPlayerApplication extends Application {

	private ArrayList<LocalMusic> localMusics;
	private ArrayList<LocalMusic> onPlayMusics;

	public int getOnPlaySize() {
		return onPlayMusics.size();
	}

	public int getNumByRandom(int musicFlag) {
		int num;
		do {
			num = (int) (Math.random() * onPlayMusics.size());
		} while (num == musicFlag);
		return num;
	}

	public int getOnPlayMusicById(long id) {
		for (int i = 0; i < onPlayMusics.size(); i++) {
			if (id == onPlayMusics.get(i).getId()) {
				return i;
			}
		}
		return -1;
	}

	public LocalMusic getOnPlayMusicByFlag(int num) {
		if (onPlayMusics.size() == 0) {
			return null;
		}
		return onPlayMusics.get(num);
	}

	public ArrayList<LocalMusic> getOnPlayMusics() {

		return onPlayMusics;
	}

	public void setOnPlayMusics(ArrayList<LocalMusic> onPlayMusics) {
		this.onPlayMusics = onPlayMusics;
		new InsertThread().start();
	}

	private class InsertThread extends Thread {
		@Override
		public void run() {
			super.run();
			/*MusicBiz.insertOnPlayMusics(onPlayMusics);*/
		}
	}

	public ArrayList<LocalMusic> getLocalMusics() {

		return localMusics;
	}

	public void setLocalMusics(ArrayList<LocalMusic> localMusics) {
		this.localMusics = localMusics;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new InnerDataThread(this).start();
		x.Ext.init(this);
		x.Ext.setDebug(true);
	}

	private class InnerDataThread extends Thread {
		Context context;

		public InnerDataThread(Context context) {
			super();
			this.context = context;
		}

		@Override
		public void run() {
			super.run();
			File file = new File(context.getFilesDir().toString(),
					"project.properties");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Properties properties = new Properties();
			try {
				properties.load(context.openFileInput("project.properties"));
				OutputStream out = context.openFileOutput("project.properties",
						Context.MODE_PRIVATE);
				Enumeration<?> e = properties.propertyNames();
				if (e.hasMoreElements()) {
					while (e.hasMoreElements()) {
						String s = (String) e.nextElement();
						if (!s.equals("filepath")) {
							properties
									.setProperty(s, properties.getProperty(s));
						}
					}
				}
				properties.setProperty("filepath", context.getFilesDir()
						.toString());
				properties.store(out, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean isFirstRun = sp.getBoolean("isFirstRun", true);
			/*if (isFirstRun) {
				DataBaseDao.createMusicDatabase(getContentResolver());
			}
			File file2 = new File("/data/cloudmusic/pic");
			file2.mkdirs();

			localMusics = MusicBiz.getLocalMusic();
			onPlayMusics = MusicBiz.getOnPlayMusic();*/
		}
	}
}
