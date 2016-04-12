package ming.cloudmusic.server;

import android.content.Context;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import ming.cloudmusic.model.AppUpdate;
import ming.cloudmusic.util.CustomUtils;

/**
 * Created by Lhy on 2016/4/10.
 */
public class BombServer {

    public static final int COED_NETWORK_ERROR = 9016;

    public static void checkUpdate(Context context, FindListener<AppUpdate> findListener) {
        BmobQuery<AppUpdate> query = new BmobQuery<>();
        query.addWhereGreaterThan("versionCode", CustomUtils.getVersionCode(context));
        query.order("-updatedAt");
        query.findObjects(context, findListener);
    }

}
