package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import ming.cloudmusic.R;
import ming.cloudmusic.model.AppUpdate;
import ming.cloudmusic.util.CustomUtils;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/24.
 */
public class AboutAppActivity extends DefalutBaseActivity implements View.OnClickListener {

    private static final int MAX_CLICK = 5;

    private TextView tvVersion;
    private TextView tvUpdate;
    private TextView tvDeveloper;

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
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvUpdate = (TextView) findViewById(R.id.tv_update);
        tvDeveloper = (TextView) findViewById(R.id.tv_developer);

        tvUpdate.setOnClickListener(this);
        tvDeveloper.setOnClickListener(this);

    }

    @Override
    public void initData() {
        tvVersion.setText("当前版本 " + CustomUtils.getVersionName(mContext));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_update:
                checkUpdate();
                break;
            case R.id.tv_developer:
                goDeveloper();
                break;
        }
    }

    private void checkUpdate() {
        tvUpdate.setEnabled(false);
        BmobQuery<AppUpdate> query = new BmobQuery<>();
        query.addWhereGreaterThan("versionCode", CustomUtils.getVersionCode(mContext));
        query.order("-updatedAt");
        query.findObjects(this, new FindListener<AppUpdate>() {
            @Override
            public void onSuccess(List<AppUpdate> result) {
                tvUpdate.setEnabled(true);
                if (result != null && result.size() > 0) {
                    updateApk(result.get(0));
                } else {
                    ToastUtils.showShort(mContext, "已经是最新版本");
                }
            }

            @Override
            public void onError(int code, String msg) {
                tvUpdate.setEnabled(true);
                LogUtils.log("复合与查询失败：" + code + ",msg:" + msg);
            }
        });
    }

    private void goDeveloper() {

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
