package ming.cloudmusic;

import android.app.Application;

import org.xutils.x;

import cn.bmob.v3.Bmob;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.MusicsManager;

public class MusicPlayerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化xUtil
        x.Ext.init(this);
        x.Ext.setDebug(true);

        //初始化Bomb
        Bmob.initialize(this, Constant.BMOB_ID);

        //初始化数据
        MusicsManager.getInstance().init(getApplicationContext());

    }

}
