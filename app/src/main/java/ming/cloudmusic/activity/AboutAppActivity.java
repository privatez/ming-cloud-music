package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.listener.FindListener;
import ming.cloudmusic.R;
import ming.cloudmusic.db.Server;
import ming.cloudmusic.model.AppUpdate;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.CustomUtils;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/24.
 */
public class AboutAppActivity extends DefalutBaseActivity implements View.OnClickListener {

    private static final int MAX_CLICK = 5;

    private TextView mTvUpdate;

    private int mClickCount;
    private long mLastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutapp);

        initView();
        initData();
    }

    @Override
    public void initView() {
        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        mTvUpdate = (TextView) findViewById(R.id.tv_update);

        boolean isAdmin = mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.ISADMIN, false);

        if (isAdmin) {
            findViewById(R.id.ll_upload_music).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_upload_apk).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.tv_update).setOnClickListener(this);
        findViewById(R.id.tv_developer).setOnClickListener(this);
        findViewById(R.id.ll_upload_music).setOnClickListener(this);
        findViewById(R.id.ll_upload_apk).setOnClickListener(this);

        tvVersion.setText("当前版本 " + CustomUtils.getVersionName(mContext));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_update:
                checkUpdate();
                break;
            case R.id.tv_developer:
                jumpToDeveloper();
                break;
            case R.id.ll_upload_apk:
                startActivity(new Intent(mContext, DeveloperActivity.class));
                break;
        }
    }

    private void checkUpdate() {
        mTvUpdate.setEnabled(false);

        FindListener findListener = new FindListener<AppUpdate>() {
            @Override
            public void onSuccess(List<AppUpdate> result) {
                mTvUpdate.setEnabled(true);
                if (result != null && result.size() > 0) {
                    updateApk(result.get(0));
                } else {
                    ToastUtils.showShort(mContext, "已经是最新版本");
                }
            }

            @Override
            public void onError(int code, String msg) {
                mTvUpdate.setEnabled(true);
                LogUtils.log("复合与查询失败：" + code + ",msg:" + msg);
            }
        };
        Server.checkUpdate(mContext, findListener);
    }

    private void jumpToDeveloper() {

        mClickCount++;

        if (mClickCount == MAX_CLICK) {
            startActivity(new Intent(mContext, DeveloperActivity.class));
            mClickCount = 0;
            return;
        }

        if (mClickCount == 1)
            mLastClickTime = System.currentTimeMillis();
        else {
            if (System.currentTimeMillis() - mLastClickTime < 1000)
                mLastClickTime = System.currentTimeMillis();
            else
                mClickCount = 0;
        }
    }


    private void updateApk(AppUpdate app) {
        LogUtils.log(app.toString());
    }

}
