package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import ming.cloudmusic.R;
import ming.cloudmusic.api.BombServer;
import ming.cloudmusic.model.AppUpdate;
import ming.cloudmusic.model.CloudDbmusic;
import ming.cloudmusic.model.DbMusic;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.CustomUtils;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.MusicsManager;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/24.
 */
public class AboutAppActivity extends DefalutBaseActivity implements View.OnClickListener {

    private static final int REQ_FILE_MANAGER = 1;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_FILE_MANAGER) {
                findMusicByPath(data.getData().getPath());
            }
        }
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
                //jumpToDeveloper();
                break;
            case R.id.ll_upload_apk:
                startActivity(new Intent(mContext, ReleaseApkActivity.class));
                break;
            case R.id.ll_upload_music:
                CustomUtils.startFileManager(this, REQ_FILE_MANAGER);
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
        BombServer.checkUpdate(mContext, findListener);
    }

    private void jumpToDeveloper() {

        mClickCount++;

        if (mClickCount == MAX_CLICK) {
            startActivity(new Intent(mContext, ReleaseApkActivity.class));
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

    private void findMusicByPath(String musicPath) {
        MusicsManager musicsManager = MusicsManager.getInstance();
        List<DbMusic> musics = musicsManager.getLocalMusics();
        for (DbMusic music : musics) {
            if (musicPath.equals(music.getPath())) {
                uploadMusic(music);
                return;
            }
        }
    }

    private void uploadMusic(final DbMusic music) {
        ToastUtils.showShort(mContext, "正在上传...");

        final BmobFile bmobFile = new BmobFile(new File(music.getPath()));

        bmobFile.uploadblock(mContext, new UploadFileListener() {
            @Override
            public void onSuccess() {
                CloudDbmusic dbmusic = new CloudDbmusic();
                dbmusic.setName(music.getName());
                dbmusic.setTitle(music.getTitle());
                dbmusic.setAlbum(music.getAlbum());
                dbmusic.setName(music.getName());
                dbmusic.setDuration(music.getDuration());
                dbmusic.setUrl(bmobFile.getFileUrl(mContext));
                updateMusicDb(dbmusic);
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.log("code:" + i + "....msg:" + s);
                if (i == BombServer.COED_NETWORK_ERROR) {
                    ToastUtils.showShort(mContext, "网络异常，上传失败...");
                } else {
                    ToastUtils.showShort(mContext, "上传失败...");
                }
            }
        });
    }

    private void updateMusicDb(final CloudDbmusic music) {
        music.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                ToastUtils.showLong(mContext, music.getName() + " 上传成功");
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showLong(mContext, music.getName() + " 上传失败");
                LogUtils.log("上传失败：" + i + ",msg:" + s);
            }
        });
    }

}
