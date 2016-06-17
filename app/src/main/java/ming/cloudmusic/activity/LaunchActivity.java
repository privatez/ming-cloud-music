package ming.cloudmusic.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.List;

import cn.bmob.v3.listener.FindListener;
import ming.cloudmusic.R;
import ming.cloudmusic.api.BombServer;
import ming.cloudmusic.model.AppUpdate;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.LogUtils;
import ming.update.UpdateDialog;

/**
 * Created by Lhy on 2016/3/5.
 */
public class LaunchActivity extends DrawableBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        checkUpdate();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    /**
     * 检查登录以判断页面跳转
     */
    private void checkLoginStatus() {
        final boolean asVisitorLogin = mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_VISITOR_LOGGED, false);
        final boolean asUserLogin = mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, false);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (asVisitorLogin || asUserLogin) {
                    intent = new Intent(mContext, CloudMusicMainActivity.class);
                } else {
                    intent = new Intent(mContext, EntranceActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        FindListener findListener = new FindListener<AppUpdate>() {
            @Override
            public void onSuccess(List<AppUpdate> result) {
                if (result != null && result.size() > 0) {
                    showConfirmDialog(result.get(0));
                } else {
                    checkLoginStatus();
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.log("复合与查询失败：" + code + ",msg:" + msg);
                checkLoginStatus();
            }
        };
        BombServer.checkUpdate(mContext, findListener);
    }

    private void showConfirmDialog(final AppUpdate appUpdate) {
        new AlertDialog.Builder(mContext).setTitle("音乐播放器第 " + appUpdate.getVersionCode() + " 版更新啦!")//设置对话框标题
                .setMessage("确定要更新吗?")//设置显示的内容
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮

                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        updateApk(appUpdate);
                    }

                }).setNegativeButton("暂时不", new DialogInterface.OnClickListener() {//添加返回按钮

            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkLoginStatus();
            }

        }).show();//在按键响应事件中显示此对话框
    }

    private void updateApk(AppUpdate app) {
        UpdateDialog dialog = new UpdateDialog(mContext);
        String title = "音乐播放器第 " + app.getVersionCode() + " 版更新啦!";
        dialog.init(title, app.getChangeLog(), app.getUrl(), "private");
    }

}
