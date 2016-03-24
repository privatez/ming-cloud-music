package ming.cloudmusic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import ming.cloudmusic.R;
import ming.cloudmusic.model.AppUpdate;
import ming.cloudmusic.util.CustomUtils;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/24.
 */
public class DeveloperActivity extends DefalutBaseActivity implements View.OnClickListener {


    private TextView tvUpload;
    private NumberProgressBar pbUpload;
    private TextView tvSubmit;
    private EditText etVersionCode;
    private EditText etRemark;

    private String mApkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        initView();
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
                uploadApk(etVersionCode.getText().toString().trim());
                break;
            case R.id.tv_submit:
                uploadUpdateInfo();
                break;
        }
    }

    private void uploadApk(String versionCode) {
        if (etVersionCode.getText().toString().length() == 0) {
            ToastUtils.showShort(mContext, "请先输入版本号");
            return;
        }

        ToastUtils.showShort(mContext, "正在上传...");
        tvUpload.setText("正在上传..");
        tvUpload.setEnabled(false);

        String picPath = "sdcard/cloudmusic/cloud-music-" + versionCode + ".apk";
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(mContext, new UploadFileListener() {

            @Override
            public void onSuccess() {
                tvUpload.setText("上传成功..");
                mApkUrl = bmobFile.getFileUrl(mContext);
                ToastUtils.showShort(mContext, "上传成功...");
                //LogUtils.log("上传成功" + mApkUrl);
            }

            @Override
            public void onProgress(Integer value) {
                LogUtils.log(value + "上传中");
                pbUpload.setProgress(value);
            }

            @Override
            public void onFailure(int code, String msg) {
                tvUpload.setText("上传文件..");
                tvUpload.setEnabled(true);
                LogUtils.log("code:" + code + "....msg:" + msg);
            }
        });
    }

    private void uploadUpdateInfo() {
        tvSubmit.setEnabled(false);
        AppUpdate app = new AppUpdate();
        app.setVersionCode(Integer.parseInt(etVersionCode.getText().toString().trim()));
        app.setVersionName(CustomUtils.getVersionName(mContext));
        app.setChangeLog(etRemark.getText().toString().trim());
        app.setUrl(mApkUrl);
        app.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                tvSubmit.setEnabled(true);
                ToastUtils.showLong(mContext, CustomUtils.getVersionName(mContext) + " 发布成功");
            }

            @Override
            public void onFailure(int i, String s) {
                tvSubmit.setEnabled(true);
                LogUtils.log("发布失败：" + i + ",msg:" + s);
            }
        });
    }
}
