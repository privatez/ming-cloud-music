package update;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by private on 16/5/28.
 */
public class FileDownloadTask extends AsyncTask<Void, Long, Boolean> {

    private OkHttpClient okHttpClient;
    private FileDownloadCallback callback;
    private String url;
    private File targetFile;

    public FileDownloadTask(String url, File target, FileDownloadCallback callback) {
        this.url = url;
        this.okHttpClient = OkHttpManager.getInstance().getDefaultOkHttpClient();
        this.callback = callback;
        this.targetFile = target;

        FileUtils.mkdirs(target.getParentFile());
        if (target.exists()) {
            target.delete();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onStart();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        final Request request = new Request.Builder()
                .url(url)
                .build();

        boolean suc = false;
        try {
            Response response = okHttpClient.newCall(request).execute();
            long total = response.body().contentLength();
            saveFile(response);
            if (total == targetFile.length()) {
                suc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            suc = false;
        }

        return suc;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (callback != null && values != null && values.length >= 2) {
            long downloaded = values[0];
            long total = values[1];
            callback.onProgress(downloaded, total);
        }
    }

    @Override
    protected void onPostExecute(Boolean suc) {
        super.onPostExecute(suc);
        if (callback != null) {
            if (suc) {
                callback.onDone();
            } else {
                callback.onFailure();
            }
        }
    }

    public String saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;

            FileUtils.mkdirs(targetFile.getParentFile());

            fos = new FileOutputStream(targetFile);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);

                if (callback != null) {
                    publishProgress(sum, total);
                }
            }
            fos.flush();

            return targetFile.getAbsolutePath();
        } finally {
            try {
                if (is != null) { is.close(); }
            } catch (IOException e) {
            }
            try {
                if (fos != null) { fos.close(); }
            } catch (IOException e) {
            }
        }
    }

}
