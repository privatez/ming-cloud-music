package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import ming.cloudmusic.R;
import ming.cloudmusic.api.BombServer;
import ming.cloudmusic.model.AppUpdate;
import ming.cloudmusic.util.CustomUtils;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/24.
 */
public class ReleaseApkActivity extends DefalutBaseActivity implements View.OnClickListener {

    private static final int REQ_FILE_MANAGER = 1;

    private TextView tvUpload;
    private NumberProgressBar pbUpload;
    private TextView tvSubmit;
    private EditText etVersionCode;
    private EditText etRemark;

    private String mApkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_apk);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_FILE_MANAGER) {
                uploadApk(data.getData().getPath());
            }
        }
    }

    @Override
    public void initView() {
        tvUpload = (TextView) findViewById(R.id.tv_upload);
        pbUpload = (NumberProgressBar) findViewById(R.id.pb_upload);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        etVersionCode = (EditText) findViewById(R.id.et_version_code);
        etRemark = (EditText) findViewById(R.id.et_remark);

        tvUpload.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload:
                if (etVersionCode.getText().toString().length() == 0) {
                    ToastUtils.showShort(mContext, "请先输入版本号");
                } else {
                    tvUpload.setEnabled(false);
                    CustomUtils.startFileManager(this, REQ_FILE_MANAGER);
                }
                break;
            case R.id.tv_submit:
                if (etVersionCode.getText().toString().trim().length() > 0)
                    uploadUpdateInfo(Integer.parseInt(etVersionCode.getText().toString().trim()));
                else
                    ToastUtils.showShort(mContext, "请先输入版本号");
                break;
        }
    }

    private void uploadApk(String apkPath) {
        ToastUtils.showShort(mContext, "正在上传...");

        final BmobFile bmobFile = new BmobFile(new File(apkPath));
        bmobFile.uploadblock(mContext, new UploadFileListener() {

            @Override
            public void onSuccess() {
                mApkUrl = bmobFile.getFileUrl(mContext);
                ToastUtils.showShort(mContext, "上传成功...");
            }

            @Override
            public void onProgress(Integer value) {
                LogUtils.log(value + "上传中");
                pbUpload.setProgress(value);
            }

            @Override
            public void onFailure(int code, String msg) {
                tvUpload.setEnabled(true);
                if (code == BombServer.COED_NETWORK_ERROR) {
                    ToastUtils.showShort(mContext, "网络异常，上传失败...");
                } else {
                    ToastUtils.showShort(mContext, "上传失败...");
                }
                LogUtils.log("code:" + code + "....msg:" + msg);
            }
        });
    }

    private void uploadUpdateInfo(final int versionCode) {

        if (TextUtils.isEmpty(mApkUrl)) {
            ToastUtils.showShort(mContext, "请先上传apk");
            return;
        }

        tvSubmit.setEnabled(false);
        AppUpdate app = new AppUpdate();
        app.setVersionCode(versionCode);
        app.setChangeLog(etRemark.getText().toString().trim());
        app.setUrl(mApkUrl);
        app.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                tvSubmit.setEnabled(true);
                ToastUtils.showLong(mContext, "版本 " + versionCode + " 发布成功");
            }

            @Override
            public void onFailure(int i, String s) {
                tvSubmit.setEnabled(true);
                LogUtils.log("发布失败：" + i + ",msg:" + s);
            }
        });
    }
}
