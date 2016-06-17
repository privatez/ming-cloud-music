package ming.download;

/**
 * Created by private on 16/5/28.
 */
public interface FileDownloadCallback {

    void onStart();

    void onProgress(float downloaded, long total);

    void onDone();

    void onFailure();
}
