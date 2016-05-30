package update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import me.imid.swipebacklayout.lib.R;

/**
 * reconstuct by private on 16/5/28.
 */
public class UpdateDialog extends AlertDialog {

    private Button btn_submit;
    private LinearLayout ll_progress;
    private TextView tv_progress;
    private ProgressBar pb_progress;
    private TextView tv_title;
    private TextView tv_msg;

    public UpdateDialog(Context context) {
        super(context);
    }

    public void init(String title, String content, final String downloadUrl, String appName) {

        final String localPath = Environment.getExternalStorageDirectory() + "/download/" + appName + ".apk";

        show();

        setContentView(R.layout.widget_dialog_update);
        setCancelable(true);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);


        tv_title.setText(title); //更新对话框标题
        tv_msg.setText(content); //更新对话框内容

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_submit.setVisibility(View.GONE);
                ll_progress.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.VISIBLE);


                FileDownloadCallback callback = new FileDownloadCallback() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onProgress(float downloaded, long total) {
                        super.onProgress(downloaded, total);
                        pb_progress.setMax((int) (total / 1000000.0));
                        tv_progress.setText(String.format("%.1fMB/%.1fMB", downloaded / 1000000.0, (float) (total / 1000000.0)));
                        pb_progress.setProgress((int) (downloaded / 1000000.0));
                    }

                    @Override
                    public void onFailure() {
                        super.onFailure();
                        btn_submit.setVisibility(View.VISIBLE);
                        ll_progress.setVisibility(View.GONE);
                        pb_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onDone() {
                        super.onDone();
                        btn_submit.setVisibility(View.VISIBLE);
                        ll_progress.setVisibility(View.GONE);
                        pb_progress.setVisibility(View.GONE);
                        installApk(localPath);
                    }
                };

                downloadFile(downloadUrl, new File(localPath), callback);

            }
        });
    }

    private void downloadFile(String url, File target, FileDownloadCallback callback) {
        if (url == null || url.length() <= 0 || target == null || callback == null) {
            return;
        }
        OkHttpManager.download(url, target, callback);
    }

    private void installApk(String apkPath) {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);
        promptInstall.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        getContext().startActivity(promptInstall);
    }

}
