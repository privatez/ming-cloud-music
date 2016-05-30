package update;

import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.tls.OkHostnameVerifier;

import java.io.File;
import java.net.ProxySelector;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;


/**
 * Created by private on 16/5/28.
 */
public class OkHttpManager {

    private static final List<Protocol> DEFAULT_PROTOCOLS = Util.immutableList(
            Protocol.HTTP_2, Protocol.SPDY_3, Protocol.HTTP_1_1);

    private static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS = Util.immutableList(
            ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT);

    private static OkHttpManager sOkHttpManager;
    private OkHttpClient okHttpClient;

    private OkHttpManager() {

    }

    public static OkHttpManager getInstance() {
        if (sOkHttpManager == null) {
            sOkHttpManager = new OkHttpManager();
        }
        return sOkHttpManager;
    }

    public OkHttpClient getDefaultOkHttpClient() {

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
            okHttpClient.setProtocols(DEFAULT_PROTOCOLS);
            okHttpClient.setConnectionSpecs(DEFAULT_CONNECTION_SPECS);
            okHttpClient.setProxySelector(ProxySelector.getDefault());
            okHttpClient.setSocketFactory(SocketFactory.getDefault());
            okHttpClient.setHostnameVerifier(OkHostnameVerifier.INSTANCE);
            okHttpClient.setCertificatePinner(CertificatePinner.DEFAULT);
            okHttpClient.setConnectionPool(ConnectionPool.getDefault());
            okHttpClient.setFollowRedirects(true);
            okHttpClient.setFollowSslRedirects(true);
            okHttpClient.setRetryOnConnectionFailure(true);
            okHttpClient.setConnectTimeout(10000, TimeUnit.MILLISECONDS);
            okHttpClient.setWriteTimeout(10000, TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(10000, TimeUnit.MILLISECONDS);
        }

        return okHttpClient;

    }

    public static void download(String url, File target, FileDownloadCallback callback) {
        if (url != null && url.length() > 0 && target != null) {
            FileDownloadTask task = new FileDownloadTask(url, target, callback);
            task.execute();
        }
    }

}
